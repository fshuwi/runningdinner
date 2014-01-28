package org.runningdinner.ui.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;

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

	static class TeamWrapper {
		protected String naturalKey;
		protected int teamNumber;
		protected List<TeamMemberWrapper> teamMembers;

		public TeamWrapper(Team wrappedTeam) {
			this.naturalKey = wrappedTeam.getNaturalKey();
			this.teamNumber = wrappedTeam.getTeamNumber();
			this.teamMembers = new ArrayList<TeamMemberWrapper>();
			for (Participant participant : wrappedTeam.getTeamMembers()) {
				this.teamMembers.add(new TeamMemberWrapper(participant.getName().getFullnameFirstnameFirst(), participant.getNaturalKey(),
						participant.isHost()));
			}
		}

		public TeamWrapper() {
		}

		public String getNaturalKey() {
			return naturalKey;
		}

		public void setNaturalKey(String naturalKey) {
			this.naturalKey = naturalKey;
		}

		public int getTeamNumber() {
			return teamNumber;
		}

		public void setTeamNumber(int teamNumber) {
			this.teamNumber = teamNumber;
		}

		public List<TeamMemberWrapper> getTeamMembers() {
			if (this.teamMembers == null) {
				return Collections.emptyList();
			}
			return teamMembers;
		}

		public void setTeamMembers(List<TeamMemberWrapper> teamMembers) {
			this.teamMembers = teamMembers;
		}

		@Override
		public String toString() {
			return "TeamWrapper [naturalKey=" + naturalKey + ", teamNumber=" + teamNumber + ", teamMembers=" + teamMembers + "]";
		}

	}

	static class TeamMemberWrapper {

		protected String fullname;
		protected String naturalKey;
		protected boolean host;

		public TeamMemberWrapper() {
		}

		public TeamMemberWrapper(String fullname, String naturalKey, boolean host) {
			this.fullname = fullname;
			this.naturalKey = naturalKey;
			this.host = host;
		}

		public String getFullname() {
			return fullname;
		}

		public void setFullname(String fullname) {
			this.fullname = fullname;
		}

		public String getNaturalKey() {
			return naturalKey;
		}

		public void setNaturalKey(String naturalKey) {
			this.naturalKey = naturalKey;
		}

		public boolean isHost() {
			return host;
		}

		public void setHost(boolean host) {
			this.host = host;
		}

		@Override
		public String toString() {
			return fullname;
		}
	}
}
