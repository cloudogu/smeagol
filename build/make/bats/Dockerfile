ARG BATS_BASE_IMAGE
ARG BATS_TAG

FROM ${BATS_BASE_IMAGE:-bats/bats}:${BATS_TAG:-1.12.0}

# Make bash more findable by scripts and tests
RUN apk add make git bash
# suppress git "detected dubious ownership" error/warning for repos which are checked out later
RUN git config --global --add safe.directory /workspace
# Add unprivileged users so Jenkins CI has less problems with the UID mapping inside this container. This avoids
# undesired warnings (like "unknown ID 1002") in BATS assertions, failing assert_output tests
RUN set -x && \
  addgroup -S -g 1000 jenkins && \
  adduser -S -h /home/jenkins -s /bin/bash -G jenkins -u 1000 jenkins && \
  adduser -S -h /home/jenkins -s /bin/bash -G jenkins -u 1001 jenkins1 && \
  adduser -S -h /home/jenkins -s /bin/bash -G jenkins -u 1002 jenkins2
