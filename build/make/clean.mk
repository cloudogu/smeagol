##@ Cleaning

.PHONY: clean
clean: $(ADDITIONAL_CLEAN) ## Remove target and tmp directories
	rm -rf ${TARGET_DIR}
	rm -rf ${TMP_DIR}
	rm -rf ${UTILITY_BIN_PATH}

.PHONY: dist-clean
dist-clean: clean ## Remove all generated directories
	rm -rf node_modules
	rm -rf public/vendor
	rm -rf vendor
	rm -rf npm-cache
	rm -rf bower
