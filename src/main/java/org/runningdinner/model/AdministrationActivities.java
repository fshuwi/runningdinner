package org.runningdinner.model;

import javax.persistence.Embeddable;

@Embeddable
public class AdministrationActivities {

	private boolean participantsUploaded = true;

	private boolean teamArrangementsFinalized;

	private boolean teamArrangementsMailsSent;

	private boolean dinnerRoutesMailsSent;

	public boolean isParticipantsUploaded() {
		return participantsUploaded;
	}

	public void setParticipantsUploaded(boolean participantsUploaded) {
		this.participantsUploaded = participantsUploaded;
	}

	public boolean isTeamArrangementsFinalized() {
		return teamArrangementsFinalized;
	}

	public void setTeamArrangementsFinalized(boolean teamArrangementsFinalized) {
		this.teamArrangementsFinalized = teamArrangementsFinalized;
	}

	public boolean isTeamArrangementsMailsSent() {
		return teamArrangementsMailsSent;
	}

	public void setTeamArrangementsMailsSent(boolean teamArrangementsMailsSent) {
		this.teamArrangementsMailsSent = teamArrangementsMailsSent;
	}

	public boolean isDinnerRoutesMailsSent() {
		return dinnerRoutesMailsSent;
	}

	public void setDinnerRoutesMailsSent(boolean dinnerRoutesMailsSent) {
		this.dinnerRoutesMailsSent = dinnerRoutesMailsSent;
	}

}
