package org.runningdinner.events;

import java.util.List;

import org.runningdinner.core.Team;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.TeamMailReport;
import org.runningdinner.service.email.MailServerSettings;
import org.runningdinner.service.email.TeamArrangementMessageFormatter;

/**
 * This event is published whenever a user wants to send team-arrangement messages
 * 
 * @author Clemens Stich
 * 
 */
public class SendTeamArrangementsEvent extends BaseAdminMailEvent {

	private static final long serialVersionUID = -3297813701725791593L;

	protected List<Team> regularTeams;
	protected TeamArrangementMessageFormatter teamArrangementsMessageFormatter;
	protected TeamMailReport teamMailReport;

	public SendTeamArrangementsEvent(final Object source, RunningDinner dinner, List<Team> regularTeams,
			TeamArrangementMessageFormatter teamArrangementsMessageFormatter, TeamMailReport teamMailReport,
			MailServerSettings customMailServerSettings) {
		super(source, dinner, customMailServerSettings);
		this.regularTeams = regularTeams;
		this.teamArrangementsMessageFormatter = teamArrangementsMessageFormatter;
		this.teamMailReport = teamMailReport;
	}

	public List<Team> getRegularTeams() {
		return regularTeams;
	}

	public void setRegularTeams(List<Team> regularTeams) {
		this.regularTeams = regularTeams;
	}

	public TeamArrangementMessageFormatter getTeamArrangementsMessageFormatter() {
		return teamArrangementsMessageFormatter;
	}

	public void setTeamArrangementsMessageFormatter(TeamArrangementMessageFormatter teamArrangementsMessageFormatter) {
		this.teamArrangementsMessageFormatter = teamArrangementsMessageFormatter;
	}

	public TeamMailReport getTeamMailReport() {
		return teamMailReport;
	}

	public void setTeamMailReport(TeamMailReport teamMailReport) {
		this.teamMailReport = teamMailReport;
	}
}
