package org.runningdinner.ui.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;

/**
 * Wraps up the attributes of a team that is used for displaying the results in a JSON-AJaX reqest
 * 
 * @author i01002492
 * 
 */
public class TeamWrapper {
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
