FROM openjdk:11.0.10-jdk as builder

ENV SMEAGOL_DIR=/usr/src/smeagol
COPY mvnw pom.xml package.json yarn.lock .prettierrc ${SMEAGOL_DIR}/
COPY .mvn ${SMEAGOL_DIR}/.mvn
# We resolve dependencies before copying src so we profit from dockers caching behavior
RUN set -x \
 && cd ${SMEAGOL_DIR} \
 && ./mvnw dependency:resolve
COPY src ${SMEAGOL_DIR}/src
RUN set -x \
 && cd ${SMEAGOL_DIR} \
 && ./mvnw package

FROM registry.cloudogu.com/official/java:11.0.5-4
LABEL NAME="official/smeagol" \
      VERSION="0.7.0-1" \
      maintainer="Sebastian Sdorra <sebastian.sdorra@cloudogu.com>"

ENV SERVICE_TAGS=webapp \
    SMEAGOL_HOME=/var/lib/smeagol

COPY --from=builder /usr/src/smeagol/target/smeagol.war /app/smeagol.war
COPY resources/ /

VOLUME ${SMEAGOL_HOME}
EXPOSE 8080

HEALTHCHECK CMD doguctl healthy smeagol || exit 1

WORKDIR /app
CMD /startup.sh
