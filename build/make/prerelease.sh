#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

prerelease_namespace() {

  TIMESTAMP=$(date +"%Y%m%d%H%M%S")

  # Update version in dogu.json
  if [ -f "dogu.json" ]; then
    echo "Updating name in dogu.json..."
    ORIG_NAME="$(jq -r ".Name" ./dogu.json)"
    ORIG_VERSION="$(jq -r ".Version" ./dogu.json)"
    PRERELEASE_NAME="prerelease_${ORIG_NAME}"
    PRERELEASE_VERSION="${ORIG_VERSION}${TIMESTAMP}"
    jq ".Name = \"${PRERELEASE_NAME}\"" dogu.json >dogu2.json && mv dogu2.json dogu.json
    jq ".Version = \"${PRERELEASE_VERSION}\"" dogu.json >dogu2.json && mv dogu2.json dogu.json
    jq ".Image = \"registry.cloudogu.com/${PRERELEASE_NAME}\"" dogu.json >dogu2.json && mv dogu2.json dogu.json
  fi

  # Update version in Dockerfile
  if [ -f "Dockerfile" ]; then
    echo "Updating version in Dockerfile..."
    LABEL_BLOCK=$(sed -n '/^LABEL[[:space:]]/ {N; /NAME=".*"/ {N; /VERSION=".*"/ {p}}}' Dockerfile)

    # Extract NAME and VERSION from the LABEL block
    ORIG_NAME=$(echo "$LABEL_BLOCK" | sed -n 's/.*NAME="\([^"]*\)".*/\1/p')
    ORIG_VERSION=$(echo "$LABEL_BLOCK" | sed -n 's/.*VERSION="\([^"]*\)".*/\1/p')

    # Output the extracted values for debugging
    echo "ORIG_NAME Dockerfile: ${ORIG_NAME}"
    echo "ORIG_VERSION Dockerfile: ${ORIG_VERSION}"

    # Prepare prerelease name and version
    PRERELEASE_NAME="prerelease_$(echo -e "$ORIG_NAME" | sed 's/\//\\\//g')"
    PRERELEASE_VERSION="${ORIG_VERSION}${TIMESTAMP}"

    # Output the new values for debugging
    echo "PRERELEASE_NAME Dockerfile: ${PRERELEASE_NAME}"
    echo "PRERELEASE_VERSION Dockerfile: ${PRERELEASE_VERSION}"
    
    # Only replace NAME= and VERSION= and only inside the LABEL block
    # This assumes LABEL block is between 'LABEL' and first non-indented line
    sed -i '/^LABEL/,/^[^[:space:]]/ {
      s/\(NAME="\)[^"]*\("\)/\1'"${PRERELEASE_NAME}"'\2/
      s/\(VERSION="\)[^"]*\("\)/\1'"${PRERELEASE_VERSION}"'\2/
    }' Dockerfile
  fi
  
}


TYPE="${1}"

echo ${TYPE}
if [[ "${TYPE}" == "prerelease_namespace" ]];then
  prerelease_namespace
fi