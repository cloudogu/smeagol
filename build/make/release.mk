##@ Releases

# This makefile holds the dogu-release target for starting a new dogu release

.PHONY: dogu-release
dogu-release: ## Start a dogu release
	build/make/release.sh dogu

.PHONY: go-release
go-release: ## Start a go tool release
	build/make/release.sh go-tool