#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

# scan a already build image for CVE findings
# Get tag name from dogu.json
trivy_scan() {
  echo "Build image and get Tag-Name:"
  IMAGE_TAG="$(jq ".Image" --raw-output dogu.json):$(jq ".Version" --raw-output dogu.json)"
  docker run -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image --severity $SEVERITY $IMAGE_TAG
}

TYPE="${1}"
SEVERITY="${2:-"CRITICAL"}"

if [[ "${TYPE}" == "scan" ]];then
  trivy_scan
fi