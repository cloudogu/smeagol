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
    ORIG_NAME="$(grep -oP ".*[ ]*NAME=\"([^\"]*)" Dockerfile | awk -F "\"" '{print $2}')"
    ORIG_VERSION="$(grep -oP ".*[ ]*VERSION=\"([^\"]*)" Dockerfile | awk -F "\"" '{print $2}')"
    PRERELEASE_NAME="prerelease_$( echo -e "$ORIG_NAME" | sed 's/\//\\\//g' )"
    PRERELEASE_VERSION="${ORIG_VERSION}${TIMESTAMP}"
    sed -i "s/\(.*[ ]*NAME=\"\)\([^\"]*\)\(.*$\)/\1${PRERELEASE_NAME}\3/" Dockerfile
    sed -i "s/\(.*[ ]*VERSION=\"\)\([^\"]*\)\(.*$\)/\1${PRERELEASE_VERSION}\3/" Dockerfile
  fi

}


TYPE="${1}"

echo ${TYPE}
if [[ "${TYPE}" == "prerelease_namespace" ]];then
  prerelease_namespace
fi