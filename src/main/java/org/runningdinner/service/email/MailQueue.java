package org.runningdinner.service.email;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.runningdinner.core.Team;
import org.runningdinner.events.NewRunningDinnerEvent;
import org.runningdinner.events.SendTeamArrangementsEvent;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.impl.AdminUrlGenerator;
import org.runningdinner.ui.dto.FinalizeTeamsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;

public class MailQueue {

	private EmailService emailService;

	private AdminUrlGenerator adminUrlGenerator;

	private BlockingQueue<ApplicationEvent> mailMessagesQueue = new LinkedBlockingQueue<ApplicationEvent>();
	private ExecutorService execService;
	private volatile boolean allowMessagesOnQueue = false;

	private static final Logger LOGGER = LoggerFactory.getLogger(MailQueue.class);

	public boolean putToQueue(ApplicationEvent event) throws InterruptedException {
		if (!allowMessagesOnQueue) {
			return false;
		}
		mailMessagesQueue.put(event);
		return true;
	}

	@PostConstruct
	public void startConsumerThread() {
		final int numConsumerThreads = 3;

		LOGGER.info("Received container start event, creating executor service with threadpool for {} threads", numConsumerThreads);

		execService = Executors.newFixedThreadPool(numConsumerThreads);
		for (int i = 0; i < numConsumerThreads; i++) {
			execService.submit(new FetchAndSendEmailFromQueueTask());
		}

		this.allowMessagesOnQueue = true;
	}

	@PreDestroy
	public void stop() {
		LOGGER.info("Received container stop event, shutting down ExecutorService...");

		this.allowMessagesOnQueue = false;

		// try {
		// putToQueue(new PoisonPillEvent(this));
		// }
		// catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		execService.shutdown();

		try {
			LOGGER.info("Await Executor service termination...");
			execService.awaitTermination(2, TimeUnit.SECONDS);
			LOGGER.info("Execturservice successfully shutdown");
		}
		catch (InterruptedException e) {
			LOGGER.error("Execturservice shutdown was interrupted", e);
			Thread.currentThread().interrupt(); // Save interrupt state to current thread; nothing more to do for us now
		}

	}

	class FetchAndSendEmailFromQueueTask implements Runnable {

		private final Logger LOGGER = LoggerFactory.getLogger(FetchAndSendEmailFromQueueTask.class);

		@Override
		public void run() {

			LOGGER.info("Starting task {} within thread {}", FetchAndSendEmailFromQueueTask.class.getName(),
					Thread.currentThread().getName());

			while (allowMessagesOnQueue) {

				ApplicationEvent event = null;
				try {
					event = mailMessagesQueue.take();
				}
				catch (InterruptedException e) {
					// ExecutorService is probably shutdown:
					LOGGER.info("Executor service is shutdown by application, quit task {} within thred {}",
							FetchAndSendEmailFromQueueTask.class.getName(), Thread.currentThread().getName());
					Thread.currentThread().interrupt(); // Propagate interrupt
					return;
				}

				LOGGER.info("Received event from queue {}", event);

				if (event instanceof NewRunningDinnerEvent) {
					sendNewRunningDinnerEmail((NewRunningDinnerEvent)event);
				}

				if (event instanceof SendTeamArrangementsEvent) {
					sendTeamArrangementMail((SendTeamArrangementsEvent)event);
				}
			}

			LOGGER.info("Task {} was explicitly stopped from within thread {}", FetchAndSendEmailFromQueueTask.class.getName(),
					Thread.currentThread().getName());
		}

		private boolean sendNewRunningDinnerEmail(NewRunningDinnerEvent event) {
			RunningDinner newRunningDinner = event.getNewRunningDinner();
			String administrationUrl = adminUrlGenerator.constructAdministrationUrl(newRunningDinner.getUuid(), null);

			try {
				emailService.sendRunningDinnerCreatedMessage(newRunningDinner.getEmail(), administrationUrl);
				LOGGER.info("Sent new running dinner mail for dinner {} to recipient {} successfully", newRunningDinner.getUuid(),
						newRunningDinner.getEmail());
				return true;
			}
			catch (Exception ex) {
				LOGGER.error("Could not send new running dinner email for dinner {} to recipient {}", newRunningDinner.getUuid(),
						newRunningDinner.getEmail(), ex);
				return false;
			}
		}

		private void sendTeamArrangementMail(SendTeamArrangementsEvent event) {
			List<Team> teams = event.getRegularTeams();
			FinalizeTeamsModel finalizeTeamsModel = event.getFinalizeTeamsModel();
			emailService.sendTeamArrangementMessages(teams, finalizeTeamsModel);
		}

	}

	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Autowired
	public void setAdminUrlGenerator(AdminUrlGenerator adminUrlGenerator) {
		this.adminUrlGenerator = adminUrlGenerator;
	}

	// static class PoisonPillEvent extends ApplicationEvent {
	//
	// private static final long serialVersionUID = -6621060281553947462L;
	//
	// public PoisonPillEvent(Object source) {
	// super(source);
	// }
	//
	// }

}
