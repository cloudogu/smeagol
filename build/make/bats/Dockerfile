ARG BATS_BASE_IMAGE
ARG BATS_TAG

FROM ${BATS_BASE_IMAGE:-bats/bats}:${BATS_TAG:-1.11.0}

# Make bash more findable by scripts and tests
RUN apk add make git bash
# suppress git "detected dubious ownership" error/warning for repos which are checked out later
RUN git config --global --add safe.directory /workspace