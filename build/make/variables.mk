TARGET_DIR=target

WORKDIR:=$(shell pwd)
BUILD_DIR=$(WORKDIR)/build
TMP_DIR:=$(BUILD_DIR)/tmp

BINARY:=$(TARGET_DIR)/$(ARTIFACT_ID)

COMMIT_ID:=$(shell git rev-parse HEAD)
LAST_COMMIT_DATE=$(shell git rev-list --format=format:'%ci' --max-count=1 `git rev-parse HEAD` | tail -1)
TAR_ARGS:=--owner=0:0 --group=0:0 --mtime="$(LAST_COMMIT_DATE)" --sort=name
BRANCH=$(shell git branch | grep \* | sed 's/ /\n/g' | head -2 | tail -1)

GO_ENVIRONMENT?=
# GO_CALL accomodates the go CLI command as well as necessary environment variables which are optional.
GO_CALL=${GO_ENVIRONMENT} go
PACKAGES=$(shell ${GO_CALL} list ./... | grep -v /vendor/)
GO_BUILD_TAG_INTEGRATION_TEST?=integration
GOMODULES=on
UTILITY_BIN_PATH?=${WORKDIR}/.bin

SRC:=$(shell find "${WORKDIR}" -type f -name "*.go" -not -path "*/vendor/*")

# debian stuff
DEBIAN_BUILD_DIR=$(BUILD_DIR)/deb
DEBIAN_CONTENT_DIR=$(DEBIAN_BUILD_DIR)/content
DEBIAN_PACKAGE=$(TARGET_DIR)/$(ARTIFACT_ID)_$(VERSION).deb
APT_API_BASE_URL=https://apt-api.cloudogu.com/api

# choose the environment, if BUILD_URL environment variable is available then we are on ci (jenkins)
ifdef BUILD_URL
ENVIRONMENT=ci
else
ENVIRONMENT=local
endif

YARN_TARGET=$(WORKDIR)/node_modules
BOWER_TARGET?=$(WORKDIR)/public/vendor
NODE_VERSION?=8

UID_NR:=$(shell id -u)
GID_NR:=$(shell id -g)
HOME_DIR=$(TMP_DIR)/home
PASSWD=$(TMP_DIR)/passwd
ETCGROUP=$(TMP_DIR)/group

$(TMP_DIR):
	@mkdir -p $(TMP_DIR)

$(HOME_DIR): $(TMP_DIR)
	@mkdir -p $(HOME_DIR)

$(TARGET_DIR):
	@mkdir -p $(TARGET_DIR)

$(PASSWD): $(TMP_DIR)
	@echo "$(USER):x:$(UID_NR):$(GID_NR):$(USER):/home/$(USER):/bin/bash" > $(PASSWD)

$(ETCGROUP): $(TMP_DIR)
	@echo "root:x:0:" > $(ETCGROUP)
	@echo "$(USER):x:$(GID_NR):" >> $(ETCGROUP)

$(UTILITY_BIN_PATH):
	@mkdir -p $@

# Subdirectories of workdir where no mocks should be generated.
# Multiple directories can be separated by space, comma or whatever is not a word to regex.
MOCKERY_IGNORED=vendor,build,docs

##@ General

.PHONY: help
help: ## Display this help.
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m<target>\033[0m\n"} /^[a-zA-Z_0-9-]+:.*?##/ { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

.PHONY: info
info: ## Print build information
	@echo "dumping build information ..."
	@echo "Version    : $(VERSION)"
	@echo "Commit-ID  : $(COMMIT_ID)"
	@echo "Environment: $(ENVIRONMENT)"
	@echo "Branch     : $(BRANCH)"
	@echo "Packages   : $(PACKAGES)"


# go-get-tool will 'go get' any package $2 and install it to $1.
define go-get-tool
	@[ -f $(1) ] || { \
		set -e ;\
		TMP_DIR=$$(mktemp -d) ;\
		cd $$TMP_DIR ;\
		go mod init tmp ;\
		echo "Downloading $(2)" ;\
		GOBIN=$(UTILITY_BIN_PATH) go install $(2) ;\
		rm -rf $$TMP_DIR ;\
	}
endef
