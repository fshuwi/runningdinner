package org.runningdinner.ui.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Team;

public class SaveTeamHostsResponse extends StandardJsonResponse {

	private static final long serialVersionUID = 1L;
	
	protected List<TeamWrapper> savedTeams;

	public List<TeamWrapper> getSavedTeams() {
		if (this.savedTeams == null) {
			return Collections.emptyList();
		}
		return savedTeams;
	}

	public void setSavedTeams(List<TeamWrapper> savedTeams) {
		this.savedTeams = savedTeams;
	}

	public static SaveTeamHostsResponse createSuccessResponse(final List<Team> savedTeams, final String dinnerUuid) {
		SaveTeamHostsResponse result = new SaveTeamHostsResponse();

		List<TeamWrapper> teamWrappers = new ArrayList<TeamWrapper>();
		for (Team team : savedTeams) {
			teamWrappers.add(new TeamWrapper(team, dinnerUuid));
		}
		result.setSavedTeams(teamWrappers);

		result.setSuccess(true);
		return result;
	}

	public static SaveTeamHostsResponse createErrorResponse(final String message) {
		SaveTeamHostsResponse result = new SaveTeamHostsResponse();
		result.setSavedTeams(Collections.<TeamWrapper> emptyList());
		result.setSuccess(false);
		result.setErrorMessage(StringUtils.isEmpty(message) ? "Could not save team hosts" : message);
		return result;
	}

}
