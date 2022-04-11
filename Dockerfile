FROM openjdk:11.0.10-jdk as builder

ENV SMEAGOL_DIR=/usr/src/smeagol
COPY mvnw pom.xml package.json yarn.lock .prettierrc ${SMEAGOL_DIR}/
COPY .mvn ${SMEAGOL_DIR}/.mvn
RUN git config --global url."https://github.com/".insteadOf git://github.com/
# We resolve dependencies before copying src so we profit from dockers caching behavior
RUN set -x \
 && cd ${SMEAGOL_DIR} \
 && ./mvnw dependency:resolve
COPY src ${SMEAGOL_DIR}/src
RUN set -x \
 && cd ${SMEAGOL_DIR} \
 && ./mvnw package -Dmaven.wagon.http.pool=false



FROM registry.cloudogu.com/official/java:11.0.14-3
LABEL NAME="official/smeagol" \
      VERSION="1.6.2-2" \
      maintainer="Sebastian Sdorra <sebastian.sdorra@cloudogu.com>"

ENV SERVICE_TAGS=webapp \
    SMEAGOL_HOME=/var/lib/smeagol

COPY --from=builder /usr/src/smeagol/target/smeagol.war /app/smeagol.war
COPY resources/ /

RUN set -o errexit \
 && set -o nounset \
 && set -o pipefail \
 && apk update \
 && apk upgrade

VOLUME ${SMEAGOL_HOME}
EXPOSE 8080

HEALTHCHECK CMD doguctl healthy smeagol || exit 1

WORKDIR /app
CMD /startup.sh
