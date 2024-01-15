package logging.service;

import messaging.Event;
import messaging.MessageQueue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LoggingService {
    MessageQueue queue;
    List<LoggedTransaction> loggedTransactionList = new LinkedList<>(); //TODO Use synchronized list instead of arraylist? https://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#synchronizedList(java.util.List)

    private CompletableFuture<LoggedTransaction> registeredTransaction; //TODO necessary?

    public LoggingService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("MoneyTransferred", this::handleMoneyTransferred);
    }

    public void handleMoneyTransferred(Event ev) {
//        registeredTransaction = new CompletableFuture<>(); //TODO Is having a completeablefuture required when we're not doing anything else in the method?
        var t = ev.getArgument(0, LoggedTransaction.class);
        loggedTransactionList.add(t);
//        registeredTransaction.complete(t);
    }

    public List<LoggedTransaction> getTransactionList() {
        return loggedTransactionList;
    }
}
