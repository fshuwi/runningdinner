package org.runningdinner.ui.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/**
	 * Needed for Spring's automatic JSON conversion support
	 * 
	 */
	public static class TeamHostChangeList extends ArrayList<SingleTeamParticipantChange> {
		private static final long serialVersionUID = -3946462970562661784L;

		public static Map<String, String> generateTeamHostsMap(final List<SingleTeamParticipantChange> singleTeamHostChanges) {
			Map<String, String> teamHostMapping = new HashMap<String, String>();
			for (SingleTeamParticipantChange teamHostChange : singleTeamHostChanges) {
				String teamKey = teamHostChange.getTeamKey();
				teamHostMapping.put(teamKey, teamHostChange.getParticipantKey());
			}
			return teamHostMapping;
		}
	}

	public static class SwitchTeamMembers extends ArrayList<SingleTeamParticipantChange> {
		private static final long serialVersionUID = -3811055478238741644L;

	}
}