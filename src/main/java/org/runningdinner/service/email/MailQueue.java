package org.runningdinner.service.email;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;

public class MailQueue {
	private BlockingQueue<?> mailMessagesQueue;
	private ExecutorService execService;

	private EmailService emailService;

	// public void putMessage(Object message) {
	// try {
	// mailMessagesQueue.put(null);
	// }
	// catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	@PostConstruct
	public void startConsumerThread() {
		System.out.println("START OF CONTAINER");
		// execService.submit(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// Thread.currentThread().sleep(10);
		// Object mailMessage = mailMessagesQueue.take();
		// // Maybe start off with other threads!
		// // emailService.sendRunningDinnerCreatedMessage(recipientEmail, administrationUrl);
		// }
		// catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// });
	}

	@PreDestroy
	public void stop() {
		System.out.println("STOP OF CONTAINER");
	}

	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

}
