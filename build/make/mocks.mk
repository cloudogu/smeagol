##@ Mocking

MOCKERY_BIN=${UTILITY_BIN_PATH}/mockery
MOCKERY_VERSION?=v2.53.3
MOCKERY_YAML=${WORKDIR}/.mockery.yaml

${MOCKERY_BIN}: ${UTILITY_BIN_PATH}
	$(call go-get-tool,$(MOCKERY_BIN),github.com/vektra/mockery/v2@$(MOCKERY_VERSION))

${MOCKERY_YAML}:
	@cp ${BUILD_DIR}/make/mockery.yaml ${WORKDIR}/.mockery.yaml

.PHONY: mocks
mocks: ${MOCKERY_BIN} ${MOCKERY_YAML} ## This target is used to generate mocks for all interfaces in a project.
	@for dir in ${WORKDIR}/*/ ;\
 		do \
 		# removes trailing '/' \
		dir=$${dir%*/} ;\
		# removes everything before the last '/' \
		dir=$${dir##*/} ;\
		if ! echo '${MOCKERY_IGNORED}' | egrep -q "\b$${dir}\b" ;\
		then \
		  	echo "Creating mocks for $${dir}" ;\
			${MOCKERY_BIN} --all --dir $${dir} ;\
		fi ;\
 	done ;
	@echo "Mocks successfully created."
