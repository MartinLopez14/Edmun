Feature: Adding hashtags to activities

  Background:
    Given I registered a user with email "poly@pocket.com" and password "Password1"
    And I log in as a user with email "poly@pocket.com" and password "Password1"

  @U17
  Scenario: I can view a public activity
    Given There is an activity "My cool activity" with "public" access
    When user "poly@pocket.com" views "My cool activity"
    Then I have response status "200" on activity "My cool activity"

  @U17
  Scenario: I can not view a private activity
    Given There is an activity "My cool activity" with "private" access
    When user "poly@pocket.com" views "My cool activity"
    Then I have response status "403" on activity "My cool activity"

  @U17
  Scenario: I can not view a restricted activity
    Given There is an activity "My cool activity" with "restricted" access
    When user "poly@pocket.com" views "My cool activity"
    Then I have response status "403" on activity "My cool activity"

  @U17
  Scenario: I can view a restricted activity with access role
    Given There is an activity "My cool activity" with "restricted" access
    And user "poly@pocket.com" has role "access" on activity "my cool activity"
    When user "poly@pocket.com" views "My cool activity"
    Then I have response status "200" on activity "My cool activity"

  @U17
  Scenario: I can view my own private activity
    Given user "poly@pocket.com" creates activity "My cool activity" with "private" access
    When user "poly@pocket.com" views "My cool activity"
    Then I have response status "200" on activity "My cool activity"

  @U17
  Scenario: I can view list of users I have shared my activity with
    Given user "poly@pocket.com" creates activity "My cool activity" with "restricted" access
    When user "poly@pocket.com" adds user roles
       | UserEmail | Role |
       | john@john.com      | organiser |
       | tester@testboy.com | access |
    Then user "poly@pocket.com" gets users of role "access" for activity "My cool activity" should show
      | UserEmail          |
      | john@john.com      |
      | tester@testboy.com |

  @U17
  Scenario: I can change the privacy option of my activity from public to private
    Given user "poly@pocket.com" creates activity "My cool activity" with "public" access
    When  user "poly@pocket.com" sets activity "My cool activity" to "private" access
    And  user "john@john.com" views "My cool activity"
    Then  I have response status "403" on activity "My cool activity"

  @U17
  Scenario: I can change the privacy option of my activity from private to public
    Given user "poly@pocket.com" creates activity "My cool activity" with "private" access
    When  user "poly@pocket.com" sets activity "My cool activity" to "public" access
    And  user "john@john.com" views "My cool activity"
    Then  I have response status "200" on activity "My cool activity"

  @U17
  Scenario: If I remove a user from my activity they will lose their role
    Given user "poly@pocket.com" creates activity "My cool activity" with "restricted" access
    And user "poly@pocket.com" adds user roles
      | UserEmail | Role |
      | john@john.com      | organiser |
      | tester@testboy.com | access |
    When user "poly@pocket.com" removes user
      | UserEmail |
      | john@john.com |
    Then user "poly@pocket.com" gets users of role "access" for activity "My cool activity" should show
      | UserEmail          |
      | tester@testboy.com |

  @U17
  Scenario: I can increase a users role to organiser in my activity
    Given user "poly@pocket.com" creates activity "My cool activity" with "restricted" access
    And user "poly@pocket.com" adds user roles
      | UserEmail | Role |
      | john@john.com      | organiser |
      | tester@testboy.com | access |
    When user "poly@pocket.com" adds user roles
      | UserEmail | Role |
      | tester@testboy.com | organiser |
    Then user "poly@pocket.com" gets users of role "organiser" for activity "My cool activity" should show
      | UserEmail          |
      | john@john.com      |
      | tester@testboy.com |

  @U17
  Scenario: I can remove an organiser
    Given user "poly@pocket.com" creates activity "My cool activity" with "restricted" access
    And user "poly@pocket.com" adds user roles
      | UserEmail | Role |
      | john@john.com      | organiser |
      | tester@testboy.com | organiser |
    When user "poly@pocket.com" removes user roles
      | UserEmail | Role |
      | tester@testboy.com | organiser |
    Then user "poly@pocket.com" gets users of role "organiser" for activity "My cool activity" should show
      | UserEmail          |
      | john@john.com      |

  @U17
  Scenario: I can decrease an organiser to a follower
    Given user "poly@pocket.com" creates activity "My cool activity" with "restricted" access
    And user "poly@pocket.com" adds user roles
      | UserEmail | Role |
      | john@john.com      | organiser |
      | tester@testboy.com | organiser |
    When user "poly@pocket.com" changes user roles
      | UserEmail | Role | NewRole |
      | tester@testboy.com | organiser | follower |
    Then user "poly@pocket.com" gets users of role "organiser" for activity "My cool activity" should show
      | UserEmail          |
      | john@john.com      |
    And user "poly@pocket.com" gets users of role "follower" for activity "My cool activity" should show
      | UserEmail          |
      | tester@testboy.com      |


  @U17
  Scenario: An organiser can not delete the activity
    Given user "poly@pocket.com" creates activity "My cool activity" with "public" access
    And user "poly@pocket.com" adds user roles
      | UserEmail | Role |
      | john@john.com      | organiser |
    When user "john@john.com" deletes activity "My cool activity"
    Then  I have response status "403" on activity "My cool activity"
    And activity "My cool activity" exists

  @U17
  Scenario: An organiser can not share the activity
    Given user "poly@pocket.com" creates activity "My cool activity" with "restricted" access
    When user "john@john.com" adds user roles
      | UserEmail | Role |
      | john@john.com      | access |
      | tester@testboy.com | access |
    Then  I have response status "403" on activity "My cool activity"
    And user "john@john.com" gets users of role "access" for activity "My cool activity" should show
      | UserEmail |

  @U17
  Scenario: An organiser can edit the activity
    Given user "poly@pocket.com" creates activity "My cool activity" with "public" access
    And user "poly@pocket.com" adds user roles
      | UserEmail | Role |
      | john@john.com      | Organiser |
    When the user "john@john.com" edits activity "My cool activity" description to "My coolest activity"
    Then the activity "My cool activity" should have description "My coolest activity"

  @U17
  Scenario: A participant can not delete the activity
    Given user "poly@pocket.com" creates activity "My cool activity" with "public" access
    And user "john@john.com" adds user roles
      | UserEmail | Role |
      | john@john.com | Participant |
    When user "john@john.com" deletes activity "My cool activity"
    Then  I have response status "403" on activity "My cool activity"
    And activity "My cool activity" exists


  @U17
  Scenario: A participant can not share the activity
    Given user "poly@pocket.com" creates activity "My cool activity" with "restricted" access
    And user "poly@pocket.com" adds user roles
      | UserEmail | Role |
      | john@john.com | participant |
      | john@john.com | access |
    When user "john@john.com" adds user roles
      | UserEmail | Role |
      | tester@testboy.com | access |
    Then  I have response status "403" on activity "My cool activity"
    And user "john@john.com" gets users of role "access" for activity "My cool activity" should show
      | UserEmail     |
      | john@john.com |

  @U17
  Scenario: A participant can not edit the activity
    Given user "poly@pocket.com" creates activity "My cool activity" with "public" access
    And user "john@john.com" adds user roles
      | UserEmail | Role |
      | john@john.com | participant |
    When the user "john@john.com" edits activity "My cool activity" description to "My coolest activity"
    Then  I have response status "403" on activity "My cool activity"

  @U17
  Scenario: A follower can not delete the activity
    Given user "poly@pocket.com" creates activity "My cool activity" with "public" access
    And user "john@john.com" adds user roles
      | UserEmail | Role |
      | john@john.com | follower |
    When user "john@john.com" deletes activity "My cool activity"
    Then  I have response status "403" on activity "My cool activity"
    And activity "My cool activity" exists

  @U17
  Scenario: A follower can not share the activity
    Given user "poly@pocket.com" creates activity "My cool activity" with "restricted" access
    And user "poly@pocket.com" adds user roles
      | UserEmail | Role |
      | john@john.com | follower |
      | john@john.com | access |
    When user "john@john.com" adds user roles
      | UserEmail | Role |
      | tester@testboy.com | access |
    Then  I have response status "403" on activity "My cool activity"
    And user "john@john.com" gets users of role "access" for activity "My cool activity" should show
      | UserEmail     |
      | john@john.com |

  @U17
  Scenario: A follower can not edit the activity
    Given user "poly@pocket.com" creates activity "My cool activity" with "public" access
    And user "john@john.com" adds user roles
      | UserEmail | Role |
      | john@john.com | follower |
    When the user "john@john.com" edits activity "My cool activity" description to "My coolest activity"
    Then  I have response status "403" on activity "My cool activity"










