# DO NOT EDIT! These feature files are provided by the dogu integration test library.
Feature: Browser-based CAS login and logout functionality

  @requires_testuser
  Scenario: logged out user is redirected to CAS login page
    Given the user is logged out of the CES
    When the user opens the dogu start page
    Then the user is redirected to the CAS login page

  @requires_testuser
  Scenario: logged out user can log in to the dogu
    Given the user is logged out of the CES
    When the user opens the dogu start page
    And the test user logs in with correct credentials
    Then the user is logged in to the dogu

  @requires_testuser
  Scenario: logged out user can not log in to the dogu with wrong credentials
    Given the user is logged out of the CES
    When the user opens the dogu start page
    And the user logs in with wrong credentials
    Then the login page informs the user about invalid credentials

  @requires_testuser
  Scenario: logged in user can log out via logout button (front-channel)
    Given the user is logged into the CES
    When the user opens the dogu start page
    And the user clicks the dogu logout button
    Then the user is logged out of the dogu

  @requires_testuser
  Scenario: logged in user can log out via cas logout page (back-channel)
    Given the user is logged into the CES
    When the user logs out by visiting the cas logout page
    Then the user is logged out of the dogu