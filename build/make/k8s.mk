# This file is optional and can be used to set personal information without committing them to the repository.
MY_ENV_FILE ?= $(WORKDIR)/.env
ifneq (,$(wildcard $(MY_ENV_FILE)))
    include .env
endif

## Variables

BINARY_YQ = $(UTILITY_BIN_PATH)/yq
BINARY_YQ_4_VERSION?=v4.40.3
BINARY_HELM = $(UTILITY_BIN_PATH)/helm
BINARY_HELM_VERSION?=v3.13.0
CONTROLLER_GEN = $(UTILITY_BIN_PATH)/controller-gen
CONTROLLER_GEN_VERSION?=v0.19.0

# Setting SHELL to bash allows bash commands to be executed by recipes.
# Options are set to exit when a recipe line exits non-zero or a piped command fails.
SHELL = /usr/bin/env bash -o pipefail
.SHELLFLAGS = -ec

# The productive tag of the image
IMAGE ?=

# Set production as default stage. Use "development" as stage in your .env file to generate artifacts
# with development images pointing to CES_REGISTRY_URL_PREFIX.
STAGE?=production

# Set the "local" as runtime-environment, to push images to the container-registry of the local cluster and to apply resources to the local cluster.
# Use "remote" as runtime-environment in your .env file to push images to the container-registry at "registry.cloudogu.com/testing" and to apply resources to the configured kubernetes-context in KUBE_CONTEXT_NAME.
RUNTIME_ENV?=local
$(info RUNTIME_ENV=$(RUNTIME_ENV))

# The host and port of the local cluster
K3S_CLUSTER_FQDN?=k3ces.local
K3S_LOCAL_REGISTRY_PORT?=30099

# The URL of the container-registry to use. Defaults to the registry of the local-cluster.
# If RUNTIME_ENV is "remote" it is "registry.cloudogu.com/testing", if ENVIRONMENT is "ci" it is "registry.cloudogu.com/ci"
# if run on ci (jenkins) the images must be pushed to a separate namespace in order to free space every night after the build.
CES_REGISTRY_HOST?=${K3S_CLUSTER_FQDN}:${K3S_LOCAL_REGISTRY_PORT}
CES_REGISTRY_NAMESPACE ?=
ifeq (${RUNTIME_ENV}, remote)
	CES_REGISTRY_HOST=registry.cloudogu.com
	CES_REGISTRY_NAMESPACE=/testing
	ifeq ($(ENVIRONMENT), ci)
		CES_REGISTRY_NAMESPACE=/ci
	endif
endif
$(info CES_REGISTRY_HOST=$(CES_REGISTRY_HOST))

# The name of the kube-context to use for applying resources.
# If KUBE_CONTEXT_NAME is empty and RUNTIME_ENV is "remote" the currently configured kube-context is used.
# If KUBE_CONTEXT_NAME is empty and RUNTIME_ENV is not "remote" the "k3ces.local" is used as kube-context.
ifeq (${KUBE_CONTEXT_NAME}, )
	ifeq (${RUNTIME_ENV}, remote)
		KUBE_CONTEXT_NAME = $(shell kubectl config current-context)
	else
		KUBE_CONTEXT_NAME = k3ces.local
	endif
endif
$(info KUBE_CONTEXT_NAME=$(KUBE_CONTEXT_NAME))

# The git branch-name in lowercase, shortened to 63 bytes, and with everything except 0-9 and a-z replaced with -. No leading / trailing -.
GIT_BRANCH := $(shell git rev-parse --abbrev-ref HEAD | tr '[:upper:]' '[:lower:]' | sed -E 's/[^a-z0-9]+/-/g; s/^-+|-+$$//g' | cut -c1-63)
# The short git commit-hash
GIT_HASH := $(shell git rev-parse --short HEAD)

## Image URL to use all building/pushing image targets
IMAGE_DEV?=$(CES_REGISTRY_HOST)$(CES_REGISTRY_NAMESPACE)/$(ARTIFACT_ID)/$(GIT_BRANCH)
IMAGE_DEV_VERSION=$(IMAGE_DEV):$(VERSION)

# Variables for the temporary yaml files. These are used as template to generate a development resource containing
# the current namespace and the dev image.
K8S_RESOURCE_TEMP_FOLDER ?= $(TARGET_DIR)/k8s

# This can be used by components with own images to check if all image env var are set.
# These components should override this variable with `check-all-vars`.
CHECK_VAR_TARGETS?=check-all-vars-without-image

##@ K8s - Variables

.PHONY: check-all-vars
check-all-vars: check-all-vars-without-image check-all-image-vars ## Conduct a sanity check against selected build artefacts or local environment

.PHONY: check-all-image-vars
check-all-image-vars: check-k8s-image-env-var check-k8s-image-dev-var check-etc-hosts check-insecure-cluster-registry

.PHONY: check-all-vars-without-image
check-all-vars-without-image: check-k8s-artifact-id check-k8s-namespace-env-var

.PHONY: check-k8s-namespace-env-var
check-k8s-namespace-env-var:
	@$(call check_defined, NAMESPACE, k8s namespace)

.PHONY: check-k8s-image-env-var
check-k8s-image-env-var:
	@$(call check_defined, IMAGE, docker image tag)

.PHONY: check-k8s-artifact-id
check-k8s-artifact-id:
	@$(call check_defined, ARTIFACT_ID, app/dogu name)

