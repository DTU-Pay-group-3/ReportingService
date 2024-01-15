package behaviourtests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import reporting.service.ReportingService;
import reporting.service.Transaction;

import java.math.BigDecimal;

public class ReportingServiceSteps {
	MessageQueue queue = mock(MessageQueue.class);

	ReportingService reportingService = new ReportingService(queue);
	Transaction expected;
    @When("a {string} event for a transaction is received")
    public void aEventForATransactionIsReceived(String eventName) {
		expected = new Transaction();
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
}

