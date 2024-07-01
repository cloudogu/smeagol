##@ Version

# This makefile is used to get the sha256sum of a specific github tag-src.tar.gz or .zip.
# You may set any of the following variables before your make call to change the hash url.

SHA_SUM_ORGANISATION?="cloudogu"
SHA_SUM_REPOSITORY?="ecosystem"
SHA_SUM_FILE_TYPE?="tar.gz"
SHA_SUM_VERSION?="v20.04.4-2"
SHA_SUM_URL?="https://github.com/${SHA_SUM_ORGANISATION}/${SHA_SUM_REPOSITORY}/archive/refs/tags/${SHA_SUM_VERSION}.${SHA_SUM_FILE_TYPE}"

.PHONY: sha-sum
sha-sum: ## Print out the version
	@echo "Downloading from: ${SHA_SUM_URL}"
	@wget -O - -o /dev/null "${SHA_SUM_URL}" > .download.for.hash \
     || (echo "Could not be downloaded" && exit 1) \
     && cat .download.for.hash | sha256sum
	@rm -f .download.for.hash
