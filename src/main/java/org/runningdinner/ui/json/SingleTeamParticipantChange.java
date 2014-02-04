package org.runningdinner.ui.json;

/**
 * Used as a JSON request for either changing switching team-members or for changing the host of a team.<br>
 * For Spring MVC's automatic JSON support we have to create separate concrete classes, therefore this class should not be used as
 * RequestBody or ResponseBody<br>
 * {@link TeamHostChangeList} and {@link SwitchTeamMembers}
 * 
 * @author i01002492
 * 
 */
public class SingleTeamParticipantChange {

	private String teamKey;

	private String participantKey;

	public SingleTeamParticipantChange() {
	}

	public SingleTeamParticipantChange(String teamKey, String newHostParticipantKey) {
		this.teamKey = teamKey;
		this.participantKey = newHostParticipantKey;
	}

	public String getTeamKey() {
		return teamKey;
	}

	public void setTeamKey(String teamKey) {
		this.teamKey = teamKey;
	}

	public String getParticipantKey() {
		return participantKey;
	}

	public void setParticipantKey(String newHostParticipantKey) {
		this.participantKey = newHostParticipantKey;
	}

}