package reporting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import messaging.Event;
import messaging.MessageQueue;

public class ReportingService {

	private MessageQueue queue;
	private CompletableFuture<List<LoggedTransaction>> report;
	private CompletableFuture<LoggedTransaction> transactionLogged;

	public ReportingService(MessageQueue q) {
		queue = q;
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
}
