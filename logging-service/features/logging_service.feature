Feature: Logging money transfer

  Scenario: Log money transferred event
    When a "OnMoneyTransferred" event for a transaction is received
    Then the transaction is logged