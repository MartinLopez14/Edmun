Feature: Create an user admin account that can do any functionality available within the system
  
  Background:
    Given There is a normal user with email "adminwashere2@test.com" registered in the database
    And There is a user admin with email "useradmin@test.com" registered in the database
    And I log in as a user admin with email "useradmin@test.com"
    And I receive response status code 200

  @13
  Scenario: User admin can edit another user's primary email
    When I edit the primary email of the user to "adminwashere@test.com"
    Then primary email of the user is "adminwashere@test.com"

  @13
  Scenario: User admin can edit another user's additional email
    When I add "adminwashere3@test.com" to the user's list of additional email
    Then "adminwashere3@test.com" is in the user's list of additional email

  @13
  Scenario: User admin can edit another user's password
    When I change the user's password to "Admin123" with old password
    And I log out of the user admin account
    Then I can log into "adminwashere2@test.com" with password "Admin123"
    And I receive response status code 200

  @13
  Scenario: User admin can give another user admin rights
    When I give "adminwashere2@test.com" admin rights
    Then "adminwashere2@test.com" has user admin role

  @13
  Scenario: User admin can remove user admin rights
    Given "adminwashere3@test.com" has admin rights
    When I remove user admin rights from "adminwashere3@test.com"
    Then "adminwashere3@test.com" is a normal user

  @13
  Scenario: User admin can edit user's password without old user password
    When I change the password of "adminwashere2@test.com" to "Admin123"
    Then I can log into "adminwashere2@test.com" with password "Admin123"

  @13
  Scenario: User admin cannot remove admin role from default admin
    When I attempt to remove admin rights from "test@test.com"
    Then "test@test.com" still has admin role
