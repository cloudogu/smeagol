# we set this default to maintain compatibility with CRDs that are still inside monorepos
APPEND_CRD_SUFFIX ?= true
ifeq ($(APPEND_CRD_SUFFIX), true)
	ARTIFACT_CRD_ID = $(ARTIFACT_ID)-crd
else ifeq ($(APPEND_CRD_SUFFIX), false)
	ARTIFACT_CRD_ID = $(ARTIFACT_ID)
endif
DEV_CRD_VERSION ?= ${VERSION}-dev
HELM_CRD_SOURCE_DIR ?= ${WORKDIR}/k8s/helm-crd
HELM_CRD_TARGET_DIR ?= $(K8S_RESOURCE_TEMP_FOLDER)/helm-crd
HELM_CRD_RELEASE_TGZ = ${HELM_CRD_TARGET_DIR}/${ARTIFACT_CRD_ID}-${VERSION}.tgz
HELM_CRD_DEV_RELEASE_TGZ = ${HELM_CRD_TARGET_DIR}/${ARTIFACT_CRD_ID}-${DEV_CRD_VERSION}.tgz

K8S_RESOURCE_CRD_COMPONENT ?= "${K8S_RESOURCE_TEMP_FOLDER}/component-${ARTIFACT_CRD_ID}-${VERSION}.yaml"
K8S_RESOURCE_COMPONENT_CR_TEMPLATE_YAML ?= $(BUILD_DIR)/make/k8s-component.tpl
# CRD_POST_MANIFEST_TARGETS can be used to post-process CRD YAMLs after their creation.
CRD_POST_MANIFEST_TARGETS ?= crd-add-labels

# This can be used by external components to prevent generate and copy controller manifests by overriding with an empty value.
CRD_HELM_MANIFEST_TARGET?=manifests

##@ K8s - CRD targets

.PHONY: manifests
manifests: ${CONTROLLER_GEN} manifests-run ${CRD_POST_MANIFEST_TARGETS} ## Generate CustomResourceDefinition YAMLs.

.PHONY: manifests-run
manifests-run:
	@echo "Generate manifests..."
	@$(CONTROLLER_GEN) crd paths="./..." output:crd:artifacts:config=${HELM_CRD_SOURCE_DIR}/templates

