Feature: Report generation feature

	Scenario: Generate report
		Given there is a customer with id "1122330000"
		And there is a merchant with id "3322119999"
		And the customer has made a transaction to the merchant with amount 1000
		When the customer requests a report
		Then the "ReportCustomerRequested" event is sent
		When the "ReportGenerated" event is then returned
		Then a list of the customer's transactions are returned