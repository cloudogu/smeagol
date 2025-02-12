#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

# logging behaviour can be configured in logging/root with the following options <ERROR,WARN,INFO,DEBUG>
DEFAULT_LOGGING_KEY="logging/root"

SMEAGOL_LOGGING_TEMPLATE="/app/logback.xml.tpl"
SMEAGOL_LOGGING="/app/logback.xml"

echo "Validate root log level"
validateExitCode=0
doguctl validate "${DEFAULT_LOGGING_KEY}" || validateExitCode=$?

if [[ ${validateExitCode} -ne 0 ]]; then
    echo "ERROR: The loglevel configured in ${DEFAULT_LOGGING_KEY} is invalid. Cannot start dogu..."
    exit 1
fi

echo "Rendering logging configuration..."
doguctl template ${SMEAGOL_LOGGING_TEMPLATE} ${SMEAGOL_LOGGING}
