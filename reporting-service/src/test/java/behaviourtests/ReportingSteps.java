package behaviourtests;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import studentregistration.service.LoggedTransaction;
import studentregistration.service.ReportingService;

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

        service.logTransaction(loggedTransaction);
    }

    @When("the customer requests a report")
    public void theCustomerRequestsAReport() {
        new Thread(() -> {
            var result = service.getReportsCustomer(customerId);
            report.complete(result);
        }).start();
    }

    @Then("the {string} event is sent")
    public void theEventIsSent(String event) {
        Event e = new Event(event, new Object[] { customerId });
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
}
