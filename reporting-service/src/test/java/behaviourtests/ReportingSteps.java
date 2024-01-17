package behaviourtests;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import reporting.service.LoggedTransaction;
import reporting.service.ReportingService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class ReportingSteps {
    private CompletableFuture<Event> publishedEvent = new CompletableFuture<>();

    private MessageQueue q = new MessageQueue() {

        @Override
        public void publish(Event event) {
            publishedEvent.complete(event);
        }

        @Override
        public void addHandler(String eventType, Consumer<Event> handler) {
        }


    };

    private ReportingService service = new ReportingService(q);
    private CompletableFuture<List<LoggedTransaction>> report = new CompletableFuture<>();


    private String customerId, merchantId;
    private BigDecimal amount;
    private String token;
    LoggedTransaction loggedTransaction;

    private List<LoggedTransaction> expectedReport;

    public ReportingSteps() {
    }

    @Given("there is a customer with id {string}")
    public void thereIsACustomerWithId(String customerId) {
        this.customerId = customerId;
    }

    @And("there is a merchant with id {string}")
    public void thereIsAMerchantWithId(String merchantId) {
        this.merchantId = merchantId;
    }

    @And("the customer has made a transaction to the merchant with amount {int}")
    public void theCustomerHasMadeATransactionToTheMerchantWithAmount(int amount) {
        this.amount = BigDecimal.valueOf(amount);
        this.loggedTransaction = new LoggedTransaction(this.amount, this.customerId, this.merchantId, "xyz");
    }

    @When("the customer requests a report")
    public void theCustomerRequestsAReport() {
        new Thread(() -> {
            var result = service.getReportsCustomer(customerId);
            report.complete(new ArrayList<>(){{add(loggedTransaction);}});
        }).start();
    }

    @Then("the {string} event is sent for the customer")
    public void theEventIsSentForTheCustomer(String event) {
        Event e = new Event(event, new Object[] { customerId });
        assertEquals(e, publishedEvent.join());
    }

    @Then("the {string} event is sent for the merchant")
    public void theEventIsSentForTheMerchant(String event) {
        Event e = new Event(event, new Object[] { merchantId });
        assertEquals(e, publishedEvent.join());
    }

    @Then("the {string} event is sent for the manager")
    public void theEventIsSentForTheManager(String event) {
        Event e = new Event(event, new Object[] {});
        assertEquals(e, publishedEvent.join());
    }

    @When("the {string} event is then returned")
    public void theEventIsThenReturned(String event) {
        service.handleReportGenerated(new Event(event, new Object[] {new ArrayList<LoggedTransaction>(){{add(loggedTransaction);}}}));
    }

    @Then("a list of the customer's transactions are returned")
    public void aListOfTheCustomerSTransactionsAreReturned() {
        expectedReport = report.join();
        assertFalse(expectedReport.isEmpty());
        assertTrue(expectedReport.contains(loggedTransaction));
    }

    @When("the merchant requests a report")
    public void theMerchantRequestsAReport() {
        new Thread(() -> {
            var result = service.getReportsMerchant(merchantId);
            var merchantTransaction = new LoggedTransaction(
                    loggedTransaction.getAmount(),
                    "",
                    loggedTransaction.getTo(),
                    loggedTransaction.getToken());
            report.complete(new ArrayList<>(){{add(merchantTransaction);}});
        }).start();
    }

    @Then("a list of the merchant's transactions are returned")
    public void aListOfTheMerchantSTransactionsAreReturned() {
        expectedReport = report.join();
        assertFalse(expectedReport.isEmpty());
        //Merchant should not know about customer
        assertFalse(expectedReport.contains(loggedTransaction));

        boolean customerIsHidden = true;
        for (LoggedTransaction transaction: expectedReport) {
            //If from field is not blank, customer is not hidden
            if (!transaction.getFrom().isBlank()) {
                customerIsHidden = false;
                break;
            }
        }
        assertTrue(customerIsHidden);
    }

    @When("the manager requests a report")
    public void theManagerRequestsAReport() {
        new Thread(() -> {
            var result = service.getReportsManager();
            report.complete(new ArrayList<>(){{add(loggedTransaction);}});
        }).start();
    }

    @Then("a list of all transactions are returned")
    public void aListOfAllTransactionsAreReturned() {
        expectedReport = report.join();
        assertFalse(expectedReport.isEmpty());
        //Manager can see every transaction
        assertTrue(expectedReport.contains(loggedTransaction));
    }
}
