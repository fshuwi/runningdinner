package org.runningdinner.ui.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Team;
import org.runningdinner.ui.RequestMappings;

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

	public static SwitchTeamMembersResponse createSuccessResponse(List<Team> changedTeams, final String dinnerUuid) {
		SwitchTeamMembersResponse result = new SwitchTeamMembersResponse();

		List<TeamWrapper> teamWrappers = new ArrayList<TeamWrapper>();
		for (Team team : changedTeams) {
			teamWrappers.add(new TeamWrapper(team));
		}
		result.setChangedTeams(teamWrappers);

		// Add Edit Links for team members:
		for (TeamWrapper teamWrapper : teamWrappers) {
			List<TeamMemberWrapper> teamMembers = teamWrapper.getTeamMembers();
			for (TeamMemberWrapper teamMemberWrapper : teamMembers) {
				String editLink = RequestMappings.EDIT_PARTICIPANT;
				editLink = editLink.replaceFirst("\\{" + RequestMappings.ADMIN_URL_UUID_MARKER + "\\}", dinnerUuid);
				editLink = editLink.replaceFirst("\\{key\\}", teamMemberWrapper.getNaturalKey());
				teamMemberWrapper.setEditLink(editLink);
			}
		}

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
