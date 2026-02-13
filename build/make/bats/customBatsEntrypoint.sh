#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o pipefail

targetReportDir="${PWD}"/target/shell_test_reports
uidgid=1000:1000
exitcode=0
"$@" || exitcode=$?
echo "Resetting file ownership to ${uidgid} in ${targetReportDir}/"
chown -R ${uidgid} "${targetReportDir}"/*
echo "exiting with code ${exitcode}"
exit ${exitcode}