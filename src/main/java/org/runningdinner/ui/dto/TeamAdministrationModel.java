package org.runningdinner.ui.dto;

import org.runningdinner.model.AdministrationActivities;

public class TeamAdministrationModel {

	protected boolean teamsAlreadySaved;
	protected boolean mailsAlreadySent;
	protected boolean hasTeams;

	protected TeamAdministrationModel() {

	}

	public boolean isTeamsAlreadySaved() {
		return teamsAlreadySaved;
	}

	public void setTeamsAlreadySaved(boolean teamsAlreadySaved) {
		this.teamsAlreadySaved = teamsAlreadySaved;
	}

	public boolean isMailsAlreadySent() {
		return mailsAlreadySent;
	}

	public void setMailsAlreadySent(boolean mailsAlreadySent) {
		this.mailsAlreadySent = mailsAlreadySent;
	}

	public boolean isHasTeams() {
		return hasTeams;
	}

	public void setHasTeams(boolean hasTeams) {
		this.hasTeams = hasTeams;
	}

	public static TeamAdministrationModel fromFirstTeamGeneration(boolean hasGeneratedTeams) {
		TeamAdministrationModel result = new TeamAdministrationModel();
		result.teamsAlreadySaved = false;
		result.mailsAlreadySent = false;
		result.hasTeams = hasGeneratedTeams;
		return result;
	}

	public static TeamAdministrationModel fromActivities(AdministrationActivities activities, boolean hasPersistedTeams) {
		TeamAdministrationModel result = new TeamAdministrationModel();

		result.teamsAlreadySaved = false;
		result.mailsAlreadySent = false;
		result.hasTeams = true;

		if (hasPersistedTeams) {
			if (activities.isTeamArrangementsFinalized()) {
				result.setTeamsAlreadySaved(true);
				if (activities.isTeamArrangementsMailsSent()) {
					result.setMailsAlreadySent(true);
				}
			}
		}
		else {
			result.setHasTeams(false);
		}

		return result;
	}

	@Override
	public String toString() {
		return "TeamAdministrationModel [teamsAlreadySaved=" + teamsAlreadySaved + ", mailsAlreadySent=" + mailsAlreadySent + ", hasTeams="
				+ hasTeams + "]";
	}

}
