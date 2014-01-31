package org.runningdinner.event.publisher;

import java.util.List;

import org.runningdinner.core.Team;
import org.runningdinner.events.NewRunningDinnerEvent;
import org.runningdinner.events.SendDinnerRoutesEvent;
import org.runningdinner.events.SendTeamArrangementsEvent;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.ui.dto.FinalizeTeamsModel;
import org.runningdinner.ui.dto.SendDinnerRoutesModel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher implements ApplicationEventPublisherAware {

	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	public void notifyNewRunningDinner(final RunningDinner runningDinner) {
		applicationEventPublisher.publishEvent(new NewRunningDinnerEvent(this, runningDinner));
	}

	public void publishTeamMessages(final List<Team> regularTeams, FinalizeTeamsModel finalizeTeamsModel) {
		applicationEventPublisher.publishEvent(new SendTeamArrangementsEvent(this, regularTeams, finalizeTeamsModel));
	}

	public void publishDinnerRouteMessages(List<Team> teams, SendDinnerRoutesModel sendDinnerRoutesModel) {
		applicationEventPublisher.publishEvent(new SendDinnerRoutesEvent(this, teams, sendDinnerRoutesModel));
	}
}
