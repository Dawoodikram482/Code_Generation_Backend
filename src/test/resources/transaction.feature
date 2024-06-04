Feature: Everything associated with Transactions
Scenario: Endpoint Check
  Given I have a valid login credentials
  And I call the application login endpoint
  And I receive a token
  When I call the application transaction endpoint
  Then I get HTTP status 200
  And I get 0 elements in the list