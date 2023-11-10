# DO NOT EDIT! These feature files are provided by the dogu integration test library.
Feature: Automatic grant of privileges when logging into a dogu

  @requires_testuser
  Scenario: ces user with default privileges has default privileges in the dogu
    Given the user is not member of the admin user group
    When the user logs into the CES
    Then the user has no administrator privileges in the dogu

  @requires_testuser
  Scenario: ces user with admin privileges has admin privileges in the dogu
    Given the user is member of the admin user group
    When the user logs into the CES
    Then the user has administrator privileges in the dogu

  @requires_testuser
  Scenario: ces user without admin privileges has no admin privileges in the dogu
    Given the user is not member of the admin user group
    When the user logs into the CES
    Then the user has no administrator privileges in the dogu

  @requires_testuser
  Scenario: internal dogu admin account is demoted after login of non admin
    Given the user has an internal default dogu account
    And the user is not member of the admin user group
    When the user logs into the CES
    Then the user has no administrator privileges in the dogu

  @requires_testuser
  Scenario: internal dogu default account is promoted after login of admin
    Given the user has an internal default dogu account
    And the user is member of the admin user group
    When the user logs into the CES
    Then the user has administrator privileges in the dogu