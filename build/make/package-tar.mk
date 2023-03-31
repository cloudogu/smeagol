##@ Tar packaging

TAR_PACKAGE:=$(ARTIFACT_ID)-$(VERSION).tar.gz

.PHONY: package
package: $(TAR_PACKAGE) ## Build binary and create tar package from it

$(TAR_PACKAGE): $(BINARY)
	# Check owner and group id
	tar cvfz $(TARGET_DIR)/$(TAR_PACKAGE) -C $(TARGET_DIR) $$(basename ${BINARY}) $(TAR_ARGS)
