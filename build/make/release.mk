##@ Releases

# This makefile holds the dogu-release target for starting a new dogu release

.PHONY: dogu-release
dogu-release: ## Start a dogu release
	build/make/release.sh dogu "${FIXED_CVE_LIST}" $(DRY_RUN)

.PHONY: node-release
node-release: ## Start a node package release
	build/make/release.sh node-pkg

.PHONY: go-release
go-release: ## Start a go tool release
	build/make/release.sh go-tool

.PHONY: image-release
image-release: ## Start a go tool release
	build/make/release.sh image

.PHONY: dogu-cve-release
dogu-cve-release: ## Start a dogu release of a new build if the local build fixes critical CVEs
	@bash -c "build/make/release_cve.sh \"${REGISTRY_USERNAME}\" \"${REGISTRY_PASSWORD}\" \"${TRIVY_IMAGE_SCAN_FLAGS}\" \"${DRY_RUN}\" \"${CVE_SEVERITY}\""
