const {
  When,
} = require("cypress-cucumber-preprocessor/steps");

//
//
// When
//
//

When(/^the user clicks the logout button$/, function () {
  cy.get('.container > div > div').click();
  cy.get('.nav a').click();
});
