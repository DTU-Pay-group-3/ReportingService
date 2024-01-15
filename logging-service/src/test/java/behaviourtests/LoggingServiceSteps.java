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
import logging.service.LoggingService;
import logging.service.LoggedTransaction;

import java.math.BigDecimal;

public class LoggingServiceSteps {
	MessageQueue queue = mock(MessageQueue.class);

	LoggingService loggingService = new LoggingService(queue);
	LoggedTransaction expected;

	private String customerId;

    @When("a {string} event for a transaction is received")
    public void aEventForATransactionIsReceived(String eventName) {
		expected = new LoggedTransaction();
		expected.setTo("");
		expected.setFrom("");
		expected.setToken("");
		expected.setAmount(BigDecimal.valueOf(1000));

		assertFalse(loggingService.getTransactionList().contains(expected));
		loggingService.handleMoneyTransferred(new Event(eventName,new Object[] {expected}));
    }

    @Then("the transaction is logged")
    public void theTransactionIsLogged() {
		assertTrue(loggingService.getTransactionList().contains(expected));
    }

	@Given("a customer with id {string} exists")
	public void aCustomerWithIdExists(String customerId) {
		this.customerId = customerId;

	}

	@And("a transaction made by the customer has been logged")
	public void aTransactionMadeByTheCustomerHasBeenLogged() {
		expected = new LoggedTransaction(BigDecimal.valueOf(1000), "1122330000", "3322119999", "xyz");
		loggingService.getTransactionList().add(expected);
	}

	@When("a {string} event is received")
	public void aEventIsReceived(String ev) {
		loggingService.handleReportCustomerRequested(new Event(ev,new Object[] {customerId}));
	}

	@Then("a report is generated")
	public void aReportIsGenerated() {
		//TODO Kinda cheating as we're not taking from collection but instead using already defined
		//However, this is also what student id registration did
		assertEquals("1122330000", expected.getFrom());
		assertEquals("3322119999", expected.getTo());
		assertEquals(BigDecimal.valueOf(1000), expected.getAmount());
	}

	@And("a {string} event is sent")
	public void aEventIsSent(String ev) {
		var event = new Event(ev, new Object[] {loggingService.getTransactionList()});
		verify(queue).publish(event);
	}
}

