Feature: Adding hashtags to activities

  Background:
    Given I registered a user with email "poly@pocket.com" and password "Password1"
    And I log in as a user with email "poly@pocket.com" and password "Password1"

  @U15
  #AC1
  Scenario: Create an activity with hashtags
    Given there are no activities in the database
    When I create an activity "Running" with hashtags
      | Hashtag      |
      | #somehashtag |
      | #123abc      |
    Then I have an activity "Running" with hashtags
      | Hashtag      |
      | #somehashtag |
      | #123abc      |

  @U15
  #AC1
  Scenario: Create an activity with hashtags with case sensitivity
    Given there are no activities in the database
    When I create an activity "Running" with hashtags
      | Hashtag      |
      | #someHashTag |
    Then I have an activity "Running" with hashtags
      | Hashtag      |
      | #somehashtag |

  @15
  #AC1
  Scenario: Attempt to create an activity with hashtags that contain spaces
    Given there are no activities in the database
    When I create an activity "Running" with hashtags
      | Hashtag        |
      | #some hash tag |
    Then I do not have an activity "Running" and receive "400" status code

  @15
  #AC1
  Scenario: Attempt to create an activity with hashtags that contain symbols
    Given there are no activities in the database
    When I create an activity "Running" with hashtags
      | Hashtag           |
      | #some%*&^(*^*&%^( |
    Then I do not have an activity "Running" and receive "400" status code

  @15
  #AC2
  Scenario: Searching for hashtags for auto complete
    Given I create an activity "Running" with hashtags
      | Hashtag      |
      | #someHashTag |
      | #someStuff   |
      | #someNumber  |
      | #someThings  |
      | #nothing     |
      | #notEvery    |
      | #everyday    |
    When I search for hashtag "some"
    Then I get hashtag search results containing
      | Hashtag      |
      | someHashTag |
      | someStuff   |
      | someNumber  |
      | someThings  |

  @15
  #AC2
  Scenario: Searching for hashtags for auto complete on frequency
    Given I create an activity "Running" with hashtags
      | Hashtag   |
      | #nothing  |
      | #notEvery |
      | #everyday |
    And I create an activity "Walking" with hashtags
      | Hashtag   |
      | #nothing  |
      | #notEvery |
      | #tomorrow |
    And I create an activity "Jumping" with hashtags
      | Hashtag   |
      | #not      |
      | #notEvery |
      | #alert    |

    When I search for hashtag "not"
    Then I get hashtag search results in order
      | Hashtag   |
      | notEvery |
      | nothing  |
      | not      |

  @15
  #AC2
  Scenario: Searching for hashtags for auto complete on two letters
    Given I create an activity "Running" with hashtags
      | Hashtag      |
      | #someHashTag |
      | #someStuff   |
      | #someNumber  |
      | #someThings  |
      | #nothing     |
      | #notEvery    |
      | #everyday    |
      | #so          |

    When I search for hashtag "so"
    Then I get hashtag search results containing
      | Hashtag |
      | so   |

  @15
  #AC4
  Scenario: Searching for activity by hashtag
    Given I create an activity "Running" with hashtags
      | Hashtag      |
      | #someHashTag |
    And I create an activity "Walking" with hashtags
      | Hashtag      |
      | #someHashTag |
    And I create an activity "Jumping" with hashtags
      | Hashtag |
      | #abc    |
    When I search for activity by hashtag "someHashTag"
    Then I get the activities in order
      | ActivityName |
      | Walking      |
      | Running      |


  @15
  #AC3
  Scenario: Create an activity with more than 30 hashtag will fail
    Given there are no activities in the database
    When I create an activity "Running" with hashtags
      | Hashtag      |
      | #someHashTag |
      | #someStuff   |
      | #someNumber  |
      | #someThings  |
      | #nothing     |
      | #notEvery    |
      | #everyday    |
      | #a           |
      | #b           |
      | #c           |
      | #d           |
      | #e           |
      | #f           |
      | #g           |
      | #h           |
      | #a1          |
      | #b1          |
      | #c1          |
      | #d1          |
      | #e1          |
      | #f1          |
      | #g1          |
      | #h1          |
      | #a2          |
      | #b2          |
      | #c2          |
      | #d2          |
      | #e2          |
      | #f2          |
      | #g2          |
      | #h2          |
    Then I do not have an activity "Running" and receive "400" status code

  @15
  #AC3
  Scenario: Create an activity with 30 hashtags will work
    Given there are no activities in the database
    When I create an activity "Running" with hashtags
      | Hashtag      |
      | #someHashTag |
      | #someStuff   |
      | #someNumber  |
      | #someThings  |
      | #nothing     |
      | #notEvery    |
      | #a           |
      | #b           |
      | #c           |
      | #d           |
      | #e           |
      | #f           |
      | #g           |
      | #h           |
      | #a1          |
      | #b1          |
      | #c1          |
      | #d1          |
      | #e1          |
      | #f1          |
      | #g1          |
      | #h1          |
      | #a2          |
      | #b2          |
      | #c2          |
      | #d2          |
      | #e2          |
      | #f2          |
      | #g2          |
      | #h2          |
    Then I have an activity "Running" with hashtags
      | Hashtag      |
      | #someHashTag |
      | #someStuff   |
      | #someNumber  |
      | #someThings  |
      | #nothing     |
      | #notEvery    |
      | #a           |
      | #b           |
      | #c           |
      | #d           |
      | #e           |
      | #f           |
      | #g           |
      | #h           |
      | #a1          |
      | #b1          |
      | #c1          |
      | #d1          |
      | #e1          |
      | #f1          |
      | #g1          |
      | #h1          |
      | #a2          |
      | #b2          |
      | #c2          |
      | #d2          |
      | #e2          |
      | #f2          |
      | #g2          |
      | #h2          |

  @15
  #AC3
  Scenario: Edit an activity and add more than 30 hashtag will fail
    Given I create an activity "Running" with no hashtags
    When I edit an activity "Running" and add hashtags
      | Hashtag      |
      | #someHashTag |
      | #someStuff   |
      | #someNumber  |
      | #someThings  |
      | #nothing     |
      | #notEvery    |
      | #everyday    |
      | #a           |
      | #b           |
      | #c           |
      | #d           |
      | #e           |
      | #f           |
      | #g           |
      | #h           |
      | #a1          |
      | #b1          |
      | #c1          |
      | #d1          |
      | #e1          |
      | #f1          |
      | #g1          |
      | #h1          |
      | #a2          |
      | #b2          |
      | #c2          |
      | #d2          |
      | #e2          |
      | #f2          |
      | #g2          |
      | #h2          |
    Then I will receive "400" status code
    And the activity "Running" has no hashtags

  @15
  #AC3
  Scenario: Edit an activity and add 30 hashtags will work
    Given I create an activity "Running" with no hashtags
    When I edit an activity "Running" and add hashtags
      | Hashtag      |
      | #someHashTag |
      | #someStuff   |
      | #someNumber  |
      | #someThings  |
      | #nothing     |
      | #notEvery    |
      | #a           |
      | #b           |
      | #c           |
      | #d           |
      | #e           |
      | #f           |
      | #g           |
      | #h           |
      | #a1          |
      | #b1          |
      | #c1          |
      | #d1          |
      | #e1          |
      | #f1          |
      | #g1          |
      | #h1          |
      | #a2          |
      | #b2          |
      | #c2          |
      | #d2          |
      | #e2          |
      | #f2          |
      | #g2          |
      | #h2          |
    Then I have an activity "Running" with hashtags
      | Hashtag      |
      | #someHashTag |
      | #someStuff   |
      | #someNumber  |
      | #someThings  |
      | #nothing     |
      | #notEvery    |
      | #a           |
      | #b           |
      | #c           |
      | #d           |
      | #e           |
      | #f           |
      | #g           |
      | #h           |
      | #a1          |
      | #b1          |
      | #c1          |
      | #d1          |
      | #e1          |
      | #f1          |
      | #g1          |
      | #h1          |
      | #a2          |
      | #b2          |
      | #c2          |
      | #d2          |
      | #e2          |
      | #f2          |
      | #g2          |
      | #h2          |









