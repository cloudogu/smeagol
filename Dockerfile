FROM registry.cloudogu.com/official/java:8u151-1
MAINTAINER Sebastian Sdorra <sebastian.sdorra@cloudogu.com>
ENV SERVICE_TAGS=webapp \
    SMEAGOL_HOME=/var/lib/smeagol

COPY dist /app
COPY ces-startup.sh /app/startup.sh

VOLUME /var/lib/smeagol
EXPOSE 8080

WORKDIR /app
CMD /app/startup.sh