.PHONY: check-etc-hosts
check-etc-hosts:
	@if [[ ${RUNTIME_ENV} == "local" ]]; then \
		grep -E "^.+\s+${K3S_CLUSTER_FQDN}\$$" /etc/hosts > /dev/null || \
      		(echo "Missing /etc/hosts entry for ${K3S_CLUSTER_FQDN}" && exit 1) \
	fi

.PHONY: check-insecure-cluster-registry
check-insecure-cluster-registry:
	@if [[ ${RUNTIME_ENV} == "local" ]]; then \
		grep "${CES_REGISTRY_HOST}" /etc/docker/daemon.json > /dev/null || \
			(echo "Missing /etc/docker/daemon.json for ${CES_REGISTRY_HOST}" && exit 1) \
	fi

# If the RUNTIME_ENV is "remote" checks if the current docker-client has credentials for CES_REGISTRY_HOST
# If no credentials could be found, the credentials are queried and docker-login is performed
check-docker-credentials:
	@if [[ "$(RUNTIME_ENV)" == "remote" ]]; then \
		if ! grep -q $(CES_REGISTRY_HOST) ~/.docker/config.json ; then \
			echo "Error: Docker is not logged in to $(CES_REGISTRY_HOST)"; \
			read -p "Enter Docker Username for $(CES_REGISTRY_HOST): " username; \
			read -sp "Enter Docker Password for $(CES_REGISTRY_HOST): " password; \
			echo ""; \
			echo "$$password" | docker login -u "$$username" --password-stdin $(CES_REGISTRY_HOST); \
			if [ $$? -eq 0 ]; then \
				echo "Docker login to $(CES_REGISTRY_HOST) successful"; \
			else \
				echo "Docker login to $(CES_REGISTRY_HOST) failed"; \
				exit 1; \
			fi \
		fi \
	fi

##@ K8s - Resources

${K8S_RESOURCE_TEMP_FOLDER}:
	@mkdir -p $@


##@ K8s - Docker

.PHONY: docker-build
docker-build: check-docker-credentials check-k8s-image-env-var ${BINARY_YQ} ## Builds the docker image of the K8s app.
	@echo "Building docker image $(IMAGE)..."
	@DOCKER_BUILDKIT=1 docker build . -t $(IMAGE)

.PHONY: docker-dev-tag
docker-dev-tag: check-k8s-image-dev-var docker-build ## Tags a Docker image for local K3ces deployment.
	@echo "Tagging image with dev tag $(IMAGE_DEV_VERSION)..."
	@DOCKER_BUILDKIT=1 docker tag ${IMAGE} $(IMAGE_DEV_VERSION)

.PHONY: check-k8s-image-dev-var
check-k8s-image-dev-var:
ifeq (${IMAGE_DEV},)
	@echo "Missing make variable IMAGE_DEV detected. It should look like \$${CES_REGISTRY_HOST}/docker-image:tag"
	@exit 19
endif

.PHONY: image-import
image-import: check-all-vars check-k8s-artifact-id docker-dev-tag ## Imports the currently available image into the configured ces-registry.
	@echo "Import $(IMAGE_DEV_VERSION) into K8s cluster ${KUBE_CONTEXT_NAME}..."
	@docker push $(IMAGE_DEV_VERSION)
	@echo "Done."

## Functions

# Check that given variables are set and all have non-empty values,
# die with an error otherwise.
#
# Params:
#   1. Variable name(s) to test.
#   2. (optional) Error message to print.
check_defined = \
    $(strip $(foreach 1,$1, \
        $(call __check_defined,$1,$(strip $(value 2)))))
__check_defined = \
    $(if $(value $1),, \
      $(error Undefined $1$(if $2, ($2))))

##@ K8s - Download Utilities

.PHONY: install-yq ## Installs the yq YAML editor.
install-yq: ${BINARY_YQ}

${BINARY_YQ}: $(UTILITY_BIN_PATH)
	$(call go-get-tool,$(BINARY_YQ),github.com/mikefarah/yq/v4@${BINARY_YQ_4_VERSION})

##@ K8s - Download Kubernetes Utilities

.PHONY: install-helm ## Download helm locally if necessary.
install-helm: ${BINARY_HELM}

${BINARY_HELM}: $(UTILITY_BIN_PATH)
	$(call go-get-tool,$(BINARY_HELM),helm.sh/helm/v3/cmd/helm@${BINARY_HELM_VERSION})

.PHONY: controller-gen
controller-gen: ${CONTROLLER_GEN} ## Download controller-gen locally if necessary.

${CONTROLLER_GEN}:
	$(call go-get-tool,$(CONTROLLER_GEN),sigs.k8s.io/controller-tools/cmd/controller-gen@${CONTROLLER_GEN_VERSION})

ENVTEST = $(UTILITY_BIN_PATH)/setup-envtest
.PHONY: envtest
envtest: ${ENVTEST} ## Download envtest-setup locally if necessary.

${ENVTEST}:
	$(call go-get-tool,$(ENVTEST),sigs.k8s.io/controller-runtime/tools/setup-envtest@latest)

.PHONY: isProduction
isProduction:
	@if [[ "${STAGE}" == "production" ]]; then \
		echo "Command executed in production stage. Aborting."; \
		exit 1; \
	else \
		echo "Command executed in development stage. Continuing."; \
	fi


