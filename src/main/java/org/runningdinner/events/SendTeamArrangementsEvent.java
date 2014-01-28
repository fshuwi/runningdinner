package org.runningdinner.events;

import java.util.List;

import org.runningdinner.core.Team;
import org.runningdinner.ui.dto.FinalizeTeamsModel;
import org.springframework.context.ApplicationEvent;

public class SendTeamArrangementsEvent extends ApplicationEvent {

	private static final long serialVersionUID = -3297813701725791593L;

	List<Team> regularTeams;
	FinalizeTeamsModel finalizeTeamsModel;

	public SendTeamArrangementsEvent(final Object source, List<Team> regularTeams, FinalizeTeamsModel finalizeTeamsModel) {
		super(source);
		this.regularTeams = regularTeams;
		this.finalizeTeamsModel = finalizeTeamsModel;
	}

	public List<Team> getRegularTeams() {
		return regularTeams;
	}

	public void setRegularTeams(List<Team> regularTeams) {
		this.regularTeams = regularTeams;
	}

	public FinalizeTeamsModel getFinalizeTeamsModel() {
		return finalizeTeamsModel;
	}

	public void setFinalizeTeamsModel(FinalizeTeamsModel finalizeTeamsModel) {
		this.finalizeTeamsModel = finalizeTeamsModel;
	}

}
