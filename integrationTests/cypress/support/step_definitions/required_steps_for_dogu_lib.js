const {
  When,
  Then
} = require("@badeball/cypress-cucumber-preprocessor");
const env = require('@cloudogu/dogu-integration-test-library/lib/environment_variables')

// Loads all steps from the dogu integration library into this project
const doguTestLibrary = require('@cloudogu/dogu-integration-test-library')
doguTestLibrary.registerSteps()

//Implement all necessary steps fore dogu integration test library
When(/^the user clicks the dogu logout button$/, function () {
  cy.get('.nav a').click();
});

Then(/^the user has no administrator privileges in the dogu$/, function () {
  // Does nothing as Smeagol does not differentiate between admins and non-admins. Only the assigned privileges in the
  // scm manager make a difference
});

Then(/^the user has administrator privileges in the dogu$/, function () {
  // Does nothing as Smeagol does not differentiate between admins and non-admins. Only the assigned privileges in the
  // scm manager make a difference
});
