# This script can be used to build and deploy kubernetes controllers. It is required to implement the controller
# specific targets `manifests` and `generate`:
#
# Examples:
#
#.PHONY: manifests
#manifests: controller-gen ## Generate WebhookConfiguration, ClusterRole and CustomResourceDefinition objects.
#	@echo "Generate manifests..."
#	@$(CONTROLLER_GEN) rbac:roleName=manager-role crd webhook paths="./..." output:crd:artifacts:config=config/crd/bases
#
#.PHONY: generate
#generate: controller-gen ## Generate code containing DeepCopy, DeepCopyInto, and DeepCopyObject method implementations.
#	@echo "Auto-generate deepcopy functions..."
#	@$(CONTROLLER_GEN) object:headerFile="hack/boilerplate.go.txt" paths="./..."

# This script requires the k8s.mk script
include $(WORKDIR)/build/make/k8s.mk

## Variables

# Setting SHELL to bash allows bash commands to be executed by recipes.
# This is a requirement for 'setup-envtest.sh' in the test target.
# Options are set to exit when a recipe line exits non-zero or a piped command fails.
SHELL = /usr/bin/env bash -o pipefail
.SHELLFLAGS = -ec

# make sure to create a statically linked binary otherwise it may quit with
# "exec user process caused: no such file or directory"
GO_BUILD_FLAGS=-mod=vendor -a -tags netgo,osusergo $(LDFLAGS) -o $(BINARY)

# remove DWARF symbol table and strip other symbols to shave ~13 MB from binary
ADDITIONAL_LDFLAGS=-extldflags -static -w -s

# ENVTEST_K8S_VERSION refers to the version of kubebuilder assets to be downloaded by envtest binary.
ENVTEST_K8S_VERSION = 1.23
K8S_INTEGRATION_TEST_DIR=${TARGET_DIR}/k8s-integration-test

##@ K8s - EcoSystem

.PHONY: build
build: image-import k8s-apply ## Builds a new version of the dogu and deploys it into the K8s-EcoSystem.

##@ Release

.PHONY: controller-release
controller-release: ## Interactively starts the release workflow.
	@echo "Starting git flow release..."
	@build/make/release.sh controller-tool

##@ K8s - Development

.PHONY: build-controller
build-controller: ${SRC} compile ## Builds the controller Go binary.

# Allows to perform tasks before locally running the controller
K8S_RUN_PRE_TARGETS ?=
.PHONY: run
run: manifests generate $(K8S_RUN_PRE_TARGETS) ## Run a controller from your host.
	go run -ldflags "-X main.Version=$(VERSION)" ./main.go

##@ K8s - Integration test with envtest

$(K8S_INTEGRATION_TEST_DIR):
	@mkdir -p $@

.PHONY: k8s-integration-test
k8s-integration-test: $(K8S_INTEGRATION_TEST_DIR) manifests generate envtest ## Run k8s integration tests.
	@echo "Running K8s integration tests..."
	@KUBEBUILDER_ASSETS="$(shell $(ENVTEST) use $(ENVTEST_K8S_VERSION) -p path)" go test -tags=k8s_integration ./... -coverprofile ${K8S_INTEGRATION_TEST_DIR}/report-k8s-integration.out

##@ K8s - Controller Resource

# The pre generation script creates a K8s resource yaml containing generated manager yaml.
.PHONY: k8s-create-temporary-resource
 k8s-create-temporary-resource: $(K8S_RESOURCE_TEMP_FOLDER) manifests kustomize
	@echo "Generating temporary k8s resources $(K8S_RESOURCE_TEMP_YAML)..."
	cd $(WORKDIR)/config/manager && $(KUSTOMIZE) edit set image controller=$(IMAGE)
	$(KUSTOMIZE) build config/default > $(K8S_RESOURCE_TEMP_YAML)
	@echo "Done."

##@ K8s - Download Kubernetes Utility Tools

CONTROLLER_GEN = $(UTILITY_BIN_PATH)/controller-gen
.PHONY: controller-gen
controller-gen: ## Download controller-gen locally if necessary.
	$(call go-get-tool,$(CONTROLLER_GEN),sigs.k8s.io/controller-tools/cmd/controller-gen@v0.11.3)

KUSTOMIZE = $(UTILITY_BIN_PATH)/kustomize
.PHONY: kustomize
kustomize: ## Download kustomize locally if necessary.
	$(call go-get-tool,$(KUSTOMIZE),sigs.k8s.io/kustomize/kustomize/v4@v4.5.7)

ENVTEST = $(UTILITY_BIN_PATH)/setup-envtest
.PHONY: envtest
envtest: ## Download envtest-setup locally if necessary.
	$(call go-get-tool,$(ENVTEST),sigs.k8s.io/controller-runtime/tools/setup-envtest@latest)