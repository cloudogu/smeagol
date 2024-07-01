# This script requires the k8s.mk script
include ${BUILD_DIR}/make/k8s-component.mk
include ${BUILD_DIR}/make/k8s-crd.mk

## Variables

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
build: helm-apply ## Builds a new version of the dogu and deploys it into the K8s-EcoSystem.

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
run: generate-deepcopy $(K8S_RUN_PRE_TARGETS) ## Run a controller from your host.
	go run -ldflags "-X main.Version=$(VERSION)" ./main.go

##@ K8s - Integration test with envtest

$(K8S_INTEGRATION_TEST_DIR):
	@mkdir -p $@

.PHONY: k8s-integration-test
k8s-integration-test: $(K8S_INTEGRATION_TEST_DIR) ${ENVTEST} ## Run k8s integration tests.
	@echo "Running K8s integration tests..."
	@KUBEBUILDER_ASSETS="$(shell $(ENVTEST) use $(ENVTEST_K8S_VERSION) -p path)" go test -tags=k8s_integration ./... -coverprofile ${K8S_INTEGRATION_TEST_DIR}/report-k8s-integration.out

##@ Controller specific targets

.PHONY: generate-deepcopy
generate-deepcopy: ${CONTROLLER_GEN} ## Generate code containing DeepCopy* method implementations.
	@echo "Auto-generate deepcopy functions..."
	@$(CONTROLLER_GEN) object:headerFile="hack/boilerplate.go.txt" paths="./..."
