#!/bin/bash

set -euo pipefail
IFS=$'\n\t'

FQDN=$(doguctl config --global fqdn)
export SMEAGOL_SERVICE_URL="https://${FQDN}/smeagol"
export SMEAGOL_CAS_URL="https://${FQDN}/cas"
export SCM_INSTANCE_URL="https://${FQDN}/scm"
export PLANTUML_URL="https://${FQDN}/plantuml/png"


# wait until scm passes all health checks
echo "wait until scm passes all health checks"
if ! doguctl healthy --wait --timeout 300 scm; then
  echo "timeout reached by waiting of scm to get healthy"
  exit 1
fi


TRUSTSTORE="${SMEAGOL_HOME}/truststore.jks"
create_truststore.sh "${TRUSTSTORE}" > /dev/null

java -Djava.awt.headless=true \
  -Djava.net.preferIPv4Stack=true \
  -Djavax.net.ssl.trustStore="${TRUSTSTORE}" \
  -Djavax.net.ssl.trustStorePassword=changeit \
  -jar /app/smeagol-app.jar
