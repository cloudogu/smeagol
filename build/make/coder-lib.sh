#!/bin/bash
# a collection of helpful functions to update coder workspaces for rapid development
set -e -u -x -o pipefail

function getContainerBin() {
  if [ -x "$(command -v podman)" ]; then
    echo "podman";
  else
    echo "docker";
  fi
}

function getCoderUser() {
  # check if coder is installed, so that there is no problem with build and release targets if this is called before
  if [ -x "$(command -v coder)" ]; then
    coder users show me -o json | jq -r '.username';
  fi
}

function getAllWorkspaces() {
  coder list -c workspace | tail -n+2
}

function doesWorkspaceExist() {
  coderUser="$1"
  workspaceName="$2"

  workspace=$(coder list -a -o json | jq -r "select(.[].owner_name == \"${coderUser}\" and  .[].name == \"${workspaceName}\") | .[0].name")
  if [ -z "$workspace" ]; then
    return 1 #workspace does not exist
  else
    return 0
  fi
}

function generateUniqueWorkspaceName() {
  local wantedWorkspacePrefix="$1"
  # use time to make name unique
  local time
  time=$(date +'%H-%M-%S')
  local lengthOfTime=${#time}
  local delimiter='-'
  local lengthOfDelimiter=${#delimiter}
  # trim prefix, as workspace names are limited to 32 chars
  local trimmedPrefix="${wantedWorkspacePrefix:0:$((32 - lengthOfDelimiter - lengthOfTime))}"
  local uniqueName="${trimmedPrefix}${delimiter}${time}"
  # '--' is forbidden in coder, replace multiple '-' with a single one.
  echo "${uniqueName}" | awk '{gsub(/[-]+/,"-")}1'
  # returns sth like 'myPrefix-12-45-23'
}

function buildImage() {
  local tag="$1"
  local containerBuildDir="${2:-./container}"
  local secretDir="${3:-./secrets}"
  local containerExec="${4:-podman}"

  # include build-secrets if there are any
  local secretArgs=()
  if [ -d "$secretDir" ]; then
    # shellcheck disable=SC2231
    for secretPath in $secretDir/*; do
      # do not match .sh scripts
      [[ $secretPath == *.sh ]] && continue
      local secretName
      secretName=$(basename "$secretPath")
      secretArgs+=("--secret=id=$secretName,src=$secretDir/$secretName")
    done
  fi

  if [ "$containerExec" = "podman" ]; then
    $containerExec build -t "$tag" --pull=newer "$containerBuildDir" "${secretArgs[@]}"
  else
    $containerExec build -t "$tag" --pull "$containerBuildDir" "${secretArgs[@]}"
  fi
}

function doTrivyConvert() {
  local trivyFlags=$1
  local outputFile=$2
  local containerExec=$3
  local jsonScanToConvert=$4

  local containerJsonScanFile="/tmp/scan.json"

  # shellcheck disable=SC2086
  # as globbing is what we want here
  "$containerExec" run --rm --pull=always \
    -v trivy-cache:/root/.cache \
    -v "$jsonScanToConvert:$containerJsonScanFile" \
    aquasec/trivy -q \
    convert $trivyFlags "$containerJsonScanFile"  > "$outputFile"
}

function uploadTemplate() {
  local templateDir="${1:?"Error. you need to add the template directory as the first parameter"}"
  local templateName="${2:?"Error. you need to add the template name as the second parameter"}"
  # for terraform variables (not editable by workspace users)
  local variablesFile="${templateDir}/variables.yaml"
  if [ -f "$variablesFile" ]; then
    local doesVariablesFileExist=1
  fi
  if ! coder template push -y -d "$templateDir" ${doesVariablesFileExist:+--variables-file "$variablesFile"} "$templateName"; then
    # if template does not exist yet, create it in coder
    coder template create -y -d "$templateDir" ${doesVariablesFileExist:+--variables-file "$variablesFile"} "$templateName"
  fi
}

function createNewWorkspace() {
  local templateName="$1"
  local workspaceName="$2"
  # 3. param is optional, set it to autofill prompts for coder params
  local templateDir="${3-unset}"
  local richParametersFile="${templateDir}/rich-parameters.yaml"
  if [ -n "${templateDir+x}" ] && [ -f "$richParametersFile" ]; then
    local doesRichParametersFileExist=1
  fi
  coder create -t "$templateName" -y "$workspaceName" ${doesRichParametersFileExist:+--rich-parameter-file "$richParametersFile"}
}

function removeAllOtherWorkspaces() {
  local CODER_USER="$1"
  local WORKSPACE_PREFIX="$2"
  local IGNORED_WORKSPACE="$3"
  WORKSPACES="$(getAllWorkspaces)"
  for ws in $WORKSPACES; do
    if [ "$ws" != "$CODER_USER/$IGNORED_WORKSPACE" ] && [[ "$ws" =~ ^"$CODER_USER/$WORKSPACE_PREFIX" ]]; then
      echo "delete $ws"
      if ! coder delete "$ws" -y; then
        #do it twice as podman always throws an error at the first time
        coder delete "$ws" -y
      fi
    fi
  done
}

function updateWorkspace() {
  local coderUser="$1"
  local workspaceName="$2"
  local qualifiedWorkspaceName="$coderUser/$workspaceName"
  if ! coder stop "$qualifiedWorkspaceName" -y; then
    #do it twice as podman always throws an error at the first time
    coder stop "$qualifiedWorkspaceName" -y
  fi
  coder update "$qualifiedWorkspaceName"
}

function startTestWorkspace() {
  local coderUser="$1"
  local templateDir="$2"
  local workspacePrefix="$3"
  local templateName="$4"
  local reuseTestWorkspace="$5"

  local newWorkspaceName
  if [ "$reuseTestWorkspace" = false ]; then
    newWorkspaceName="$(generateUniqueWorkspaceName "$workspacePrefix")"
    # do that before deleting others, so that i don't need to wait
    createNewWorkspace "$templateName" "$newWorkspaceName" "$templateDir"
    # trim prefix as the name of the workspace can also get trimmed
    removeAllOtherWorkspaces "$coderUser" "${workspacePrefix:0:22}" "$newWorkspaceName"
  else
    newWorkspaceName="$workspacePrefix"
    if ! doesWorkspaceExist "$coderUser" "$newWorkspaceName"; then
      createNewWorkspace "$templateName" "$newWorkspaceName" "$templateDir"
    else
      updateWorkspace "$coderUser" "$newWorkspaceName"
    fi
  fi
}

function uploadToNexus() {
  local fileToUpload="$1"
  local fileNameNexus="${fileToUpload##*/}"
  local templateName="$2"
  local releaseVersion="$3"
  local nexusUrl="${4:-https://ecosystem.cloudogu.com/nexus/repository/itz-bund/coder}"
  set +x #disable command printing because of the password
  curl --progress-bar -u "$(cat secrets/nexus-user):$(cat secrets/nexus-pw)" --upload-file "$fileToUpload" \
    "$nexusUrl/$templateName/$releaseVersion/$fileNameNexus"
  set -x
}