package org.runningdinner.ui.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Needed for Spring's automatic JSON conversion support
 * 
 */
public class TeamHostChangeList extends ArrayList<SingleTeamParticipantChange> {

	private static final long serialVersionUID = -3946462970562661784L;

	/**
	 * Generates out of a list of pased SingleTeamParticipantChange objects a map which holds the team as key and the participant as value
	 * for indicating the new host of a team.
	 * 
	 * @param singleTeamHostChanges
	 * @return
	 */
	public static Map<String, String> generateTeamHostsMap(final List<SingleTeamParticipantChange> singleTeamHostChanges) {
		Map<String, String> teamHostMapping = new HashMap<String, String>();
		for (SingleTeamParticipantChange teamHostChange : singleTeamHostChanges) {
			String teamKey = teamHostChange.getTeamKey();
			teamHostMapping.put(teamKey, teamHostChange.getParticipantKey());
		}
		return teamHostMapping;
	}
}