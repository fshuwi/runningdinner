package org.runningdinner.events;

import java.util.List;

import org.runningdinner.core.Team;
import org.runningdinner.service.email.TeamArrangementMessageFormatter;
import org.springframework.context.ApplicationEvent;

public class SendTeamArrangementsEvent extends ApplicationEvent {

	private static final long serialVersionUID = -3297813701725791593L;

	protected List<Team> regularTeams;
	protected TeamArrangementMessageFormatter teamArrangementsMessageFormatter;

	public SendTeamArrangementsEvent(final Object source, List<Team> regularTeams,
			TeamArrangementMessageFormatter teamArrangementsMessageFormatter) {
		super(source);
		this.regularTeams = regularTeams;
		this.teamArrangementsMessageFormatter = teamArrangementsMessageFormatter;
	}

	public List<Team> getRegularTeams() {
		return regularTeams;
	}

	public void setRegularTeams(List<Team> regularTeams) {
		this.regularTeams = regularTeams;
	}

	public TeamArrangementMessageFormatter getTeamArrangementMessageFormatter() {
		return teamArrangementsMessageFormatter;
	}

	public void setTeamArrangementMessageFormatter(TeamArrangementMessageFormatter teamArrangementsMessageFormatter) {
		this.teamArrangementsMessageFormatter = teamArrangementsMessageFormatter;
	}

}
