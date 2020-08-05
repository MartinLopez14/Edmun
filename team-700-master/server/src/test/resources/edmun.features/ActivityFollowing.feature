Feature: Following an activity

  Background:
    Given I create a user with email "poly@pocket.com" and password "Password1"
    And I log in with email "poly@pocket.com" and password "Password1"

  @U16
  #AC1
  Scenario: Follow a public activity
    Given I create an activity "My cool activity"
    When User follows the activity "My cool activity"
    Then User is a follower of "My cool activity"

  @U16
  #AC2
  Scenario: Unfollowing a public activity
    Given I create an activity "My cool activity"
    And User follows the activity "My cool activity"
    When User unfollows the activity "My cool activity"
    Then User is not a follower of "My cool activity"

  @U16
  #AC3
  Scenario: Following adds to home feed
    Given I create an activity "My cool activity"
    When User follows the activity "My cool activity"
    Then User with email "poly@pocket.com" home feed shows
      | Activity Name |
      | My cool activity |

  @U16
  #AC3
  Scenario: Unfollows adds to home feed
    Given I create an activity "My cool activity"
    And User follows the activity "My cool activity"
    When User unfollows the activity "My cool activity"
    Then User with email "poly@pocket.com" home feed shows
      | Activity Name |
      | My cool activity |
      | My cool activity |

  @U16
  #AC4
  Scenario: Home feed changes
    Given I create an activity "My cool activity"
    And User follows the activity "My cool activity"
    When the activity "My cool activity" has its name changed to "My coolest activity"
    Then User with email "poly@pocket.com" home feed shows
      | Activity Name       |
      | My coolest activity |
      | My coolest activity |