#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

set -euo pipefail
IFS=$'\n\t'

# wait until scm passes all health checks
echo "wait until scm passes all health checks"
if ! doguctl healthy --wait --timeout 300 scm; then
  echo "timeout reached by waiting of scm to get healthy"
  exit 1
fi

TRUSTSTORE="${SMEAGOL_HOME}/truststore.jks"
create_truststore.sh "${TRUSTSTORE}" > /dev/null

# override setting from src/main/resources/application.yml
FQDN=$(doguctl config --global fqdn)
export FQDN
export SMEAGOL_HOME
doguctl template /app/application.yml.tpl /app/application.yml

# configure logging behaviour using the etcd property logging/root <ERROR,WARN,INFO,DEBUG>

# If an error occurs in logging.sh the whole scripting quits because of -o errexit. Catching the sourced exit code
# leads to an zero exit code which enables further error handling.
loggingExitCode=0
# shellcheck disable=SC1091
source /logging.sh || loggingExitCode=$?
if [[ ${loggingExitCode} -ne 0 ]]; then
  echo "ERROR: An error occurred during the root log level evaluation.";
  doguctl state "ErrorRootLogLevelMapping"
  sleep 300
  exit 2
fi

if [[ "$(doguctl config "container_config/memory_limit" -d "empty")" == "empty" ]];  then
  echo "Starting Smeagol without memory limits..."
  java -Djava.awt.headless=true \
       -Djava.net.preferIPv4Stack=true \
       -Djavax.net.ssl.trustStore="${TRUSTSTORE}" \
       -Dlogging.config=/app/logback.xml \
       -Djavax.net.ssl.trustStorePassword=changeit \
       -jar /app/smeagol.war

else
  # Retrieve configurable java limits from etcd, valid default values exist
  MEMORY_LIMIT_MAX_PERCENTAGE=$(doguctl config "container_config/java_max_ram_percentage")
  MEMORY_LIMIT_MIN_PERCENTAGE=$(doguctl config "container_config/java_min_ram_percentage")
  echo "Starting Smeagol with memory limits: MaxRAMPercentage=${MEMORY_LIMIT_MAX_PERCENTAGE}, MinRAMPercentage=${MEMORY_LIMIT_MIN_PERCENTAGE} ..."

  java -Djava.awt.headless=true \
       -Djava.net.preferIPv4Stack=true \
       -Djavax.net.ssl.trustStore="${TRUSTSTORE}" \
       -Djavax.net.ssl.trustStorePassword=changeit \
       -Dlogging.config=/app/logback.xml \
       -XX:MaxRAMPercentage="${MEMORY_LIMIT_MAX_PERCENTAGE}" \
       -XX:MinRAMPercentage="${MEMORY_LIMIT_MIN_PERCENTAGE}" \
       -jar /app/smeagol.war
fi
