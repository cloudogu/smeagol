#!/bin/bash
set -o errexit
set -o pipefail
set -o nounset

function readCveSeverityIfUnset() {
  if [ -z "${CVE_SEVERITY}" ]; then
    echo "CVE_SEVERITY is unset"
    while [[ -z ${CVE_SEVERITY} ]]; do
      read -r -p "select the desired cve severity (CRITICAL, HIGH, MEDIUM, ...): " CVE_SEVERITY
    done
  fi
}

function readCredentialsIfUnset() {
  if [ -z "${USERNAME}" ]; then
    echo "username is unset"
    while [[ -z ${USERNAME} ]]; do
      read -r -p "type username for ${REGISTRY_URL}: " USERNAME
    done
  fi
  if [ -z "${PASSWORD}" ]; then
    echo "password is unset"
    while [[ -z ${PASSWORD} ]]; do
      read -r -s -p "type password for ${REGISTRY_URL}: " PASSWORD
    done
  fi
}

function diffArrays() {
  local cveListX=("$1")
  local cveListY=("$2")
  local result=()

  local cveX
  # Disable the following shellcheck because the arrays are sufficiently whitespace delimited because of the jq parsing result.
  # shellcheck disable=SC2128
  for cveX in ${cveListX}; do
    local found=0
    local cveY
    for cveY in ${cveListY}; do
      [[ "${cveY}" == "${cveX}" ]] && {
        found=1
        break
      }
    done

    [[ "${found}" == 0 ]] && result+=("${cveX}")
  done

  echo "${result[@]}"
}

function dockerLogin() {
  docker login "${REGISTRY_URL}" -u "${USERNAME}" -p "${PASSWORD}"
}

function dockerLogout() {
  docker logout "${REGISTRY_URL}"
}

function nameFromDogu() {
  jsonPropertyFromDogu ".Name"
}

function imageFromDogu() {
  jsonPropertyFromDogu ".Image"
}

function versionFromDogu() {
  jsonPropertyFromDogu ".Version"
}

function jsonPropertyFromDogu() {
  local property="${1}"
  jq -r "${property}" "${DOGU_JSON_FILE}"
}

function pullRemoteImage() {
  docker pull "$(imageFromDogu):$(versionFromDogu)"
}

function buildLocalImage() {
  docker build --no-cache . -t "$(imageFromDogu):$(versionFromDogu)"
}

function scanImage() {
  docker run -v "${TRIVY_CACHE_DIR}":"${TRIVY_DOCKER_CACHE_DIR}" -v /var/run/docker.sock:/var/run/docker.sock -v "${TRIVY_PATH}":/result aquasec/trivy --cache-dir "${TRIVY_DOCKER_CACHE_DIR}" -f json -o /result/results.json image ${TRIVY_IMAGE_SCAN_FLAGS:+"${TRIVY_IMAGE_SCAN_FLAGS}"} "$(imageFromDogu):$(versionFromDogu)"
}

function parseTrivyJsonResult() {
  local severity="${1}"
  local trivy_result_file="${2}"

  # First select results which have the property "Vulnerabilities". Filter the vulnerability ids with the given severity and afterward put the values in an array.
  # This array is used to format the values with join(" ") in a whitespace delimited string list.
  jq -rc "[.Results[] | select(.Vulnerabilities) | .Vulnerabilities | .[] | select(.Severity == \"${severity}\") | .VulnerabilityID] | unique | join(\" \")" "${trivy_result_file}"
}

RELEASE_SH="build/make/release.sh"

REGISTRY_URL="registry.cloudogu.com"
DOGU_JSON_FILE="dogu.json"

CVE_SEVERITY=

TRIVY_PATH=
TRIVY_RESULT_FILE=
TRIVY_CACHE_DIR=
TRIVY_DOCKER_CACHE_DIR=/tmp/db
TRIVY_IMAGE_SCAN_FLAGS=

USERNAME=""
PASSWORD=""
DRY_RUN=

function runMain() {
  readCveSeverityIfUnset
  readCredentialsIfUnset
  dockerLogin

  mkdir -p "${TRIVY_PATH}" # Cache will not be removed after release. rm requires root because the trivy container only runs with root.
  pullRemoteImage
  scanImage
  local remote_trivy_cve_list
  remote_trivy_cve_list=$(parseTrivyJsonResult "${CVE_SEVERITY}" "${TRIVY_RESULT_FILE}")

  buildLocalImage
  scanImage
  local local_trivy_cve_list
  local_trivy_cve_list=$(parseTrivyJsonResult "${CVE_SEVERITY}" "${TRIVY_RESULT_FILE}")

  dockerLogout

  local cve_in_local_but_not_in_remote
  cve_in_local_but_not_in_remote=$(diffArrays "${local_trivy_cve_list}" "${remote_trivy_cve_list}")
  if [[ -n "${cve_in_local_but_not_in_remote}" ]]; then
    echo "Abort release. Added new vulnerabilities:"
    echo "${cve_in_local_but_not_in_remote[@]}"
    exit 2
  fi

  local cve_in_remote_but_not_in_local
  cve_in_remote_but_not_in_local=$(diffArrays "${remote_trivy_cve_list}" "${local_trivy_cve_list}")
  if [[ -z "${cve_in_remote_but_not_in_local}" ]]; then
    echo "Abort release. Fixed no new vulnerabilities"
    exit 3
  fi

  echo "Fixed ${CVE_SEVERITY} CVEs: ${cve_in_remote_but_not_in_local}"
  "${RELEASE_SH}" "dogu-cve-release" "${cve_in_remote_but_not_in_local}" "${DRY_RUN}"
}

# make the script only runMain when executed, not when sourced from bats tests
if [[ -n "${BASH_VERSION}" && "${BASH_SOURCE[0]}" == "${0}" ]]; then
  USERNAME="${1:-""}"
  PASSWORD="${2:-""}"
  TRIVY_IMAGE_SCAN_FLAGS="${3:-""}"
  DRY_RUN="${4:-""}"
  CVE_SEVERITY="${5:-""}"

  TRIVY_PATH="/tmp/trivy-dogu-cve-release-$(nameFromDogu)"
  TRIVY_RESULT_FILE="${TRIVY_PATH}/results.json"
  TRIVY_CACHE_DIR="${TRIVY_PATH}/db"
  runMain
fi
