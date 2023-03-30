##@ Go mod dependency management

.PHONY: dependencies
dependencies: vendor ## Install dependencies using go mod

vendor: go.mod go.sum
	@echo "Installing dependencies using go modules..."
	${GO_CALL} mod vendor
