Feature: Browser-based CAS login and logout functionality

  Scenario: logged out user is redirected to CAS login page
    Given the current user is logged out of the CES
    When the user opens the dogu start page
    Then the user is redirected to the CAS login page

  Scenario: logged out user can log in to the dogu
    Given the current user is logged out of the CES
    When the user opens the dogu start page
    And the user types in correct login credentials
    And the user presses the login button
    Then the user is logged in to the dogu

  Scenario: logged out user can not log in to the dogu with wrong credentials
    Given the current user is logged out of the CES
    When the user opens the dogu start page
    And the user types in wrong login credentials
    And the user presses the login button
    Then the login page informs the user about invalid credentials

  Scenario: logged in user can log out via logout button (front-channel)
    Given the admin is logged in to the CES
    When the user opens the dogu start page
    And the user clicks the logout button
    Then the user is logged out of the dogu

  Scenario: logged in user can log out via cas logout page (back-channel)
    Given the admin is logged in to the CES
    When the user opens the CAS logout page
    Then the user is logged out of the dogu
