Feature: Logging money transfer

  Scenario: Log money transferred event
    When a "MoneyTransferred" event for a transaction is received
    Then the transaction is logged

  Scenario: A customer requests a report
    Given a customer with id "1122330000" exists
    And a transaction made by the customer has been logged
    When a "ReportCustomerRequested" event is received
    Then a report is generated
    And a "ReportGenerated" event is sent
