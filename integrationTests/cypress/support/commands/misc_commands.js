// ***********************************************
// commands for the login/logout flow
// ***********************************************
let doguName = "smeagol"

/**
 * Log the test user in the cas.
 */
const login = (username, password) => {
    cy.visit("/" + doguName)

    cy.clickWarpMenuCheckboxIfPossible()

    cy.get('input[name="username"]').type(username)
    cy.get('input[name="password"]').type(password)
    cy.get('button[name="submit"]').click()
}


/**
 * Log the admin user defined in the ces_admin_data.json in the cas.
 */
const loginAdmin = () => {
    cy.fixture("ces_admin_data").then(function (admin) {
        cy.visit("/" + doguName)

        cy.clickWarpMenuCheckboxIfPossible()

        cy.get('input[name="username"]').type(admin.username)
        cy.get('input[name="password"]').type(admin.password)
        cy.get('button[name="submit"]').click()
    })
}

/**
 * Log the testuser out of the cas.
 */
const logout = () => {
    cy.visit("/cas/logout")
    cy.visit("/" + doguName)
}

/**
 * Clicks the button of the warp menu
 */
const clickWarpMenuButton = () => {
    cy.get('button[id="warp-menu-warpbtn"]').click()
}

/**
 * Log the testuser out of the cas via click on link in warp menu.
 */
const logoutViaWarpMenu = () => {
    cy.get('a[href="/cas/logout').click()
}

/**
 * Handles the warp menu tooltip by clicking the 'do not show again' checkbox on the first time.
 */
const clickWarpMenuCheckboxIfPossible = () => {
    cy.get('div[id="warp-menu-container"]').then(function (container) {
        let warpContainer = container.children( ".warp-menu-column-tooltip")
        if (warpContainer.length == 1) {
            cy.get('input[type="checkbox"]').click(true)
        }
    })
}

Cypress.Commands.add("login", login)
Cypress.Commands.add("loginAdmin", loginAdmin)
Cypress.Commands.add("logout", logout)
Cypress.Commands.add("logoutViaWarpMenu", logoutViaWarpMenu)
Cypress.Commands.add("clickWarpMenuCheckboxIfPossible", clickWarpMenuCheckboxIfPossible)
Cypress.Commands.add("clickWarpMenuButton", clickWarpMenuButton)
