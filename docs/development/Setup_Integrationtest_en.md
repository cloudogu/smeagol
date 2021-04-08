---
title: "Setup for the Integration-Tests".
---

# Setup for the integration tests

This section describes the steps required to correctly execute the integration tests for smeagol.

## Prerequisites

* It is necessary to install [yarn](https://classic.yarnpkg.com/en/docs/install#debian-stable):
  * `npm install --global yarn`

## Configuration

For all integration tests to work properly, some data must be configured beforehand.

**integrationTests/cypress.json** [[Link to file](../../integrationTests/cypress.json)]

In this file the base-URL has to be adjusted to the host system.
To do this, the `baseUrl` field must be adjusted to the host FQDN (`https://local.cloudogu.com`).

**integrationTests/cypress/fixtures/ces_admin_data.json** [[Link to file](../../integrationTests/cypress/fixtures/ces_admin_data.json)]

In the `ces_admin_data.json` the login information of a CES admin must be entered in the fields `username` and `password`.

## Starting the integration tests

The integration tests can be started in two ways:

1. with `yarn cypress run` the tests start only in the console without visual feedback.
   This mode is useful when execution is the main focus.
   For example, in a Jenkins pipeline.

1. `yarn cypress open` starts an interactive window where you can run, visually observe and debug the tests.
   This mode is especially useful when developing new tests and finding bugs.
