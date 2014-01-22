package org.runningdinner.ui.dto;

import java.util.ArrayList;

public class SingleTeamHostChange {

	private String teamKey;

	private String newHostParticipantKey;

	public SingleTeamHostChange() {
	}

	public SingleTeamHostChange(String teamKey, String newHostParticipantKey) {
		this.teamKey = teamKey;
		this.newHostParticipantKey = newHostParticipantKey;
	}

	public String getTeamKey() {
		return teamKey;
	}

	public void setTeamKey(String teamKey) {
		this.teamKey = teamKey;
	}

	public String getNewHostParticipantKey() {
		return newHostParticipantKey;
	}

	public void setNewHostParticipantKey(String newHostParticipantKey) {
		this.newHostParticipantKey = newHostParticipantKey;
	}

	public static class TeamHostChangeList extends ArrayList<SingleTeamHostChange> {
		private static final long serialVersionUID = -3946462970562661784L;
	}
}