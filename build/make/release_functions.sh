#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

wait_for_ok() {
  printf "\n"
  local OK="false"
  while [[ "${OK}" != "ok" ]]; do
    read -r -p "${1} (type 'ok'): " OK
  done
}

ask_yes_or_no() {
  local ANSWER=""

  while [ "${ANSWER}" != "y" ] && [ "${ANSWER}" != "n" ]; do
    read -r -p "${1} (type 'y/n'): " ANSWER
  done

  echo "${ANSWER}"
}

get_current_version_by_makefile() {
  grep '^VERSION=[0-9[:alpha:].-]*$' Makefile | sed s/VERSION=//g
}

get_base_version_by_makefile() {
  BASE_VERSION=$(grep '^BASE_VERSION=[0-9[:alpha:].-]*$' Makefile | sed s/BASE_VERSION=//g)
  echo "${BASE_VERSION}"
}

get_current_version_by_dogu_json() {
  jq ".Version" --raw-output dogu.json
}

read_new_version() {
  local NEW_RELEASE_VERSION
  read -r -p "Current Version is v${CURRENT_TOOL_VERSION}. Please provide the new version: v" NEW_RELEASE_VERSION
  echo "${NEW_RELEASE_VERSION}"
}

validate_new_version() {
  local NEW_RELEASE_VERSION="${1}"
  # Validate that release version does not start with vv
  if [[ ${NEW_RELEASE_VERSION} = v* ]]; then
    echo "WARNING: The new release version (v${NEW_RELEASE_VERSION}) starts with 'vv'."
    echo "You must not enter the v when defining the new version."
    local ANSWER
    ANSWER=$(ask_yes_or_no "Should the first v be removed?")
    if [ "${ANSWER}" == "y" ]; then
      NEW_RELEASE_VERSION="${NEW_RELEASE_VERSION:1}"
      echo "Release version now is: ${NEW_RELEASE_VERSION}"
    fi
  fi
}

start_git_flow_release() {
  local NEW_RELEASE_VERSION="${1}"
  local BASE_RELEASE_VERSION="${2}"
  local BASE_DEV_BRANCH_NAME

  # Do gitflow
  git flow init --defaults --force

  local mainBranchExists
  mainBranchExists="$(git show-ref refs/remotes/origin/main || echo "")"
  if [[ -z "$BASE_RELEASE_VERSION" ]]; then
      echo "BASE_RELEASE_VERSION variable is empty"
      if [ -n "$mainBranchExists" ]; then
        echo 'Using "main" branch for production releases'
        git flow config set master main
        git checkout main
        git pull origin main
      else
        echo 'Using "master" branch for production releases'
        git checkout master
        git pull origin master
      fi
      BASE_DEV_BRANCH_NAME="develop"
  else
      echo "BASE_RELEASE_VERSION variable is not empty"
      if [[ ${NEW_RELEASE_VERSION} != ${BASE_RELEASE_VERSION}* ]]; then
      echo "ERROR: Release version (${NEW_RELEASE_VERSION}) does not start with base version (${BASE_RELEASE_VERSION})"
      exit 1
      fi

      BASE_MAIN_BRANCH_NAME="${BASE_RELEASE_VERSION}/main"
      echo "Using ${BASE_MAIN_BRANCH_NAME} branch for production releases"
      git flow config set master ${BASE_MAIN_BRANCH_NAME}
      git checkout ${BASE_MAIN_BRANCH_NAME}
      git pull origin ${BASE_MAIN_BRANCH_NAME}
      BASE_DEV_BRANCH_NAME="${BASE_RELEASE_VERSION}/develop"
  fi

  git flow config set develop ${BASE_DEV_BRANCH_NAME}

  git checkout ${BASE_DEV_BRANCH_NAME}
  git pull origin ${BASE_DEV_BRANCH_NAME}
  git flow config
  git flow release start v"${NEW_RELEASE_VERSION}"
}

start_dry_run_release() {
  local NEW_RELEASE_VERSION="${1}"

  git checkout -b dryrun/v"${NEW_RELEASE_VERSION}"
}

abort_dry_run_release() {
  local NEW_RELEASE_VERSION="${1}"
  local BASE_RELEASE_VERSION="${2}"

  local BASE_DEV_BRANCH_NAME

  if [[ -z "$BASE_RELEASE_VERSION" ]]; then
      BASE_DEV_BRANCH_NAME="develop"
  else
      BASE_DEV_BRANCH_NAME="${BASE_RELEASE_VERSION}/develop"
  fi

  git checkout ${BASE_DEV_BRANCH_NAME}
  git branch -D dryrun/v"${NEW_RELEASE_VERSION}"
}

# update_versions updates files with the new release version and interactively asks the user for verification. If okay
# the updated files will be staged to git and finally committed.
#
# extension points:
# - update_versions_modify_files <newVersionNumber> - update a file with the new version number
# - update_versions_stage_modified_files - stage a modified file to prepare the file for the up-coming commit
update_versions() {
  local NEW_RELEASE_VERSION="${1}"

  if [[ $(type -t update_versions_modify_files) == function ]]; then
    local preSkriptExitCode=0
    update_versions_modify_files "${NEW_RELEASE_VERSION}" || preSkriptExitCode=$?
    if [[ ${preSkriptExitCode} -ne 0 ]]; then
      echo "ERROR: custom update_versions_modify_files() exited with exit code ${preSkriptExitCode}"
      exit 1
    fi
  fi

  # Update version in dogu.json
  if [ -f "dogu.json" ]; then
    echo "Updating version in dogu.json..."
    jq ".Version = \"${NEW_RELEASE_VERSION}\"" dogu.json >dogu2.json && mv dogu2.json dogu.json
  fi

  # Update version in Dockerfile
  if [ -f "Dockerfile" ]; then
    echo "Updating version in Dockerfile..."
    sed -i "s/\(^[ ]*VERSION=\"\)\([^\"]*\)\(.*$\)/\1${NEW_RELEASE_VERSION}\3/" Dockerfile
  fi

  # Update version in Makefile
  if [ -f "Makefile" ]; then
    echo "Updating version in Makefile..."
    sed -i "s/\(^VERSION=\)\(.*\)$/\1${NEW_RELEASE_VERSION}/" Makefile
  fi

  # Update version in package.json
  if [ -f "package.json" ]; then
    echo "Updating version in package.json..."
    jq ".version = \"${NEW_RELEASE_VERSION}\"" package.json >package2.json && mv package2.json package.json
  fi

  # Update version in pom.xml
  if [ -f "pom.xml" ]; then
    echo "Updating version in pom.xml..."
    mvn versions:set -DgenerateBackupPoms=false -DnewVersion="${NEW_RELEASE_VERSION}"
  fi

  wait_for_ok "Please make sure that all versions have been updated correctly now (e.g. via \"git diff\")."

  ### The `git add` command has to be after the okay. Otherwise user-made changes to versions would not be added.

  if [[ $(type -t update_versions_stage_modified_files) == function ]]; then
    preSkriptExitCode=0
    update_versions_stage_modified_files "${NEW_RELEASE_VERSION}" || preSkriptExitCode=$?
    if [[ ${preSkriptExitCode} -ne 0 ]]; then
      echo "ERROR: custom update_versions_stage_modified_files exited with exit code ${preSkriptExitCode}"
      exit 1
    fi
  fi

  if [ -f "dogu.json" ]; then
    git add dogu.json
  fi

  if [ -f "Dockerfile" ]; then
    git add Dockerfile
  fi

  if [ -f "Makefile" ]; then
    git add Makefile
  fi

  if [ -f "package.json" ]; then
    git add package.json
  fi

  if [ -f "pom.xml" ]; then
    git add pom.xml
  fi

  git commit -m "Bump version"
}

update_changelog() {
  local NEW_RELEASE_VERSION="${1}"
  local FIXED_CVE_LIST="${2}"

  # Changelog update
  local CURRENT_DATE
  CURRENT_DATE=$(date --rfc-3339=date)
  local NEW_CHANGELOG_TITLE="## [v${NEW_RELEASE_VERSION}] - ${CURRENT_DATE}"
  # Check if "Unreleased" tag exists
  while ! grep --silent "## \[Unreleased\]" CHANGELOG.md; do
    echo ""
    echo -e "\e[31mYour CHANGELOG.md does not contain a \"## [Unreleased]\" line!\e[0m"
    echo "Please add one to make it comply to https://keepachangelog.com/en/1.0.0/"
    wait_for_ok "Please insert a \"## [Unreleased]\" line into CHANGELOG.md now."
  done

  if [[ -n "${FIXED_CVE_LIST}" ]]; then
    addFixedCVEListFromReRelease "${FIXED_CVE_LIST}"
  fi

  # Add new title line to changelog
  sed -i "s|## \[Unreleased\]|## \[Unreleased\]\n\n${NEW_CHANGELOG_TITLE}|g" CHANGELOG.md

  # Wait for user to validate changelog changes
  wait_for_ok "Please make sure your CHANGELOG.md looks as desired."

  # Check if new version tag still exists
  while ! grep --silent "## \[v${NEW_RELEASE_VERSION}\] - ${CURRENT_DATE}" CHANGELOG.md; do
    echo ""
    echo -e "\e[31mYour CHANGELOG.md does not contain \"${NEW_CHANGELOG_TITLE}\"!\e[0m"
    wait_for_ok "Please update your CHANGELOG.md now."
  done

  git add CHANGELOG.md
  git commit -m "Update changelog"
}

update_releasenotes() {
  local NEW_RELEASE_VERSION="${1}"

  # ReleaseNotes update
  local CURRENT_DATE
  CURRENT_DATE=$(date --rfc-3339=date)
  local NEW_RELEASENOTE_TITLE="## [v${NEW_RELEASE_VERSION}] - ${CURRENT_DATE}"
  rm -rf ".rn_changed"
  find . -name "*release_notes*.md" -print0 | while read -d $'\0' file
  do
     # Check if "Unreleased" tag exists
     while ! grep --silent "## \[Unreleased\]" "${file}"; do
       echo ""
       echo -e "\e[31mYour ${file} does not contain a \"## [Unreleased]\" line!\e[0m"
       echo "Please add one to make it comply to https://keepachangelog.com/en/1.0.0/"
       wait_for_ok "Please insert a \"## [Unreleased]\" line into ${file} now."
     done

     # Add new title line to changelog
     sed -i "s|## \[Unreleased\]|## \[Unreleased\]\n\n${NEW_RELEASENOTE_TITLE}|g" "${file}"
     echo "Processed ${file}"
     echo true > ".rn_changed"
  done

  if test -f ".rn_changed" ; then
    # Wait for user to validate changelog changes
    wait_for_ok "Please make sure your release notes looks as desired."

    find . -name "*release_notes*.md" -print0 | while read -d $'\0' file
    do
      # Check if new version tag still exists
      while ! grep --silent "$(echo $NEW_RELEASENOTE_TITLE | sed -e 's/[]\/$*.^[]/\\&/g')" "${file}"; do
        echo ""
        echo -e "\e[31mYour ${file} does not contain \"${NEW_RELEASENOTE_TITLE}\"!\e[0m"
        wait_for_ok "Please update your ${file} now."
      done
      git add "${file}"
    done

    git commit -m "Update ReleaseNotes"
  fi
  rm -rf ".rn_changed"
}

# addFixedCVEListFromReRelease is used in dogu cve releases. The method adds the fixed CVEs under the ### Fixed header
# in the unreleased section.
addFixedCVEListFromReRelease() {
  local fixed_cve_list="${1}"

  local cve_sed_search=""
  local cve_sed_replace=""
  local fixed_exists_in_unreleased
  fixed_exists_in_unreleased=$(awk '/^\#\# \[Unreleased\]$/{flag=1;next}/^\#\# \[/{flag=0}flag' CHANGELOG.md | grep -e "^### Fixed$" || true)
  if [[ -n "${fixed_exists_in_unreleased}" ]]; then
    # extend fixed header with CVEs.
    cve_sed_search="^\#\#\# Fixed$"
    cve_sed_replace="\#\#\# Fixed\n- Fixed ${fixed_cve_list}"
  else
    # extend unreleased header with fixed header and CVEs.
    cve_sed_search="^\#\# \[Unreleased\]$"
    cve_sed_replace="\#\# \[Unreleased\]\n\#\#\# Fixed\n- Fixed ${fixed_cve_list}"

    local any_exists_unreleased
    any_exists_unreleased=$(awk '/^\#\# \[Unreleased\]$/{flag=1;next}/^\#\# \[/{flag=0}flag' CHANGELOG.md | grep -e "^\#\#\# Added$" -e "^\#\#\# Fixed$" -e "^\#\#\# Changed$" || true)
    if [[ -n ${any_exists_unreleased} ]]; then
      cve_sed_replace+="\n"
    fi
  fi

  sed -i "0,/${cve_sed_search}/s//${cve_sed_replace}/" CHANGELOG.md
}

show_diff() {
  if ! git diff --exit-code >/dev/null; then
    echo "There are still uncommitted changes:"
    echo ""
    echo "# # # # # # # # # #"
    echo ""
    git --no-pager diff
    echo ""
    echo "# # # # # # # # # #"
  fi

  echo "All changes compared to develop branch:"
  echo ""
  echo "# # # # # # # # # #"
  echo ""
  git --no-pager diff develop
  echo ""
  echo "# # # # # # # # # #"
}

finish_release_and_push() {
  local CURRENT_VERSION="${1}"
  local NEW_RELEASE_VERSION="${2}"
  local BASE_RELEASE_VERSION="${3}"

  # Push changes and delete release branch
  wait_for_ok "Upgrade from version v${CURRENT_VERSION} to version v${NEW_RELEASE_VERSION} finished. Should the changes be pushed?"
  git push origin release/v"${NEW_RELEASE_VERSION}"

  echo "Switching back to develop and deleting branch release/v${NEW_RELEASE_VERSION}..."

  local BASE_DEV_BRANCH_NAME

  if [[ -z "$BASE_RELEASE_VERSION" ]]; then
      BASE_DEV_BRANCH_NAME="develop"
  else
      BASE_DEV_BRANCH_NAME="${BASE_RELEASE_VERSION}/develop"
  fi

  git checkout ${BASE_DEV_BRANCH_NAME}
  git branch -D release/v"${NEW_RELEASE_VERSION}"
}
