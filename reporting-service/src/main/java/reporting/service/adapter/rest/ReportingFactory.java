package reporting.service.adapter.rest;

import messaging.implementations.RabbitMqQueue;
import reporting.service.ReportingService;

public class ReportingFactory {
	static ReportingService service = null;

	public synchronized ReportingService getService() {
		// The singleton pattern.
		// Ensure that there is at most
		// one instance of a PaymentService
		if (service != null) {
			return service;
		}
		
		// Hookup the classes to send and receive
		// messages via RabbitMq, i.e. RabbitMqSender and
		// RabbitMqListener. 
		// This should be done in the factory to avoid 
		// the PaymentService knowing about them. This
		// is called dependency injection.
		// At the end, we can use the PaymentService in tests
		// without sending actual messages to RabbitMq.
		var mq = new RabbitMqQueue("rabbitMq");
		service = new ReportingService(mq);
//		new StudentRegistrationServiceAdapter(service, mq);
		return service;
	}
}
