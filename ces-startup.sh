#!/bin/bash

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
cat > /app/application.yml <<EOF
stage: production
server:
  contextPath: /smeagol
homeDirectory: ${SMEAGOL_HOME}
scm:
  url: https://${FQDN}/scm
cas:
  url: https://${FQDN}/cas
  serviceUrl: https://${FQDN}/smeagol
EOF

if [[ "$(doguctl config "container_config/memory_limit" -d "empty")" == "empty" ]];  then
  echo "Starting Smeagol without memory limits..."
  java -Djava.awt.headless=true \
       -Djava.net.preferIPv4Stack=true \
       -Djavax.net.ssl.trustStore="${TRUSTSTORE}" \
       -Djavax.net.ssl.trustStorePassword=changeit \
       -jar /app/smeagol.war
else
  echo "Starting Smeagol with memory limits..."
  java -Djava.awt.headless=true \
       -Djava.net.preferIPv4Stack=true \
       -Djavax.net.ssl.trustStore="${TRUSTSTORE}" \
       -Djavax.net.ssl.trustStorePassword=changeit \
       -XX:MaxRAMPercentage=85.0 \
       -XX:MinRAMPercentage=50.0 \
       -jar /app/smeagol.war
fi

