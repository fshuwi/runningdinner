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
import org.runningdinner.events.SendDinnerRoutesEvent;
import org.runningdinner.events.SendTeamArrangementsEvent;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.impl.AdminUrlGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;

/**
 * Central class for triggering the sending of email mail messages.<br>
 * Each class (producer) that wants to send one or more emails must put its mail messages (wrapped by ApplicationEvents) to this queue.<br>
 * This class provides the means for asynchronously taking mail messages from the queue and for sending it to the recipients
 * 
 * @author Clemens Stich
 * 
 */
public class MailQueue {

	private EmailService emailService;

	private AdminUrlGenerator adminUrlGenerator;

	private BlockingQueue<ApplicationEvent> mailMessagesQueue = new LinkedBlockingQueue<ApplicationEvent>();
	private ExecutorService execService;

	/**
	 * Used for disallowing any more events on the queue
	 */
	private volatile boolean allowMessagesOnQueue = false;

	private static final Logger LOGGER = LoggerFactory.getLogger(MailQueue.class);

	/**
	 * Put a new application event to the queue. This method may block until there is enough space on the queue.
	 * 
	 * @param event
	 * @return false if the event could not be put to the queue
	 * @throws InterruptedException
	 */
	public boolean putToQueue(ApplicationEvent event) throws InterruptedException {
		if (!allowMessagesOnQueue) {
			return false;
		}
		mailMessagesQueue.put(event);
		return true;
	}

	/**
	 * Called on container start up.<br>
	 * Initializes an internal thread pool with threads that act as consumers.
	 */
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

	/**
	 * Called on container shut down.<br>
	 * This method closes the internal thread pool and rejects any other message to be put into the queue. It is tried to process all
	 * reamaining messages on the queue, but there is (Currently) no gurantee or persistence mechanism
	 */
	@PreDestroy
	public void stop() {
		LOGGER.info("Received container stop event, shutting down ExecutorService...");

		this.allowMessagesOnQueue = false;

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

	/**
	 * Worker thread that takes and process ApplicationEVents from the queue
	 * 
	 * @author Clemens Stich
	 * 
	 */
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
					Thread.currentThread().interrupt(); // Save interrupt status
					return;
				}

				LOGGER.info("Received event from queue {}", event);

				if (event instanceof NewRunningDinnerEvent) {
					sendNewRunningDinnerEmail((NewRunningDinnerEvent)event);
				}

				if (event instanceof SendTeamArrangementsEvent) {
					sendTeamArrangementMails((SendTeamArrangementsEvent)event);
				}

				if (event instanceof SendDinnerRoutesEvent) {
					sendDinnerRouteMails((SendDinnerRoutesEvent)event);
				}
			}

			LOGGER.info("Task {} was explicitly stopped from within thread {}", FetchAndSendEmailFromQueueTask.class.getName(),
					Thread.currentThread().getName());
		}

		private void sendNewRunningDinnerEmail(NewRunningDinnerEvent event) {
			RunningDinner newRunningDinner = event.getNewRunningDinner();
			String administrationUrl = adminUrlGenerator.constructAdministrationUrl(newRunningDinner.getUuid(), null);
			emailService.sendRunningDinnerCreatedMessage(newRunningDinner.getEmail(), administrationUrl);
		}

		private void sendTeamArrangementMails(SendTeamArrangementsEvent event) {
			List<Team> teams = event.getRegularTeams();
			TeamArrangementMessageFormatter teamArrangementMessageFormatter = event.getTeamArrangementMessageFormatter();
			emailService.sendTeamArrangementMessages(teams, teamArrangementMessageFormatter);
		}

		private void sendDinnerRouteMails(SendDinnerRoutesEvent event) {
			List<Team> teams = event.getTeams();
			DinnerRouteMessageFormatter dinnerRouteMessageFormatter = event.getDinnerRouteMessageFormatter();
			emailService.sendDinnerRouteMessages(teams, dinnerRouteMessageFormatter);
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

}
