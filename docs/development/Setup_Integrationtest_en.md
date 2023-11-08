# Setup for the integration tests

This section describes the steps required to properly run the integration tests.

## Requirements

* It is necessary to install the program `yarn

## Configuration

In order for all integration tests to work properly, some data must be configured beforehand.

**integrationTests/cypress.config.json** [[Link to file](../../integrationTests/cypress.config.js)]

1) The base URL must be adapted to the host system.
   For this the field `baseUrl` has to be adjusted to the host FQDN (`https://local.cloudogu.com`).
2) Other aspects must be configured.
   These are set as environment variables in the `cypress.config.json`:
- `DoguName` - Determines the name of the current dogu and will be used in routing.
- `MaxLoginRetries` - Determines the number of login attempts before a test fails.
- `AdminUsername` - The username of the CES admin.
- `AdminPassword` - The password of the CES admin.
- `AdminGroup` - The user group for CES administrators.

A sample `cypress.json` looks like this:
```json
{
   "baseUrl": "https://192.168.56.2",
   "env": {
      "DoguName": "redmine",
      "MaxLoginRetries": 3,
      "AdminUsername":  "ces-admin",
      "AdminPassword":  "ecosystem2016",
      "AdminGroup":  "CesAdministrators"
   }
}
```

## Starting the integration tests

The integration tests can be started in two ways:

1. with `yarn cypress run` the tests start only in the console without visual feedback.
   This mode is useful when execution is the main focus.
   For example, in a Jenkins pipeline.

1. `yarn cypress open` starts an interactive window where you can run, visually observe and debug the tests.
   This mode is especially useful when developing new tests and finding bugs.

Translated with www.DeepL.com/Translator (free version)
