package org.runningdinner.ui.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Team;

/**
 * The response of a AJAX JSON request in which the members of two teams are switched.<br>
 * It contains the new team-teammember assignments as a result on success, or an error message.
 * 
 * @author i01002492
 * 
 */
public class SwitchTeamMembersResponse extends StandardJsonResponse {

	protected List<TeamWrapper> changedTeams;

	public List<TeamWrapper> getChangedTeams() {
		if (this.changedTeams == null) {
			return Collections.emptyList();
		}
		return changedTeams;
	}

	public void setChangedTeams(List<TeamWrapper> changedTeams) {
		this.changedTeams = changedTeams;
	}

	public static SwitchTeamMembersResponse createSuccessResponse(List<Team> changedTeams) {
		SwitchTeamMembersResponse result = new SwitchTeamMembersResponse();

		List<TeamWrapper> teamWrappers = new ArrayList<TeamWrapper>();
		for (Team team : changedTeams) {
			teamWrappers.add(new TeamWrapper(team));
		}
		result.setChangedTeams(teamWrappers);

		result.setSuccess(true);
		return result;
	}

	public static SwitchTeamMembersResponse createErrorResponse(final String message) {
		SwitchTeamMembersResponse result = new SwitchTeamMembersResponse();
		result.setChangedTeams(Collections.<TeamWrapper> emptyList());
		result.setSuccess(false);
		result.setErrorMessage(StringUtils.isEmpty(message) ? "Could not switch team members" : message);
		return result;
	}

}
