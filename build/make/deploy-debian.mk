##@ Debian package deployment

# This Makefile holds all targets for deploying and undeploying
# Uses the variable APT_REPO to determine which apt repos should be used to deploy

# Attention: This Makefile depends on package-debian.mk!

.PHONY: deploy-check
deploy-check:
	@case X"${VERSION}" in *-SNAPSHOT) echo "i will not upload a snaphot version for you" ; exit 1; esac;
	@if [ X"${APT_API_USERNAME}" = X"" ] ; then echo "supply an APT_API_USERNAME environment variable"; exit 1; fi;
	@if [ X"${APT_API_PASSWORD}" = X"" ] ; then echo "supply an APT_API_PASSWORD environment variable"; exit 1; fi;
	@if [ X"${APT_API_SIGNPHRASE}" = X"" ] ; then echo "supply an APT_API_SIGNPHRASE environment variable"; exit 1; fi;

.PHONY: upload-package
upload-package: deploy-check $(DEBIAN_PACKAGE)
	@echo "... uploading package"
	@$(APTLY) -F file=@"${DEBIAN_PACKAGE}" "${APT_API_BASE_URL}/files/$$(basename ${DEBIAN_PACKAGE})"

.PHONY: add-package-to-repo
add-package-to-repo: upload-package
ifeq ($(APT_REPO), ces-premium)
	@echo "... add package to ces-premium repository"
	@$(APTLY) -X POST "${APT_API_BASE_URL}/repos/ces-premium/file/$$(basename ${DEBIAN_PACKAGE})"
else
	@echo "\n... add package to ces repository"
	@$(APTLY) -X POST "${APT_API_BASE_URL}/repos/ces/file/$$(basename ${DEBIAN_PACKAGE})"
endif

define aptly_publish
	$(APTLY) -X PUT -H "Content-Type: application/json" --data '{"Signing": { "Batch": true, "Passphrase": "${APT_API_SIGNPHRASE}"}}' ${APT_API_BASE_URL}/publish/$(1)/$(2)
endef

.PHONY: publish
publish:
	@echo "\n... publish packages"
ifeq ($(APT_REPO), ces-premium)
	@$(call aptly_publish,ces-premium,bionic)
else
	@$(call aptly_publish,ces,focal)
	@$(call aptly_publish,ces,bionic)
endif

.PHONY: deploy
deploy: add-package-to-repo publish ## Deploy package to apt repository

define aptly_undeploy
	PREF=$$(${APTLY} "${APT_API_BASE_URL}/repos/$(1)/packages?q=${ARTIFACT_ID}%20(${VERSION})"); \
	${APTLY} -X DELETE -H 'Content-Type: application/json' --data "{\"PackageRefs\": $${PREF}}" ${APT_API_BASE_URL}/repos/$(1)/packages
endef

.PHONY: remove-package-from-repo
remove-package-from-repo:
ifeq ($(APT_REPO), ces-premium)
	@$(call aptly_undeploy,ces-premium)
else
	@$(call aptly_undeploy,ces)
endif

.PHONY: undeploy
undeploy: deploy-check remove-package-from-repo publish ## Undeploy package from apt repository

.PHONE: lint-deb-package
lint-deb-package: debian ## Lint debian package
	@lintian -i $(DEBIAN_PACKAGE)
