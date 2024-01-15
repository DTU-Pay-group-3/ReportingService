Feature: Logging money transfer

  Scenario: Log money transferred event
    When a "MoneyTransferred" event for a transaction is received
    Then the transaction is logged

  Scenario: A customer requests a report
    Given a customer with id "1122330000" exists
    And a merchant with id "3322119999" exists
    And a transaction made by the customer has been logged
    When a "ReportCustomerRequested" event for the customer is received
    Then a report for the customer is generated
    And a "ReportGenerated" event is sent

  Scenario: A merchant requests a report
    Given a customer with id "1122330000" exists
    And a merchant with id "3322119999" exists
    And a transaction made by the customer has been logged
    When a "ReportMerchantRequested" event for the merchant is received
    Then a report for the merchant is generated
    And a "ReportGenerated" event is sent

  Scenario: A manager requests a report
    Given a customer with id "1122330000" exists
    And a merchant with id "3322119999" exists
    And a transaction made by the customer has been logged
    And another transaction by two other parts has been logged
    When a "ReportManagerRequested" event for the manager is received
    Then a report for the manager is generated
    And a "ReportGenerated" event is sent
