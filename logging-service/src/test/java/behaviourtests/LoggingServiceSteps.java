package behaviourtests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
}

