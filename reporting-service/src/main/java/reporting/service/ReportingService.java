package reporting.service;

import messaging.Event;
import messaging.MessageQueue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReportingService {
    MessageQueue queue;
    List<Transaction> transactionList = new LinkedList<>(); //TODO Use synchronized list instead of arraylist? https://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#synchronizedList(java.util.List)

    private CompletableFuture<Transaction> registeredTransaction; //TODO necessary?

    public ReportingService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("MoneyTransferred", this::handleMoneyTransferred);
    }

    public void handleMoneyTransferred(Event ev) {
//        registeredTransaction = new CompletableFuture<>(); //TODO Is having a completeablefuture required when we're not doing anything else in the method?
        var t = ev.getArgument(0, Transaction.class);
        transactionList.add(t);
//        registeredTransaction.complete(t);
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }
}
