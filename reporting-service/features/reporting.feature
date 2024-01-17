Feature: Report generation feature

	Scenario: Generate report for customer
		Given there is a customer with id "1122330000"
		And there is a merchant with id "3322119999"
		And the customer has made a transaction to the merchant with amount 1000
		When the customer requests a report
		Then the "ReportCustomerRequested" event is sent for the customer
		When the "ReportGenerated" event is then returned
		Then a list of the customer's transactions are returned

	Scenario: Generate report for merchant
		Given there is a customer with id "1122330000"
		And there is a merchant with id "3322119999"
		And the customer has made a transaction to the merchant with amount 1000
		When the merchant requests a report
		Then the "ReportMerchantRequested" event is sent for the merchant
		When the "ReportGenerated" event is then returned
		Then a list of the merchant's transactions are returned

	Scenario: Generate report for manager
		Given there is a customer with id "1122330000"
		And there is a merchant with id "3322119999"
		And the customer has made a transaction to the merchant with amount 1000
		When the manager requests a report
		Then the "ReportManagerRequested" event is sent for the manager
		When the "ReportGenerated" event is then returned
		Then a list of all transactions are returned