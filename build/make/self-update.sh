#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

TYPE="${1}"

update_build_libs() {
  echo "Get newest version of ces-build-lib and dogu-build-lib"
  update_jenkinsfile
  echo "Newest Versions set. Please check your Jenkinsfile"
}

get_highest_version() {
  local target="${1}"
  local gitCesBuildLib
  # getting tags from ces-build.libs OR dogu-build-libs
  gitCesBuildLib="$(git ls-remote --tags --refs https://github.com/cloudogu/${target}-build-lib)"
  local highest
  # Flagfile for getting results out of while-loop
  rm -rf .versions
  while IFS= read -r line; do
      local version
      version="$(awk -F'/tags/' '{ for(i=1;i<=NF;i++) print $i }' <<< $line | tail -n 1 | sed 's/[^0-9\.]*//g')"
      if [[ $version == *"."* ]] ; then
        echo $version >> ".versions"
      fi
  done <<< "$gitCesBuildLib"
  highest=$(sort .versions | tail -n 1)
  rm -rf .versions
  echo "${highest}"
}

# Patch Jenkinsfile
update_jenkinsfile() {
  sed -i "s/ces-build-lib@[[:digit:]].[[:digit:]].[[:digit:]]/ces-build-lib@$(get_highest_version ces)/g" Jenkinsfile
  sed -i "s/dugu-build-lib@[[:digit:]].[[:digit:]].[[:digit:]]/dogu-build-lib@$(get_highest_version dogu)/g" Jenkinsfile
}

# switch for script entrypoint
if [[ "${TYPE}" == "buildlibs" ]];then
  update_build_libs
else
  echo "Unknown target ${TYPE}"
fi



