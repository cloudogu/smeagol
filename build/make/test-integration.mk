##@ Integration testing

INTEGRATION_TEST_DIR=$(TARGET_DIR)/integration-tests
XUNIT_INTEGRATION_XML=$(INTEGRATION_TEST_DIR)/integration-tests.xml
INTEGRATION_TEST_LOG=$(INTEGRATION_TEST_DIR)/integration-tests.log
INTEGRATION_TEST_REPORT=$(INTEGRATION_TEST_DIR)/coverage.out
PRE_INTEGRATIONTESTS?=start-local-docker-compose
POST_INTEGRATIONTESTS?=stop-local-docker-compose
INTEGRATION_TEST_NAME_PATTERN?=.*

.PHONY: integration-test
integration-test: $(XUNIT_INTEGRATION_XML) ## Start integration tests

.PHONY: start-local-docker-compose
start-local-docker-compose:
ifeq ($(ENVIRONMENT), local)
		echo "Found developer environment. Starting up docker-compose"
		docker-compose up -d
else
		echo "Found CI environment. Use existing docker configuration"
endif


.PHONY: stop-local-docker-compose
stop-local-docker-compose:
ifeq ($(ENVIRONMENT), local)
		echo "Found developer environment. Quitting docker-compose"
		docker-compose kill;
else
		echo "Found CI environment. Nothing to be done"
endif

$(XUNIT_INTEGRATION_XML): $(SRC) $(GO_JUNIT_REPORT)
ifneq ($(strip $(PRE_INTEGRATIONTESTS)),)
	@make $(PRE_INTEGRATIONTESTS)
endif

	@mkdir -p $(INTEGRATION_TEST_DIR)
	@echo 'mode: set' > $(INTEGRATION_TEST_REPORT)
	@rm -f $(INTEGRATION_TEST_LOG) || true
	@$(GO_CALL) test ./... -v -tags=${GO_BUILD_TAG_INTEGRATION_TEST} -coverpkg=./... -coverprofile=${INTEGRATION_TEST_REPORT} -run ${INTEGRATION_TEST_NAME_PATTERN} 2>&1 | tee $(INTEGRATION_TEST_LOG)
	@cat $(INTEGRATION_TEST_LOG) | $(GO_JUNIT_REPORT) > $@
	@if grep '^FAIL' $(INTEGRATION_TEST_LOG); then \
		exit 1; \
	fi

ifneq ($(strip $(POST_INTEGRATIONTESTS)),)
	@make $(POST_INTEGRATIONTESTS)
endif
