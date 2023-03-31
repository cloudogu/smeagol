GO_JUNIT_REPORT=$(UTILITY_BIN_PATH)/go-junit-report
GO_JUNIT_REPORT_VERSION=v1.0.0

$(GO_JUNIT_REPORT): $(UTILITY_BIN_PATH)
	@echo "Download go-junit-report..."
	@$(call go-get-tool,$@,github.com/jstemmer/go-junit-report@$(GO_JUNIT_REPORT_VERSION))
