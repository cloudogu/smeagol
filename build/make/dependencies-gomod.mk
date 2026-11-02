##@ Go mod dependency management

.PHONY: dependencies
dependencies: vendor ## Install dependencies using go mod

vendor: go.mod tidy
	@echo "Installing dependencies using go modules..."
	${GO_CALL} mod vendor

tidy:
	@echo "Running go mod tidy to sync go.mod with go.sum"
	${GO_CALL} mod tidy
