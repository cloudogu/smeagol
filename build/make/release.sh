#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

# Extension points in release.sh:
#
# A custom release argument file will be sourced if found. The custom release arg file may implement one or more bash
# functions which either release.sh or release_functions.sh define. If such a custom release function is found the
# release script must define the argument list which the custom release function will receive during the release.

sourceCustomReleaseArgs() {
  RELEASE_ARGS_FILE="${1}"

  if [[ -f "${RELEASE_ARGS_FILE}" ]]; then
    echo "Using custom release args file ${RELEASE_ARGS_FILE}"

    local sourceCustomReleaseExitCode=0
    # shellcheck disable=SC1090
    source "${RELEASE_ARGS_FILE}" || sourceCustomReleaseExitCode=$?
    if [[ ${sourceCustomReleaseExitCode} -ne 0 ]]; then
      echo "Error while sourcing custom release arg file ${sourceCustomReleaseExitCode}. Exiting."
      exit 9
    fi
  fi
}

PROJECT_DIR="$(pwd)"
RELEASE_ARGS_FILE="${PROJECT_DIR}/release_args.sh"

sourceCustomReleaseArgs "${RELEASE_ARGS_FILE}"

# shellcheck disable=SC1090
source "$(pwd)/build/make/release_functions.sh"

TYPE="${1}"
FIXED_CVE_LIST="${2:-""}"
DRY_RUN="${3:-""}"

echo "=====Starting Release process====="

if [[ "${TYPE}" == "dogu"  || "${TYPE}" == "dogu-cve-release" ]];then
  CURRENT_TOOL_VERSION=$(get_current_version_by_dogu_json)
else
  CURRENT_TOOL_VERSION=$(get_current_version_by_makefile)
fi

NEW_RELEASE_VERSION="$(read_new_version)"

validate_new_version "${NEW_RELEASE_VERSION}"
if [[ -n "${DRY_RUN}" ]]; then
  start_dry_run_release "${NEW_RELEASE_VERSION}"
else
  start_git_flow_release "${NEW_RELEASE_VERSION}"
fi

update_versions "${NEW_RELEASE_VERSION}"
update_changelog "${NEW_RELEASE_VERSION}" "${FIXED_CVE_LIST}"
update_releasenotes "${NEW_RELEASE_VERSION}"
show_diff

if [[ -n "${DRY_RUN}" ]]; then
  abort_dry_run_release "${NEW_RELEASE_VERSION}"
else
  finish_release_and_push "${CURRENT_TOOL_VERSION}" "${NEW_RELEASE_VERSION}"
fi

echo "=====Finished Release process====="
