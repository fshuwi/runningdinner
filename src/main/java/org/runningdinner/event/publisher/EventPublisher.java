package org.runningdinner.event.publisher;

import java.util.List;
import java.util.Map;

import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.events.NewRunningDinnerEvent;
import org.runningdinner.events.SendDinnerRouteMailsFinishedEvent;
import org.runningdinner.events.SendDinnerRoutesEvent;
import org.runningdinner.events.SendParticipantMailsFinishedEvent;
import org.runningdinner.events.SendParticipantsEvent;
import org.runningdinner.events.SendTeamArrangementsEvent;
import org.runningdinner.events.SendTeamMailsFinishedEvent;
import org.runningdinner.events.TeamHostChangedByParticipantEvent;
import org.runningdinner.model.ChangeTeamHost;
import org.runningdinner.model.DinnerRouteMailReport;
import org.runningdinner.model.ParticipantMailReport;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.TeamMailReport;
import org.runningdinner.service.email.DinnerRouteMessageFormatter;
import org.runningdinner.service.email.MailServerSettings;
import org.runningdinner.service.email.ParticipantMessageFormatter;
import org.runningdinner.service.email.TeamArrangementMessageFormatter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * Simple Spring component which is used for publishing ApplicationEvents in a synchronous way.
 * 
 * @author Clemens Stich
 * 
 */
@Component
public class EventPublisher implements ApplicationEventPublisherAware {

	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	/**
	 * Publish the event about a new created running dinner
	 * 
	 * @param runningDinner
	 */
	public void notifyNewRunningDinner(final RunningDinner runningDinner) {
		applicationEventPublisher.publishEvent(new NewRunningDinnerEvent(this, runningDinner));
	}

	/**
	 * Publish the event for sending team arrangement messages
	 * 
	 * @param regularTeams
	 * @param teamArrangementsMessageFormatter
	 */
	public void publishTeamMessages(RunningDinner dinner, final List<Team> regularTeams,
			final TeamArrangementMessageFormatter teamArrangementsMessageFormatter, final TeamMailReport teamMailReport,
			final MailServerSettings customMailServerSettings) {
		applicationEventPublisher.publishEvent(new SendTeamArrangementsEvent(this, dinner, regularTeams, teamArrangementsMessageFormatter,
				teamMailReport, customMailServerSettings));
	}

	/**
	 * Publish the event for sending dinner route messages
	 * 
	 * @param teams
	 * @param dinnerRouteMessageFormatter
	 */
	public void publishDinnerRouteMessages(RunningDinner dinner, List<Team> teams, DinnerRouteMessageFormatter dinnerRouteMessageFormatter,
			DinnerRouteMailReport dinnerRouteMailReport, final MailServerSettings customMailServerSettings) {
		applicationEventPublisher.publishEvent(new SendDinnerRoutesEvent(this, dinner, teams, dinnerRouteMessageFormatter,
				dinnerRouteMailReport, customMailServerSettings));
	}

	/**
	 * Publish the event for sending messages to participants
	 * 
	 * @param participants
	 * @param participantMessageFormatter
	 * @param participantMailReport
	 */
	public void publishParticipantMessages(RunningDinner dinner, List<Participant> participants,
			ParticipantMessageFormatter participantMessageFormatter, ParticipantMailReport participantMailReport,
			final MailServerSettings customMailServerSettings) {
		applicationEventPublisher.publishEvent(new SendParticipantsEvent(this, dinner, participants, participantMessageFormatter,
				participantMailReport, customMailServerSettings));
	}

	public void notifySendTeamMailsFinished(TeamMailReport teamMailReport, Map<String, Boolean> sendingResults) {
		applicationEventPublisher.publishEvent(new SendTeamMailsFinishedEvent(this, teamMailReport, sendingResults));
	}

	public void notifySendDinnerRouteMailsFinished(DinnerRouteMailReport dinnerRouteMailReport, Map<String, Boolean> sendingResults) {
		applicationEventPublisher.publishEvent(new SendDinnerRouteMailsFinishedEvent(this, dinnerRouteMailReport, sendingResults));
	}

	public void notifySendParticipantMailsFinished(ParticipantMailReport participantMailReport, Map<String, Boolean> sendingResults) {
		applicationEventPublisher.publishEvent(new SendParticipantMailsFinishedEvent(this, participantMailReport, sendingResults));
	}

	public void notifyTeamHostChangeByParticipant(final Team team, final ChangeTeamHost changeTeamHost) {
		applicationEventPublisher.publishEvent(new TeamHostChangedByParticipantEvent(this, team, changeTeamHost));
	}
	
	
}
