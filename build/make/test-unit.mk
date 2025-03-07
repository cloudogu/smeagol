##@ Unit testing

UNIT_TEST_DIR=$(TARGET_DIR)/unit-tests
XUNIT_JSON=$(UNIT_TEST_DIR)/report.json
XUNIT_XML=$(UNIT_TEST_DIR)/unit-tests.xml
UNIT_TEST_LOG=$(UNIT_TEST_DIR)/unit-tests.log
COVERAGE_REPORT=$(UNIT_TEST_DIR)/coverage.out

PRE_UNITTESTS?=
POST_UNITTESTS?=

.PHONY: unit-test
unit-test: $(XUNIT_JSON) ## Start unit tests

$(XUNIT_JSON): $(SRC) $(GO_JUNIT_REPORT)
ifneq ($(strip $(PRE_UNITTESTS)),)
	@make $(PRE_UNITTESTS)
endif

	@mkdir -p $(UNIT_TEST_DIR)
	@echo 'mode: set' > ${COVERAGE_REPORT}
	@rm -f $(UNIT_TEST_LOG) || true
	@for PKG in $(PACKAGES) ; do \
    ${GO_CALL} test -v $$PKG -coverprofile=${COVERAGE_REPORT}.tmp -json 2>&1 | tee $(UNIT_TEST_LOG).tmp ; \
		cat ${COVERAGE_REPORT}.tmp | tail +2 >> ${COVERAGE_REPORT} ; \
		rm -f ${COVERAGE_REPORT}.tmp ; \
		cat $(UNIT_TEST_LOG).tmp >> $(UNIT_TEST_LOG) ; \
		rm -f $(UNIT_TEST_LOG).tmp ; \
	done
	@cat $(UNIT_TEST_LOG) >> $@
	@cat $(UNIT_TEST_LOG) | $(GO_JUNIT_REPORT) -parser gojson > $(XUNIT_XML)

	@if grep '^FAIL' $(UNIT_TEST_LOG); then \
		exit 1; \
	fi

ifneq ($(strip $(POST_UNITTESTS)),)
	@make $(POST_UNITTESTS)
endif
