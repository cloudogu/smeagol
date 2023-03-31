WORKSPACE=/workspace
BATS_LIBRARY_DIR=$(TARGET_DIR)/bats_libs
TESTS_DIR=$(WORKDIR)/batsTests
BASH_TEST_REPORT_DIR=$(TARGET_DIR)/shell_test_reports
BASH_TEST_REPORTS=$(BASH_TEST_REPORT_DIR)/TestReport-*.xml
BATS_ASSERT=$(BATS_LIBRARY_DIR)/bats-assert
BATS_MOCK=$(BATS_LIBRARY_DIR)/bats-mock
BATS_SUPPORT=$(BATS_LIBRARY_DIR)/bats-support
BATS_FILE=$(BATS_LIBRARY_DIR)/bats-file
BATS_BASE_IMAGE?=bats/bats
BATS_CUSTOM_IMAGE?=cloudogu/bats
BATS_TAG?=1.2.1
BATS_DIR=build/make/bats
BATS_WORKDIR="${WORKDIR}"/"${BATS_DIR}"

.PHONY unit-test-shell:
unit-test-shell: unit-test-shell-$(ENVIRONMENT)

$(BATS_ASSERT):
	@git clone --depth 1 https://github.com/bats-core/bats-assert $@

$(BATS_MOCK):
	@git clone --depth 1 https://github.com/grayhemp/bats-mock $@

$(BATS_SUPPORT):
	@git clone --depth 1 https://github.com/bats-core/bats-support $@

$(BATS_FILE):
	@git clone --depth 1 https://github.com/bats-core/bats-file $@

$(BASH_SRC):
	BASH_SRC:=$(shell find "${WORKDIR}" -type f -name "*.sh")

${BASH_TEST_REPORT_DIR}: $(TARGET_DIR)
	@mkdir -p $(BASH_TEST_REPORT_DIR)

unit-test-shell-ci: $(BASH_SRC) $(BASH_TEST_REPORT_DIR) $(BATS_ASSERT) $(BATS_MOCK) $(BATS_SUPPORT) $(BATS_FILE)
	@echo "Test shell units on CI server"
	@make unit-test-shell-generic

unit-test-shell-local: $(BASH_SRC) $(PASSWD) $(ETCGROUP) $(HOME_DIR) buildTestImage $(BASH_TEST_REPORT_DIR) $(BATS_ASSERT) $(BATS_MOCK) $(BATS_SUPPORT) $(BATS_FILE)
	@echo "Test shell units locally (in Docker)"
	@docker run --rm \
		-v $(HOME_DIR):/home/$(USER) \
		-v $(WORKDIR):$(WORKSPACE) \
		-w $(WORKSPACE) \
		--entrypoint="" \
		$(BATS_CUSTOM_IMAGE):$(BATS_TAG) \
		"${BATS_DIR}"/customBatsEntrypoint.sh make unit-test-shell-generic-no-junit

unit-test-shell-generic:
	@bats --formatter junit --output ${BASH_TEST_REPORT_DIR} ${TESTS_DIR}

unit-test-shell-generic-no-junit:
	@bats ${TESTS_DIR}

.PHONY buildTestImage:
buildTestImage:
	@echo "Build shell test container"
	@cd $(BATS_WORKDIR) && docker build \
		--build-arg=BATS_BASE_IMAGE=${BATS_BASE_IMAGE} \
		--build-arg=BATS_TAG=${BATS_TAG} \
		-t ${BATS_CUSTOM_IMAGE}:${BATS_TAG} \
		.