package org.runningdinner.service.email;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.event.publisher.EventPublisher;
import org.runningdinner.events.NewRunningDinnerEvent;
import org.runningdinner.events.SendDinnerRoutesEvent;
import org.runningdinner.events.SendParticipantsEvent;
import org.runningdinner.events.SendTeamArrangementsEvent;
import org.runningdinner.events.TeamHostChangedByParticipantEvent;
import org.runningdinner.model.ChangeTeamHost;
import org.runningdinner.model.DinnerRouteMailReport;
import org.runningdinner.model.ParticipantMailReport;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.TeamMailReport;
import org.runningdinner.service.impl.UrlGenerator;
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

	private UrlGenerator urlGenerator;

	private BlockingQueue<ApplicationEvent> mailMessagesQueue = new LinkedBlockingQueue<ApplicationEvent>();
	private ExecutorService execService;

	private EventPublisher eventPublisher;

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

		// TODO: Currently we don't handle the MailStatusInfo object's interrupted states; it would be reasonable to retrieve all not
		// completed tasks and try to store them in database as interrupted

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
	 * Worker thread that takes and process ApplicationEvents from the queue
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

				LOGGER.info("Received event {}", event);

				try {
					if (event instanceof NewRunningDinnerEvent) {
						sendNewRunningDinnerEmail((NewRunningDinnerEvent)event);
					}

					if (event instanceof SendTeamArrangementsEvent) {
						sendTeamArrangementMails((SendTeamArrangementsEvent)event);
					}

					if (event instanceof SendDinnerRoutesEvent) {
						sendDinnerRouteMails((SendDinnerRoutesEvent)event);
					}

					if (event instanceof SendParticipantsEvent) {
						sendParticipantMails((SendParticipantsEvent)event);
					}

					if (event instanceof TeamHostChangedByParticipantEvent) {
						sendTeamHostChangedMail((TeamHostChangedByParticipantEvent)event);
					}
				}
				catch (Exception ex) {
					LOGGER.error("Fatal error while processing event {}", event, ex);
				}
			}

			LOGGER.info("Task {} was explicitly stopped from within thread {}", FetchAndSendEmailFromQueueTask.class.getName(),
					Thread.currentThread().getName());
		}

		private void sendNewRunningDinnerEmail(NewRunningDinnerEvent event) {
			RunningDinner newRunningDinner = event.getNewRunningDinner();
			String administrationUrl = urlGenerator.constructAdministrationUrl(newRunningDinner.getUuid(), null);
			emailService.sendRunningDinnerCreatedMessage(newRunningDinner.getEmail(), administrationUrl);
		}

		private void sendTeamArrangementMails(SendTeamArrangementsEvent event) {
			List<Team> teams = event.getRegularTeams();
			TeamArrangementMessageFormatter teamArrangementMessageFormatter = event.getTeamArrangementsMessageFormatter();
			TeamMailReport teamMailReport = event.getTeamMailReport();

			Map<String, Boolean> sendingResults = emailService.sendTeamArrangementMessages(teams, teamArrangementMessageFormatter, event.getCustomMailServerSettings());

			eventPublisher.notifySendTeamMailsFinished(teamMailReport, sendingResults);
		}

		private void sendDinnerRouteMails(SendDinnerRoutesEvent event) {
			List<Team> teams = event.getTeams();
			DinnerRouteMessageFormatter dinnerRouteMessageFormatter = event.getDinnerRouteMessageFormatter();
			DinnerRouteMailReport dinnerRouteMailReport = event.getDinnerRouteMailReport();

			Map<String, Boolean> sendingResults = emailService.sendDinnerRouteMessages(teams, dinnerRouteMessageFormatter, event.getCustomMailServerSettings());

			eventPublisher.notifySendDinnerRouteMailsFinished(dinnerRouteMailReport, sendingResults);
		}

		private void sendParticipantMails(SendParticipantsEvent event) {
			List<Participant> participants = event.getParticipants();
			ParticipantMessageFormatter participantMessageFormatter = event.getParticipantMessageFormatter();
			ParticipantMailReport participantMailReport = event.getParticipantMailReport();

			Map<String, Boolean> sendingResults = emailService.sendMessageToParticipants(participants, participantMessageFormatter, event.getCustomMailServerSettings());

			eventPublisher.notifySendParticipantMailsFinished(participantMailReport, sendingResults);
		}
		
		private void sendTeamHostChangedMail(TeamHostChangedByParticipantEvent event) {
			ChangeTeamHost changeTeamHost = event.getChangeTeamHost();
			Team team = event.getTeam();
			emailService.sendTeamHostChangedMail(team, changeTeamHost);
		}
	}

	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Autowired
	public void setUrlGenerator(UrlGenerator adminUrlGenerator) {
		this.urlGenerator = adminUrlGenerator;
	}

	@Autowired
	public void setEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

}
