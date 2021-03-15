// ***********************************************
// commands for the usermgt
// ***********************************************

// ***********************************************
// REST - /api/users
// ***********************************************

/**
 * Creates the user defined in in the given user data object parameter.
 * A failed request is not tolerated and fails the test.
 * @param {String} username - The username for the user.
 * @param {String} givenname - The real given name of the user.
 * @param {String} surname - The real surname of the user.
 * @param {String} displayName - The displayname for the user.
 * @param {String} mail - The E-Mail for the user.
 * @param {String} password - The password for the user.
 */
const usermgtCreateUser = (username, givenname, surname, displayName, mail, password) => {
    cy.fixture("ces_admin_data.json").then(function (admindata) {
        cy.request({
            method: "POST",
            url: Cypress.config().baseUrl + "/usermgt/api/users/",
            followRedirect: false,
            auth: {
                'user': admindata.adminuser,
                'pass': admindata.adminpassword
            },
            headers: {
                'Content-Type': 'application/json; charset=UTF-8',
            },
            body: {
                'username': username,
                'givenname': givenname,
                'surname': surname,
                'displayName': displayName,
                'mail': mail,
                'password': password,
                'memberOf': []
            }
        }).then((response) => {
            expect(response.status).to.eq(201)
        })
    })
}


/**
 * Deletes the user given by the username.
 * A failed request is not tolerated and fails the test.
 * @param {String} username - The username of the user that should be deleted.
 */
const usermgtDeleteUser = (username) => {
    cy.fixture("ces_admin_data.json").then(function (admindata) {
        cy.request({
            method: "DELETE",
            url: Cypress.config().baseUrl + "/usermgt/api/users/" + username,
            auth: {
                'user': admindata.adminuser,
                'pass': admindata.adminpassword
            }
        }).then((response) => {
            expect(response.status).to.eq(204)
        })
    })
}

/**
 * Tries to deletes the user given by the username.
 * A failed request is tolerated and does not fail the test.
 * @param {String} username - The username of the user that should be deleted.
 */
const usermgtTryDeleteUser = (username) => {
    cy.fixture("ces_admin_data.json").then(function (admindata) {
        cy.request({
            method: "DELETE",
            url: Cypress.config().baseUrl + "/usermgt/api/users/" + username,
            failOnStatusCode: false,
            auth: {
                'user': admindata.adminuser,
                'pass': admindata.adminpassword
            }
        })
    })
}

// /api/users/
Cypress.Commands.add("usermgtCreateUser", usermgtCreateUser)
Cypress.Commands.add("usermgtDeleteUser", usermgtDeleteUser)
Cypress.Commands.add("usermgtTryDeleteUser", usermgtTryDeleteUser)