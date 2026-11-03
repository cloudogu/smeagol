# Variables
# Path to the dogu json of the dogu
DOGU_JSON_FILE=${WORKDIR}/dogu.json
DOGU_JSON_DEV_FILE=${WORKDIR}/${TARGET_DIR}/dogu.json
# Name of the dogu is extracted from the dogu.json
ARTIFACT_ID=$(shell $(BINARY_YQ) -oy -e ".Name" $(DOGU_JSON_FILE) | sed "s|.*/||g")
# Namespace of the dogu is extracted from the dogu.json
ARTIFACT_NAMESPACE=$(shell $(BINARY_YQ) -oy -e ".Name" $(DOGU_JSON_FILE) | sed "s|/.*||g")
# Version of the dogu is extracted from the dogu.json
VERSION=$(shell $(BINARY_YQ) -oy -e ".Version" $(DOGU_JSON_FILE))
# Image of the dogu is extracted from the dogu.json
IMAGE=$(shell $(BINARY_YQ) -oy -e ".Image" $(DOGU_JSON_FILE)):$(VERSION)

PRE_BUILD_TARGETS ?=

ifeq (${K8S_MK_INCLUDE_MARKER}, )
	include ${BUILD_DIR}/make/k8s.mk
endif

# Immutable dev tags: when STAGE=development every `make build` produces a unique, non-overwriting image tag.
# It is placed in the integer "extra" slot (after the single hyphen) that
# cesapp-lib's core.ParseVersion expects, replacing any existing packaging revision: e.g. 3.12.1-2 -> 3.12.1-<ts>.
DOGU_BUILD_VERSION := $(shell date +%s)
DOGU_VERSION_BASE := $(firstword $(subst -, ,$(VERSION)))
DOGU_DEV_VERSION ?= $(DOGU_VERSION_BASE)-$(DOGU_BUILD_VERSION)
ifeq (${STAGE}, development)
	DOGU_TARGET_VERSION = $(DOGU_DEV_VERSION)
else
	DOGU_TARGET_VERSION = $(VERSION)
endif
# Dogu-scoped override of the shared dev image tags from k8s.mk so only dogu builds get immutable tags.
IMAGE_DEV_VERSION = $(IMAGE_DEV):$(DOGU_TARGET_VERSION)
IMAGE_DEV_PUSH_VERSION = $(IMAGE_DEV_PUSH):$(DOGU_TARGET_VERSION)
$(info DOGU_TARGET_VERSION=$(DOGU_TARGET_VERSION))

##@ K8s - EcoSystem

.PHONY: build
build: ${PRE_BUILD_TARGETS} image-import install-dogu-descriptor create-dogu-resource apply-dogu-resource ## Builds a new version of the dogu and deploys it into the K8s-EcoSystem.

##@ K8s - Dogu - Resource

# The additional k8s yaml files
K8S_RESOURCE_PRODUCTIVE_FOLDER ?= $(WORKDIR)/k8s
K8S_RESOURCE_PRODUCTIVE_YAML ?= $(K8S_RESOURCE_PRODUCTIVE_FOLDER)/$(ARTIFACT_ID).yaml
K8S_RESOURCE_DOGU_CR_TEMPLATE_YAML ?= $(BUILD_DIR)/make/k8s-dogu.tpl
K8S_RESOURCE_DOGU ?= $(K8S_RESOURCE_TEMP_FOLDER)/$(ARTIFACT_ID).yaml
# The pre generation script creates a k8s resource yaml containing the dogu crd and the content from the k8s folder.
.PHONY: create-dogu-resource
create-dogu-resource: ${BINARY_YQ} $(K8S_RESOURCE_TEMP_FOLDER)
	@echo "Generating temporary K8s resources $(K8S_RESOURCE_DOGU)..."
	@rm -f $(K8S_RESOURCE_DOGU)
	@sed "s|NAMESPACE|$(ARTIFACT_NAMESPACE)|g" $(K8S_RESOURCE_DOGU_CR_TEMPLATE_YAML) | sed "s|NAME|$(ARTIFACT_ID)|g"  | sed "s|VERSION|$(DOGU_TARGET_VERSION)|g" >> $(K8S_RESOURCE_DOGU)
	@echo "Done."

.PHONY: apply-dogu-resource
apply-dogu-resource:
	@kubectl --context="${KUBE_CONTEXT_NAME}" --namespace=${NAMESPACE} apply -f "$(K8S_RESOURCE_DOGU)"

##@ K8s - Dogu

.PHONY: install-dogu-descriptor
install-dogu-descriptor: ${BINARY_YQ} $(TARGET_DIR) ## Installs a configmap with current dogu.json into the cluster.
	@echo "Generate configmap from dogu.json..."
	@$(BINARY_YQ) -oj ".Image=\"${IMAGE_DEV}\" | .Version=\"${DOGU_TARGET_VERSION}\""  ${DOGU_JSON_FILE} > ${DOGU_JSON_DEV_FILE}
	@kubectl --context="${KUBE_CONTEXT_NAME}" create configmap "$(ARTIFACT_ID)-descriptor" --from-file=$(DOGU_JSON_DEV_FILE) --dry-run=client -o yaml | kubectl --context="${KUBE_CONTEXT_NAME}" --namespace=${NAMESPACE} apply -f -
	@echo "Done."

.PHONY: restart-dogu
restart-dogu:
	@echo "Restarting ${ARTIFACT_ID} Dogu!"
	@printf '%s\n' \
		'apiVersion: k8s.cloudogu.com/v2' \
		'kind: DoguRestart' \
		'metadata:' \
		'  generateName: ${ARTIFACT_ID}-restart-' \
		'spec:' \
		'  doguName: ${ARTIFACT_ID}' \
		| kubectl -n ${NAMESPACE} create -f -
