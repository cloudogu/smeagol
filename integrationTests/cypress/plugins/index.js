const cucumber = require('cypress-cucumber-preprocessor').default
const doguTestLibrary = require('@cloudogu/dogu-integration-test-library')

/**
 * @type {Cypress.PluginConfig}
 */
module.exports = (on, config) => {
  on('file:preprocessor', cucumber())
  config = doguTestLibrary.configure(config)
  return config
}
