#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

prerelease_namespace() {

  # Update version in dogu.json
  if [ -f "dogu.json" ]; then
    echo "Updating name in dogu.json..."
    ORIG_NAME="$(jq -r ".Name" ./dogu.json)"
    PRERELEASE_NAME="prerelease_${ORIG_NAME}"
    jq ".Name = \"${PRERELEASE_NAME}\"" dogu.json >dogu2.json && mv dogu2.json dogu.json
    jq ".Image = \"registry.cloudogu.com/${PRERELEASE_NAME}\"" dogu.json >dogu2.json && mv dogu2.json dogu.json
  fi

  # Update version in Dockerfile
  if [ -f "Dockerfile" ]; then
    echo "Updating version in Dockerfile..."
    ORIG_NAME="$(grep -oP "^[ ]*NAME=\"([^\"]*)" Dockerfile | awk -F "\"" '{print $2}')"
    PRERELEASE_NAME="prerelease_$( echo -e "$ORIG_NAME" | sed 's/\//\\\//g' )"
    sed -i "s/\(^[ ]*NAME=\"\)\([^\"]*\)\(.*$\)/\1${PRERELEASE_NAME}\3/" Dockerfile
  fi

}


TYPE="${1}"

echo ${TYPE}
if [[ "${TYPE}" == "prerelease_namespace" ]];then
  prerelease_namespace
fi