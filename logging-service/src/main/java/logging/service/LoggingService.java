package logging.service;

import messaging.Event;
import messaging.MessageQueue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LoggingService {
    MessageQueue queue;
    List<LoggedTransaction> loggedTransactionList = new ArrayList<>(); //TODO Use synchronized list instead of arraylist? https://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#synchronizedList(java.util.List)

    private CompletableFuture<LoggedTransaction> registeredTransaction; //TODO necessary?

    public LoggingService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("MoneyTransferred", this::handleMoneyTransferred);
        this.queue.addHandler("ReportCustomerRequested", this::handleReportCustomerRequested);
        this.queue.addHandler("ReportMerchantRequested", this::handleReportMerchantRequested);
        this.queue.addHandler("ReportManagerRequested", this::handleReportManagerRequested);
    }

    public void handleMoneyTransferred(Event ev) {
//        registeredTransaction = new CompletableFuture<>(); //TODO Is having a completeablefuture required when we're not doing anything else in the method?
        var t = ev.getArgument(0, LoggedTransaction.class);
        loggedTransactionList.add(t);
//        registeredTransaction.complete(t);
    }

    public List<LoggedTransaction> handleReportCustomerRequested(Event ev) {
        var customerId = ev.getArgument(0, String.class);

        List<LoggedTransaction> transactionsForCustomer = new ArrayList<>();
        for (LoggedTransaction t: loggedTransactionList) {
            if (t.from.equals(customerId)) {
                transactionsForCustomer.add(new LoggedTransaction(t.amount, t.from, t.to, t.token));
            }
        }

        Event event = new Event("ReportGenerated", new Object[] { transactionsForCustomer });
        queue.publish(event);
        return transactionsForCustomer;
    }

    public List<LoggedTransaction> handleReportMerchantRequested(Event ev) {
        var merchantId = ev.getArgument(0, String.class);

        List<LoggedTransaction> transactionsForMerchant = new ArrayList<>();
        for (LoggedTransaction t: loggedTransactionList) {
            if (t.to.equals(merchantId)) {
                //Merchant must not see customer ("from")
                transactionsForMerchant.add(new LoggedTransaction(t.amount, "", t.to, t.token));
            }
        }

        Event event = new Event("ReportGenerated", new Object[] { transactionsForMerchant });
        queue.publish(event);
        return transactionsForMerchant;
    }

    public List<LoggedTransaction> handleReportManagerRequested(Event ev) {
        Event event = new Event("ReportGenerated", new Object[] { loggedTransactionList });
        queue.publish(event);
        return loggedTransactionList;
    }

    public List<LoggedTransaction> getTransactionList() {
        return loggedTransactionList;
    }
}
