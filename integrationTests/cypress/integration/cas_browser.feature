Feature: CAS handling login and logout via Browser

  Scenario: logged out user is redirected to CAS login page
    Given the user is logged out of the CES
    When the user opens the dogu start page
    Then the user is redirected to the CAS login page

  Scenario: logged out user can log in to the dogu
    Given the user is logged out of the CES
    When the user opens the dogu start page
    And the user types in correct login credentials
    And the user presses the login button
    Then the user gets logged in to the dogu

  Scenario: logged out user can not log in to the dogu with wrong credentials
    Given the user is logged out of the CES
    When the user opens the dogu start page
    And the user types in wrong login credentials
    And the user presses the login button
    Then the login page informs user about invalid credentials

  Scenario: logged in user can log out via warp menu
    Given the user is logged in to the CES
    When the user opens the warp menu
    And the user clicks the logout button in the warp menu
    Then the user is logged out of the dogu