.PHONY: crd-add-labels
crd-add-labels: $(BINARY_YQ)
	@echo "Adding labels to CRD..."
	@for file in ${HELM_CRD_SOURCE_DIR}/templates/*.yaml ; do \
		$(BINARY_YQ) -i e ".metadata.labels.app = \"ces\"" $${file} ;\
		$(BINARY_YQ) -i e ".metadata.labels.\"app.kubernetes.io/name\" = \"${ARTIFACT_CRD_ID}\"" $${file} ;\
	done

.PHONY: crd-helm-generate ## Generates the Helm CRD chart
crd-helm-generate: ${CRD_HELM_MANIFEST_TARGET} validate-crd-chart ${HELM_CRD_TARGET_DIR}/Chart.yaml ${K8S_POST_CRD_HELM_GENERATE_TARGETS}

# this is phony because of it is easier this way than the makefile-single-run way
.PHONY: ${HELM_CRD_TARGET_DIR}/Chart.yaml
${HELM_CRD_TARGET_DIR}/Chart.yaml: ${K8S_RESOURCE_TEMP_FOLDER}
	@echo "Copying Helm CRD files..."
	@rm -drf ${HELM_CRD_TARGET_DIR}/templates
	@mkdir -p ${HELM_CRD_TARGET_DIR}/templates
	@cp -r ${HELM_CRD_SOURCE_DIR}/** ${HELM_CRD_TARGET_DIR}

	@echo "Generate Helm CRD chart..."
	@sed -i 's/name: artifact-crd-replaceme/name: ${ARTIFACT_CRD_ID}/' ${HELM_CRD_TARGET_DIR}/Chart.yaml
	@if [[ ${STAGE} == "development" ]]; then \
	  sed -i 's/appVersion: "0.0.0-replaceme"/appVersion: "${DEV_CRD_VERSION}"/' ${HELM_CRD_TARGET_DIR}/Chart.yaml; \
      sed -i 's/version: 0.0.0-replaceme/version: ${DEV_CRD_VERSION}/' ${HELM_CRD_TARGET_DIR}/Chart.yaml; \
	else \
	  sed -i 's/appVersion: "0.0.0-replaceme"/appVersion: "${VERSION}"/' ${HELM_CRD_TARGET_DIR}/Chart.yaml; \
      sed -i 's/version: 0.0.0-replaceme/version: ${VERSION}/' ${HELM_CRD_TARGET_DIR}/Chart.yaml; \
	fi

.PHONY: validate-crd-chart
validate-crd-chart:
	@if [ ! -f ${HELM_CRD_SOURCE_DIR}/Chart.yaml ] ; then \
       echo "Could not find CRD source Helm chart under \$${HELM_CRD_SOURCE_DIR}/Chart.yaml" ; \
       exit 23 ; \
    fi

.PHONY: crd-helm-apply
crd-helm-apply: ${BINARY_HELM} check-k8s-namespace-env-var crd-helm-generate ## Generates and installs the Helm CRD chart.
	@echo "Apply generated Helm CRD chart"
	@${BINARY_HELM} --kube-context="${KUBE_CONTEXT_NAME}" upgrade -i ${ARTIFACT_CRD_ID} ${HELM_CRD_TARGET_DIR} ${BINARY_HELM_ADDITIONAL_UPGR_ARGS} --namespace ${NAMESPACE}

.PHONY: crd-helm-delete
crd-helm-delete: ${BINARY_HELM} check-k8s-namespace-env-var ## Uninstalls the current Helm CRD chart.
	@echo "Uninstall Helm CRD chart"
	@${BINARY_HELM} --kube-context="${KUBE_CONTEXT_NAME}" uninstall ${ARTIFACT_CRD_ID} --namespace=${NAMESPACE} ${BINARY_HELM_ADDITIONAL_UNINST_ARGS} || true

.PHONY: crd-helm-package
crd-helm-package: crd-helm-delete-existing-tgz ${HELM_CRD_RELEASE_TGZ} ## Generates and packages the Helm CRD chart.

.PHONY: crd-helm-delete-existing-tgz
crd-helm-delete-existing-tgz: ## Remove an existing Helm CRD package.
	@rm -f ${HELM_CRD_TARGET_DIR}/${ARTIFACT_CRD_ID}-*.tgz

${HELM_CRD_RELEASE_TGZ}: ${BINARY_HELM} crd-helm-generate ## Generates and packages the Helm CRD chart.
	@echo "Package generated helm crd-chart"
	@${BINARY_HELM} package ${HELM_CRD_TARGET_DIR} -d ${HELM_CRD_TARGET_DIR} ${BINARY_HELM_ADDITIONAL_PACK_ARGS}

.PHONY: crd-helm-chart-import
crd-helm-chart-import: ${CHECK_VAR_TARGETS} check-k8s-artifact-id crd-helm-generate crd-helm-package ## Imports the currently available Helm CRD chart into the cluster-local registry.
	@if [[ ${STAGE} == "development" ]]; then \
		echo "Import ${HELM_CRD_DEV_RELEASE_TGZ} into K8s cluster ${CES_REGISTRY_HOST}..."; \
		${BINARY_HELM} push ${HELM_CRD_DEV_RELEASE_TGZ} oci://${CES_REGISTRY_HOST}/${HELM_ARTIFACT_NAMESPACE} ${BINARY_HELM_ADDITIONAL_PUSH_ARGS}; \
	else \
	  	echo "Import ${HELM_CRD_RELEASE_TGZ} into K8s cluster ${CES_REGISTRY_HOST}..."; \
        ${BINARY_HELM} push ${HELM_CRD_RELEASE_TGZ} oci://${CES_REGISTRY_HOST}/${HELM_ARTIFACT_NAMESPACE} ${BINARY_HELM_ADDITIONAL_PUSH_ARGS}; \
    fi
	@echo "Done."

.PHONY: crd-helm-lint
crd-helm-lint: $(BINARY_HELM) crd-helm-generate
	@$(BINARY_HELM) lint "${HELM_CRD_TARGET_DIR}"

.PHONY: crd-component-generate
crd-component-generate: ${K8S_RESOURCE_TEMP_FOLDER} ## Generate the CRD component YAML resource.
	@echo "Generating temporary K8s crd-component resource: ${K8S_RESOURCE_CRD_COMPONENT}"
	@if [[ ${STAGE} == "development" ]]; then \
		sed "s|NAMESPACE|$(HELM_ARTIFACT_NAMESPACE)|g" "${K8S_RESOURCE_COMPONENT_CR_TEMPLATE_YAML}" | sed "s|NAME|$(ARTIFACT_CRD_ID)|g"  | sed "s|VERSION|$(DEV_CRD_VERSION)|g" > "${K8S_RESOURCE_CRD_COMPONENT}"; \
	else \
		sed "s|NAMESPACE|$(HELM_ARTIFACT_NAMESPACE)|g" "${K8S_RESOURCE_COMPONENT_CR_TEMPLATE_YAML}" | sed "s|NAME|$(ARTIFACT_CRD_ID)|g"  | sed "s|VERSION|$(VERSION)|g" > "${K8S_RESOURCE_CRD_COMPONENT}"; \
	fi

.PHONY: crd-component-apply
crd-component-apply: check-k8s-namespace-env-var crd-helm-chart-import crd-component-generate ## Applies the CRD component YAML resource to the actual defined context.
	@kubectl apply -f "${K8S_RESOURCE_CRD_COMPONENT}" --namespace="${NAMESPACE}" --context="${KUBE_CONTEXT_NAME}"
	@echo "Done."

.PHONY: crd-component-delete
crd-component-delete: check-k8s-namespace-env-var crd-component-generate ## Deletes the CRD component YAML resource from the actual defined context.
	@kubectl delete -f "${K8S_RESOURCE_CRD_COMPONENT}" --namespace="${NAMESPACE}" --context="${KUBE_CONTEXT_NAME}" || true
	@echo "Done."
