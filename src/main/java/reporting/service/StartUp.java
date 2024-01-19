package reporting.service;

import messaging.implementations.RabbitMqQueue;

// @Author Andreas
public class StartUp {
	public static void main(String[] args) throws Exception {
		new StartUp().startUp();
	}

	private void startUp() throws Exception {
		System.out.println("startup");
		var mq = new RabbitMqQueue("rabbitMq");
		new ReportingService(mq);
	}
}
