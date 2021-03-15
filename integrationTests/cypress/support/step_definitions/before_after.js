const {
    Before,
    After
} = require("cypress-cucumber-preprocessor/steps");

/**
 * Contains instructions that are executed before every test
 */
Before(() => {
    cy.fixture("testuser_data").then(function (testUser) {
        cy.usermgtTryDeleteUser(testUser.username)
        cy.log("Creating test user")
        cy.usermgtCreateUser(testUser.username, testUser.givenname, testUser.surname, testUser.displayName, testUser.mail, testUser.password)
    })
});

/**
 * Contains instructions that are executed after every test
 */
After(() => {
    cy.fixture("testuser_data").then(function (testUser) {
        cy.log("Removing test user")
        cy.usermgtDeleteUser(testUser.username)
    })
});