FROM registry.cloudogu.com/official/java:8u102-1
MAINTAINER Sebastian Sdorra <sebastian.sdorra@cloudogu.com>

COPY dist /app
COPY ces-startup.sh /app/startup.sh
ENV SMEAGOL_HOME=/var/lib/smeagol \
    SMEAGOL_STATIC_PATH=/app/webapp \
		SMEAGOL_GEM_PATH=/app/rubygems \
		SERVICE_TAGS=webapp

VOLUME /var/lib/smeagol
CMD /app/startup.sh
