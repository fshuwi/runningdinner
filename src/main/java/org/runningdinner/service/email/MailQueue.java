package org.runningdinner.service.email;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;

public class MailQueue {

	private EmailService emailService;

	private BlockingQueue<ApplicationEvent> mailMessagesQueue = new LinkedBlockingQueue<ApplicationEvent>();
	private ExecutorService execService;
	private FetchEmailFromQueueTask fetchEmailFromQueueTask;

	public void putToQueue(ApplicationEvent event) throws InterruptedException {
		mailMessagesQueue.put(event);
	}

	@PostConstruct
	public void startConsumerThread() {
		System.out.println("START OF CONTAINER");

		execService = Executors.newFixedThreadPool(3);

		fetchEmailFromQueueTask = new FetchEmailFromQueueTask();
		execService.submit(fetchEmailFromQueueTask);
	}

	@PreDestroy
	public void stop() {
		System.out.println("STOP OF CONTAINER");

		try {
			putToQueue(new PoisonPillEvent(this));
		}
		catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		execService.shutdown();

		try {
			execService.awaitTermination(2, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	class FetchEmailFromQueueTask implements Runnable {

		@Override
		public void run() {
			ApplicationEvent event = null;
			try {
				event = mailMessagesQueue.take();
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Thread.currentThread().interrupt(); // Propagate interrupt
				return;
			}

			if (event instanceof PoisonPillEvent) {
				return;
			}

			execService.submit(new SendEmailTask(event));
		}
	}

	class SendEmailTask implements Runnable {

		public SendEmailTask(ApplicationEvent appEvent) {

		}

		@Override
		public void run() {
			emailService.sendRunningDinnerCreatedMessage(null, null);
		}

	}

	static class PoisonPillEvent extends ApplicationEvent {

		private static final long serialVersionUID = -6621060281553947462L;

		public PoisonPillEvent(Object source) {
			super(source);
		}

	}

}
