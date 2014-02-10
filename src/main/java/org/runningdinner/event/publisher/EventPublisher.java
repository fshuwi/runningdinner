package org.runningdinner.event.publisher;

import java.util.List;

import org.runningdinner.core.Team;
import org.runningdinner.events.NewRunningDinnerEvent;
import org.runningdinner.events.SendDinnerRoutesEvent;
import org.runningdinner.events.SendTeamArrangementsEvent;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.email.DinnerRouteMessageFormatter;
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
	public void publishTeamMessages(final List<Team> regularTeams, TeamArrangementMessageFormatter teamArrangementsMessageFormatter) {
		applicationEventPublisher.publishEvent(new SendTeamArrangementsEvent(this, regularTeams, teamArrangementsMessageFormatter));
	}

	/**
	 * Publish the event for sending dinner route messages
	 * 
	 * @param teams
	 * @param dinnerRouteMessageFormatter
	 */
	public void publishDinnerRouteMessages(List<Team> teams, DinnerRouteMessageFormatter dinnerRouteMessageFormatter) {
		applicationEventPublisher.publishEvent(new SendDinnerRoutesEvent(this, teams, dinnerRouteMessageFormatter));
	}
}
