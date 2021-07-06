.DEFAULT_GOAL=help
.PHONY: sonarqube build clean prettier help
branch := $(shell git branch --show-current)

sonarqube: ## Run sonarqube
	./gradlew sonarqube \
	-Dsonar.projectKey="Chocorean_authmod-core" \
	-Dsonar.organization="chocorean-sc" \
	-Dsonar.host.url="https://sonarcloud.io" \
	-Dsonar.branch.name="${branch}" \
	-Dsonar.login="${SONAR_TOKEN}"

clean:
	./gradlew clean

prettier:
	npm init -y
	npm install prettier-plugin-java --dev
	npx prettier --print-width 130 --write "**/*.java"
	rm -rf package.json package-lock.json node_modules

build: ## Build the mod
	./gradlew build

help: ## Show this help
	@grep -E '^[a-zA-Z0-9_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
