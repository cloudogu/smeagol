COMPONENT_DEV_VERSION?=${VERSION}-dev

include ${BUILD_DIR}/make/k8s.mk

ifeq (${RUNTIME_ENV}, local)
	BINARY_HELM_ADDITIONAL_PUSH_ARGS?=--plain-http
endif
BINARY_HELM_ADDITIONAL_PACK_ARGS?=
BINARY_HELM_ADDITIONAL_UNINST_ARGS?=
BINARY_HELM_ADDITIONAL_UPGR_ARGS?=

HELM_TARGET_DIR ?= $(K8S_RESOURCE_TEMP_FOLDER)/helm
HELM_SOURCE_DIR ?= k8s/helm
HELM_RELEASE_TGZ=${HELM_TARGET_DIR}/${ARTIFACT_ID}-${VERSION}.tgz
HELM_DEV_RELEASE_TGZ=${HELM_TARGET_DIR}/${ARTIFACT_ID}-${COMPONENT_DEV_VERSION}.tgz
HELM_ARTIFACT_NAMESPACE?=k8s
ifeq (${RUNTIME_ENV}, remote)
	HELM_ARTIFACT_NAMESPACE?=testing/k8s
endif

K8S_RESOURCE_COMPONENT ?= "${K8S_RESOURCE_TEMP_FOLDER}/component-${ARTIFACT_ID}-${VERSION}.yaml"
K8S_RESOURCE_COMPONENT_CR_TEMPLATE_YAML ?= $(BUILD_DIR)/make/k8s-component.tpl
# HELM_PRE_GENERATE_TARGETS allows to execute targets that affect Helm source files AND Helm target files.
HELM_PRE_GENERATE_TARGETS ?=
# HELM_POST_GENERATE_TARGETS allows to execute targets that only affect Helm target files.
HELM_POST_GENERATE_TARGETS ?=
HELM_PRE_APPLY_TARGETS ?=
COMPONENT_PRE_APPLY_TARGETS ?=

# This can be used by components with own images to build and push to the dev registry.
# These components should override this variable with `image-import`.
IMAGE_IMPORT_TARGET?=

##@ K8s - Helm general
.PHONY: helm-init-chart
helm-init-chart: ${BINARY_HELM} ## Creates a Chart.yaml-template with zero values
	@echo "Initialize ${HELM_SOURCE_DIR}/Chart.yaml..."
	@mkdir -p ${HELM_SOURCE_DIR}/tmp/
	@${BINARY_HELM} create ${HELM_SOURCE_DIR}/tmp/${ARTIFACT_ID}
	@cp ${HELM_SOURCE_DIR}/tmp/${ARTIFACT_ID}/Chart.yaml ${HELM_SOURCE_DIR}/
	@rm -dr ${HELM_SOURCE_DIR}/tmp
	@sed -i 's/appVersion: ".*"/appVersion: "0.0.0-replaceme"/' ${HELM_SOURCE_DIR}/Chart.yaml
	@sed -i 's/version: .*/version: 0.0.0-replaceme/' ${HELM_SOURCE_DIR}/Chart.yaml

.PHONY: helm-generate
helm-generate: ${HELM_TARGET_DIR}/Chart.yaml ${HELM_POST_GENERATE_TARGETS} ## Generates the final helm chart.

# this is phony because of it is easier this way than the makefile-single-run way
.PHONY: ${HELM_TARGET_DIR}/Chart.yaml
${HELM_TARGET_DIR}/Chart.yaml: $(K8S_RESOURCE_TEMP_FOLDER) validate-chart ${HELM_PRE_GENERATE_TARGETS} copy-helm-files
	@echo "Generate Helm chart..."
	@if [[ ${STAGE} == "development" ]]; then \
  	  sed -i 's/appVersion: "0.0.0-replaceme"/appVersion: '$(COMPONENT_DEV_VERSION)'/' ${HELM_TARGET_DIR}/Chart.yaml; \
  	  sed -i 's/version: 0.0.0-replaceme/version:  '$(COMPONENT_DEV_VERSION)'/' ${HELM_TARGET_DIR}/Chart.yaml; \
  	else \
  	  sed -i 's/appVersion: "0.0.0-replaceme"/appVersion: "${VERSION}"/' ${HELM_TARGET_DIR}/Chart.yaml; \
      sed -i 's/version: 0.0.0-replaceme/version: ${VERSION}/' ${HELM_TARGET_DIR}/Chart.yaml; \
    fi

