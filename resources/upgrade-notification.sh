#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o pipefail

function run_notification() {
    FROM_VERSION="${1}"
    FROM_VERSION_WITHOUT_DOGU=$(echo "${FROM_VERSION}" | cut -d '-' -f1)
    FROM_MAJOR_VERSION=$(echo "${FROM_VERSION_WITHOUT_DOGU}" | cut -d '.' -f1)
    FROM_MINOR_VERSION=$(echo "${FROM_VERSION_WITHOUT_DOGU}" | cut -d '.' -f2)

    TO_VERSION="${2}"
    TO_VERSION_WITHOUT_DOGU=$(echo "${TO_VERSION}" | cut -d '-' -f1)
    TO_MAJOR_VERSION=$(echo "${TO_VERSION_WITHOUT_DOGU}" | cut -d '.' -f1)
    TO_MINOR_VERSION=$(echo "${TO_VERSION_WITHOUT_DOGU}" | cut -d '.' -f2)

     if [[ "${FROM_MAJOR_VERSION}" -lt 1 && "${TO_MAJOR_VERSION}" -ge 1 ]]; then
        echo "You are starting an upgrade of the Smeagol dogu (from ${FROM_VERSION} to ${TO_VERSION})."
        echo "It is necessary to install the SCM dogu with a version higher than or equals to 2.20.0-1."
    fi

    if [[  "${FROM_MAJOR_VERSION}" -eq "${TO_MAJOR_VERSION}" && "${FROM_MINOR_VERSION}" -lt 4 && "${TO_MINOR_VERSION}" -ge 4 ]]; then
        echo "You are starting an upgrade of the Smeagol dogu (from ${FROM_VERSION} to ${TO_VERSION})."
        echo "It is necessary to install the SCM dogu with a version higher than or equals to 2.20.0-1."
    fi
}

run_notification "$@"
