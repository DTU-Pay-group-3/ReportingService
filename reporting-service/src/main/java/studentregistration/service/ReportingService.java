package studentregistration.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import messaging.Event;
import messaging.MessageQueue;

import javax.ws.rs.core.GenericType;

public class ReportingService {

	private MessageQueue queue;
	private CompletableFuture<List<LoggedTransaction>> report;
	private CompletableFuture<LoggedTransaction> transactionLogged;

	public ReportingService(MessageQueue q) {
		queue = q;
		queue.addHandler("TransactionLogged", this::handleTransactionLogged);
		queue.addHandler("ReportGenerated", this::handleReportGenerated);
	}

	public List<LoggedTransaction> getReportsCustomer(String customerId) {
		report = new CompletableFuture<>();
		Event event = new Event("ReportCustomerRequested", new Object[] {customerId});
		queue.publish(event);
		return report.join();
	}

	public List<LoggedTransaction> getReportsMerchant(String merchantId) {
		report = new CompletableFuture<>();
		Event event = new Event("ReportMerchantRequested", new Object[] {merchantId});
		queue.publish(event);
		return report.join();
	}

	public List<LoggedTransaction> getReportsManager() {
		report = new CompletableFuture<>();
		Event event = new Event("ReportManagerRequested", new Object[] {});
		queue.publish(event);
		return report.join();
	}

	public void handleReportGenerated(Event e) {
		//TODO Not sure if this works
		var r = e.getArgument(0, new ArrayList<LoggedTransaction>().getClass());
		report.complete(r);
	}


	//TODO ONLY USE FOR TESTING - REPORTING SERVICE SHOULD NOT LOG TRANSACTIONS
	public LoggedTransaction logTransaction(LoggedTransaction transaction) {
		transactionLogged = new CompletableFuture<>();
		Event event = new Event("LogTransactionRequested", new Object[] {transaction});
		queue.publish(event);
		return transactionLogged.join();
	}
	//TODO ONLY USE FOR TESTING - REPORTING SERVICE SHOULD NOT LOG TRANSACTIONS
	public void handleTransactionLogged(Event e) {
		var t = e.getArgument(0, LoggedTransaction.class);
		transactionLogged.complete(t);
	}
}
