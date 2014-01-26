package org.runningdinner.ui.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/**
	 * Needed for Spring's automatic JSON conversion support
	 * 
	 */
	public static class TeamHostChangeList extends ArrayList<SingleTeamHostChange> {
		private static final long serialVersionUID = -3946462970562661784L;

		public static Map<String, String> generateTeamHostsMap(final List<SingleTeamHostChange> singleTeamHostChanges) {
			Map<String, String> teamHostMapping = new HashMap<String, String>();
			for (SingleTeamHostChange teamHostChange : singleTeamHostChanges) {
				String teamKey = teamHostChange.getTeamKey();
				teamHostMapping.put(teamKey, teamHostChange.getNewHostParticipantKey());
			}
			return teamHostMapping;
		}
	}
}