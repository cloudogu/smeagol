##@ Yarn dependency management

YARN_LOCK=$(WORKDIR)/yarn.lock

.PHONY: yarn-install
yarn-install: $(YARN_TARGET) ## Execute yarn install

$(YARN_TARGET): $(YARN_LOCK) $(PASSWD)
	@echo "Executing yarn..."
	@docker run --rm \
	  -u "$(UID_NR):$(GID_NR)" \
	  -v $(PASSWD):/etc/passwd:ro \
	  -v $(WORKDIR):$(WORKDIR) \
	  -w $(WORKDIR) \
	  node:$(NODE_VERSION) \
	  yarn install
	@touch $@

.PHONY yarn-publish-ci:
yarn-publish-ci: ## Execute yarn publish with '--non-interactive' flag to suppress the version prompt
	@echo "Executing yarn publish..."
	@docker run --rm \
	  -u "$(UID_NR):$(GID_NR)" \
	  -v $(PASSWD):/etc/passwd:ro \
	  -v $(WORKDIR):$(WORKDIR) \
	  -w $(WORKDIR) \
	  node:$(NODE_VERSION) \
	  yarn publish --non-interactive

.PHONY yarn-publish: ## Execute yarn publish
yarn-publish: $(YARN_BUILD_TARGET)
	@echo "Executing yarn publish..."
	@docker run --rm \
	  -u "$(UID_NR):$(GID_NR)" \
	  -v $(PASSWD):/etc/passwd:ro \
	  -v $(WORKDIR):$(WORKDIR) \
	  -w $(WORKDIR) \
	  node:$(NODE_VERSION) \
	  yarn publish
