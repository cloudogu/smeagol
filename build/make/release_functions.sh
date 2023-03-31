#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

wait_for_ok(){
  printf "\n"
  OK=false
  while [[ ${OK} != "ok" ]] ; do
    read -r -p "${1} (type 'ok'): " OK
  done
}

ask_yes_or_no(){
  local ANSWER=""

  while [ "${ANSWER}" != "y" ] && [ "${ANSWER}" != "n" ]; do
    read -r -p "${1} (type 'y/n'): " ANSWER
  done

  echo "${ANSWER}"
}

get_current_version_by_makefile(){
  grep '^VERSION=[0-9[:alpha:].-]*$' Makefile | sed s/VERSION=//g
}

get_current_version_by_dogu_json(){
  jq ".Version" --raw-output dogu.json
}

read_new_version(){
  local NEW_RELEASE_VERSION
  read -r -p "Current Version is v${CURRENT_TOOL_VERSION}. Please provide the new version: v" NEW_RELEASE_VERSION
  echo "${NEW_RELEASE_VERSION}"
}

validate_new_version(){
  local NEW_RELEASE_VERSION="${1}"
  # Validate that release version does not start with vv
  if [[ ${NEW_RELEASE_VERSION} = v* ]]; then
    echo "WARNING: The new release version (v${NEW_RELEASE_VERSION}) starts with 'vv'."
    echo "You must not enter the v when defining the new version."
    ANSWER=$(ask_yes_or_no "Should the first v be removed?")
    if [ "${ANSWER}" == "y" ]; then
      NEW_RELEASE_VERSION="${NEW_RELEASE_VERSION:1}"
      echo "Release version now is: ${NEW_RELEASE_VERSION}"
    fi
  fi;
}

start_git_flow_release(){
  local NEW_RELEASE_VERSION="${1}"
  # Do gitflow
  git flow init --defaults --force

  mainBranchExists="$(git show-ref refs/remotes/origin/main || echo "")"
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

  git checkout develop
  git pull origin develop
  git flow release start v"${NEW_RELEASE_VERSION}"
}

# update_versions updates files with the new release version and interactively asks the user for verification. If okay
# the updated files will be staged to git and finally committed.
#
# extension points:
# - update_versions_modify_files <newVersionNumber> - update a file with the new version number
# - update_versions_stage_modified_files - stage a modified file to prepare the file for the up-coming commit
update_versions(){
  local NEW_RELEASE_VERSION="${1}"

  if [[ $(type -t update_versions_modify_files) == function ]]; then
    preSkriptExitCode=0
    update_versions_modify_files "${NEW_RELEASE_VERSION}" || preSkriptExitCode=$?
    if [[ ${preSkriptExitCode} -ne 0 ]]; then
      echo "ERROR: custom update_versions_modify_files() exited with exit code ${preSkriptExitCode}"
      exit 1
    fi
  fi

  # Update version in dogu.json
  if [ -f "dogu.json" ]; then
    echo "Updating version in dogu.json..."
    jq ".Version = \"${NEW_RELEASE_VERSION}\"" dogu.json > dogu2.json && mv dogu2.json dogu.json
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
    jq ".version = \"${NEW_RELEASE_VERSION}\"" package.json > package2.json && mv package2.json package.json
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

update_changelog(){
  local NEW_RELEASE_VERSION="${1}"

  # Changelog update
  CURRENT_DATE=$(date --rfc-3339=date)
  NEW_CHANGELOG_TITLE="## [v${NEW_RELEASE_VERSION}] - ${CURRENT_DATE}"
  # Check if "Unreleased" tag exists
  while ! grep --silent "## \[Unreleased\]" CHANGELOG.md; do
    echo ""
    echo -e "\e[31mYour CHANGELOG.md does not contain a \"## [Unreleased]\" line!\e[0m"
    echo "Please add one to make it comply to https://keepachangelog.com/en/1.0.0/"
    wait_for_ok "Please insert a \"## [Unreleased]\" line into CHANGELOG.md now."
  done

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

show_diff(){
  if ! git diff --exit-code > /dev/null; then
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

finish_release_and_push(){
  local CURRENT_VERSION="${1}"
  local NEW_RELEASE_VERSION="${2}"

  # Push changes and delete release branch
  wait_for_ok "Upgrade from version v${CURRENT_VERSION} to version v${NEW_RELEASE_VERSION} finished. Should the changes be pushed?"
  git push origin release/v"${NEW_RELEASE_VERSION}"

  echo "Switching back to develop and deleting branch release/v${NEW_RELEASE_VERSION}..."
  git checkout develop
  git branch -D release/v"${NEW_RELEASE_VERSION}"
}
