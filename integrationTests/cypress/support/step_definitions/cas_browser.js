const {
    Given,
    When,
    Then
} = require("cypress-cucumber-preprocessor/steps");

//
//
// Given
//
//

Given(/^the user is logged in to the CES$/, function () {
    cy.fixture("testuser_data").then(function (testUser) {
        cy.login(testUser.username, testUser.password)
    })
});

Given(/^the user is logged out of the CES$/, function () {
    cy.logout()
});

//
//
// When
//
//

When(/^the user opens the dogu start page$/, function () {
    cy.visit("/smeagol")
});

When(/^the user types in wrong login credentials$/, function () {
    cy.get('input[type="checkbox"]').click()
    cy.fixture('testuser_data').then(userdata => {
        cy.get('input[name="username"]').type("RaNd0mUSR_?123")
        cy.get('input[name="password"]').type("RaNd0mPWöäü_?123")
    });
});

When(/^the user types in correct login credentials$/, function () {
    cy.get('input[type="checkbox"]').click()
    cy.fixture('testuser_data').then(userdata => {
        cy.get('input[name="username"]').type(userdata.username)
        cy.get('input[name="password"]').type(userdata.password)
    });
});

When(/^the user presses the login button$/, function () {
    cy.get('input[name="submit"]').click()
});

When(/^the user clicks the logout button$/, function () {
    cy.get('a[href="/smeagol/api/v1/logout"]').click()
    cy.url().should('contain', Cypress.config().baseUrl+"/cas/logout")
});

When(/^the user opens the CAS logout page$/, function () {
    cy.logout()
});

When(/^the user opens the warp menu$/, function () {
    cy.clickWarpMenuButton()
});

When(/^the user clicks the logout button in the warp menu$/, function () {
    cy.logoutViaWarpMenu()
});

//
//
// Then
//
//

Then(/^the user gets logged in to the dogu$/, function () {
    cy.url().should('contain', Cypress.config().baseUrl+"/smeagol")
});

Then(/^the user is redirected to the CAS login page$/, function () {
    cy.url().should('contain', Cypress.config().baseUrl+"/cas/login")
});

Then(/^the user is logged out of the dogu$/, function () {
    // Verify logout by visiting dogu => should redirect to loginpage
    cy.visit("/smeagol")
    cy.url().should('contain', Cypress.config().baseUrl+"/cas/login")
});

Then(/^the login page informs user about invalid credentials$/, function () {
    cy.get('div[id="msg"]').contains("Invalid credentials.")
});