.PHONY: copy-helm-files
copy-helm-files:
	@echo "Copying Helm files..."
	@rm -drf ${HELM_TARGET_DIR}  # delete folder, so the chart is newly created.
	@mkdir -p ${HELM_TARGET_DIR}/templates
	@cp -r ${HELM_SOURCE_DIR}/** ${HELM_TARGET_DIR}

.PHONY: validate-chart
validate-chart:
	@if [ ! -f ${HELM_SOURCE_DIR}/Chart.yaml ] ; then \
       echo "Could not find source Helm chart under \$${HELM_SOURCE_DIR}/Chart.yaml" ; \
       exit 22 ; \
    fi

.PHONY: helm-update-dependencies
helm-update-dependencies: ${BINARY_HELM} ## Update Helm chart dependencies
	@$(BINARY_HELM) dependency update "${HELM_SOURCE_DIR}"

##@ K8s - Helm dev targets

.PHONY: helm-apply
helm-apply: ${BINARY_HELM} check-k8s-namespace-env-var ${IMAGE_IMPORT_TARGET} helm-generate ${HELM_PRE_APPLY_TARGETS} ## Generates and installs the Helm chart.
	@echo "Apply generated helm chart"
	@${BINARY_HELM} --kube-context="${KUBE_CONTEXT_NAME}" upgrade -i ${ARTIFACT_ID} ${HELM_TARGET_DIR} ${BINARY_HELM_ADDITIONAL_UPGR_ARGS} --namespace ${NAMESPACE}

.PHONY: helm-delete
helm-delete: ${BINARY_HELM} check-k8s-namespace-env-var ## Uninstalls the current Helm chart.
	@echo "Uninstall helm chart"
	@${BINARY_HELM} --kube-context="${KUBE_CONTEXT_NAME}" uninstall ${ARTIFACT_ID} --namespace=${NAMESPACE} ${BINARY_HELM_ADDITIONAL_UNINST_ARGS} || true

.PHONY: helm-reinstall
helm-reinstall: helm-delete helm-apply ## Uninstalls the current helm chart and reinstalls it.

.PHONY: helm-chart-import
helm-chart-import: ${CHECK_VAR_TARGETS} helm-generate helm-package ${IMAGE_IMPORT_TARGET} ## Imports the currently available chart into the cluster-local registry.
	@if [[ ${STAGE} == "development" ]]; then \
		echo "Import ${HELM_DEV_RELEASE_TGZ} into K8s cluster ${CES_REGISTRY_HOST}..."; \
		${BINARY_HELM} push ${HELM_DEV_RELEASE_TGZ} oci://${CES_REGISTRY_HOST}/${HELM_ARTIFACT_NAMESPACE} ${BINARY_HELM_ADDITIONAL_PUSH_ARGS}; \
	else \
	  	echo "Import ${HELM_RELEASE_TGZ} into K8s cluster ${CES_REGISTRY_HOST}..."; \
        ${BINARY_HELM} push ${HELM_RELEASE_TGZ} oci://${CES_REGISTRY_HOST}/${HELM_ARTIFACT_NAMESPACE} ${BINARY_HELM_ADDITIONAL_PUSH_ARGS}; \
    fi
	@echo "Done."

##@ K8s - Helm release targets

.PHONY: helm-generate-release
helm-generate-release: update-urls ## Generates the final helm chart with release URLs.


.PHONY: helm-package
helm-package: helm-delete-existing-tgz ${HELM_RELEASE_TGZ} ## Generates and packages the helm chart with release URLs.

${HELM_RELEASE_TGZ}: ${BINARY_HELM} ${HELM_TARGET_DIR}/Chart.yaml ${HELM_POST_GENERATE_TARGETS} ## Generates and packages the helm chart with release URLs.
	@echo "Package generated helm chart"
	@if [[ ${STAGE} == "development" ]]; then \
  		echo "WARNING: You are using a development environment" ; \
  	  fi
	@${BINARY_HELM} package ${HELM_TARGET_DIR} -d ${HELM_TARGET_DIR} ${BINARY_HELM_ADDITIONAL_PACK_ARGS}

.PHONY: helm-delete-existing-tgz
helm-delete-existing-tgz: ## Remove an existing Helm package from the target directory.
	@echo "Delete ${HELM_RELEASE_TGZ}*"
	@rm -f ${HELM_TARGET_DIR}/${ARTIFACT_ID}-*.tgz

##@ K8s - Helm lint targets

.PHONY: helm-lint
helm-lint: $(BINARY_HELM) helm-generate
	@$(BINARY_HELM) lint "${HELM_TARGET_DIR}"

##@ K8s - Component dev targets

.PHONY: component-generate
component-generate: ${K8S_RESOURCE_COMPONENT_CR_TEMPLATE_YAML} ${COMPONENT_POST_GENERATE_TARGETS} ## Generate the component yaml resource.

${K8S_RESOURCE_COMPONENT_CR_TEMPLATE_YAML}: ${K8S_RESOURCE_TEMP_FOLDER}
	@echo "Generating temporary K8s component resource: ${K8S_RESOURCE_COMPONENT}"
	@if [[ ${STAGE} == "development" ]]; then \
		sed "s|NAMESPACE|$(HELM_ARTIFACT_NAMESPACE)|g" "${K8S_RESOURCE_COMPONENT_CR_TEMPLATE_YAML}" | sed "s|NAME|$(ARTIFACT_ID)|g"  | sed "s|VERSION|$(COMPONENT_DEV_VERSION)|g" > "${K8S_RESOURCE_COMPONENT}"; \
	else \
		sed "s|NAMESPACE|$(HELM_ARTIFACT_NAMESPACE)|g" "${K8S_RESOURCE_COMPONENT_CR_TEMPLATE_YAML}" | sed "s|NAME|$(ARTIFACT_ID)|g"  | sed "s|VERSION|$(VERSION)|g" > "${K8S_RESOURCE_COMPONENT}"; \
	fi

.PHONY: component-apply
component-apply: check-k8s-namespace-env-var ${COMPONENT_PRE_APPLY_TARGETS} ${IMAGE_IMPORT_TARGET} helm-generate helm-chart-import component-generate ## Applies the component yaml resource to the actual defined context.
	@kubectl apply -f "${K8S_RESOURCE_COMPONENT}" --namespace="${NAMESPACE}" --context="${KUBE_CONTEXT_NAME}"
	@echo "Done."

.PHONY: component-delete
component-delete: check-k8s-namespace-env-var component-generate $(K8S_POST_GENERATE_TARGETS) ## Deletes the component yaml resource from the actual defined context.
	@kubectl delete -f "${K8S_RESOURCE_COMPONENT}" --namespace="${NAMESPACE}" --context="${KUBE_CONTEXT_NAME}" || true
	@echo "Done."

.PHONY: component-reinstall
component-reinstall: component-delete  component-apply ## Reinstalls the component yaml resource from the actual defined context.
