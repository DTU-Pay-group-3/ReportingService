package behaviourtests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import reporting.service.ReportingService;
import reporting.service.LoggedTransaction;

import java.math.BigDecimal;
import java.util.List;

public class ReportingServiceSteps {
	MessageQueue queue = mock(MessageQueue.class);

	ReportingService reportingService = new ReportingService(queue);
	LoggedTransaction expected;
	LoggedTransaction otherTransaction;

	List<LoggedTransaction> result;

	private String customerId, merchantId;

    @When("a {string} event for a transaction is received")
    public void aEventForATransactionIsReceived(String eventName) {
		expected = new LoggedTransaction();
		expected.setTo("");
		expected.setFrom("");
		expected.setToken("");
		expected.setAmount(BigDecimal.valueOf(1000));

		assertFalse(reportingService.getTransactionList().contains(expected));
		reportingService.handleMoneyTransferred(new Event(eventName,new Object[] {expected}));
    }

    @Then("the transaction is logged")
    public void theTransactionIsLogged() {
		assertTrue(reportingService.getTransactionList().contains(expected));
    }

	@Given("a customer with id {string} exists")
	public void aCustomerWithIdExists(String customerId) {
		this.customerId = customerId;
	}
	@And("a merchant with id {string} exists")
	public void aMerchantWithIdExists(String merchantId) {
		this.merchantId = merchantId;
	}

	@And("a transaction made by the customer has been logged")
	public void aTransactionMadeByTheCustomerHasBeenLogged() {
		expected = new LoggedTransaction(BigDecimal.valueOf(1000), customerId, merchantId, "xyz");
		reportingService.getTransactionList().add(expected);
	}

	@And("another transaction by two other parts has been logged")
	public void anotherTransactionByTwoOtherPartsHasBeenLogged() {
		expected = new LoggedTransaction(BigDecimal.valueOf(1000), customerId, merchantId, "xyz");
		otherTransaction = new LoggedTransaction(BigDecimal.valueOf(1000), "someCustomer", "someMerchant", "abc");
		reportingService.getTransactionList().add(expected);
		reportingService.getTransactionList().add(otherTransaction);
	}

	@When("a {string} event for the customer is received")
	public void aEventForTheCustomerIsReceived(String ev) {
		result = reportingService.handleReportCustomerRequested(new Event(ev,new Object[] {customerId}));
	}

	@When("a {string} event for the merchant is received")
	public void aEventForTheMerchantIsReceived(String ev) {
		result = reportingService.handleReportMerchantRequested(new Event(ev,new Object[] {merchantId}));
	}

	@When("a {string} event for the manager is received")
	public void aEventForTheManagerIsReceived(String ev) {
		result = reportingService.handleReportManagerRequested(new Event(ev,new Object[] {}));
	}

	@Then("a report for the customer is generated")
	public void aReportForTheCustomerIsGenerated() {
		LoggedTransaction firstResult = result.get(0);
		assertEquals(customerId, firstResult.getFrom());
		assertEquals(merchantId, firstResult.getTo());
		assertEquals(BigDecimal.valueOf(1000), firstResult.getAmount());
	}

	@Then("a report for the merchant is generated")
	public void aReportForTheMerchantIsGenerated() {
		LoggedTransaction firstResult = result.get(0);
		assertTrue(firstResult.getFrom().isBlank());
		assertEquals(merchantId, firstResult.getTo());
		assertEquals(BigDecimal.valueOf(1000), firstResult.getAmount());
	}

	@Then("a report for the manager is generated")
	public void aReportForTheManagerIsGenerated() {
		//Manager can see all transactions
		assertTrue(result.contains(expected));
		assertTrue(result.contains(otherTransaction));
	}

	@And("a {string} event is sent")
	public void aEventIsSent(String ev) {
		var event = new Event(ev, new Object[] {result});
		verify(queue).publish(event);
	}
}