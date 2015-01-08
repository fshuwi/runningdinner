package org.runningdinner.ui.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.runningdinner.core.MealClass;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.ui.RequestMappings;

/**
 * Wraps up the attributes of a team that is used for displaying the results in a JSON-AJAX reqest
 * 
 * @author i01002492
 * 
 */
public class TeamWrapper {

	protected String naturalKey;
	protected int teamNumber;
	protected List<TeamMemberWrapper> teamMembers;

	protected MealClass mealClass;

	/**
	 * Shortcut for displaying the zip of the current host
	 */
	protected String hostZip = "Unknown";

	protected List<TeamReferenceWrapper> hostTeams = new ArrayList<TeamReferenceWrapper>();

	protected List<TeamReferenceWrapper> guestTeams = new ArrayList<TeamReferenceWrapper>();
	
	protected boolean wellbalancedDistribution;
	protected boolean unbalancedDistribution;
	protected boolean overbalancedDistribution;

	public TeamWrapper(final Team wrappedTeam, final String dinnerUuid) {
		this.naturalKey = wrappedTeam.getNaturalKey();
		this.teamNumber = wrappedTeam.getTeamNumber();
		this.teamMembers = new ArrayList<TeamMemberWrapper>();
		for (Participant participant : wrappedTeam.getTeamMembers()) {
			
			TeamMemberWrapper teamMemberWrapper = new TeamMemberWrapper(participant.getName().getFullnameFirstnameFirst(),
					participant.getNaturalKey(), participant.isHost());
			
			addEditLink(teamMemberWrapper, dinnerUuid);
			
			teamMemberWrapper.setZip(participant.getAddress().getZip());
			
			this.teamMembers.add(teamMemberWrapper);
			
			if (participant.isHost()) {
				this.hostZip = String.valueOf(participant.getAddress().getZip());
			}
		}

		this.mealClass = wrappedTeam.getMealClass();
	}

	public TeamWrapper(final Team wrappedTeam, final String dinnerUuid, final boolean populateHostsAndGuests) {
		this(wrappedTeam, dinnerUuid);
		if (populateHostsAndGuests) {
			Set<Team> hostTeams = wrappedTeam.getVisitationPlan().getHostTeams();
			Set<Team> guestTeams = wrappedTeam.getVisitationPlan().getGuestTeams();
			this.hostTeams = convertToTeamReferences(hostTeams);
			this.guestTeams = convertToTeamReferences(guestTeams);
		}
	}
	
	protected void addEditLink(TeamMemberWrapper teamMemberWrapper, String dinnerUuid) {
		String editLink = RequestMappings.EDIT_PARTICIPANT;
		editLink = editLink.replaceFirst("\\{" + RequestMappings.ADMIN_URL_UUID_MARKER + "\\}", dinnerUuid);
		editLink = editLink.replaceFirst("\\{key\\}", teamMemberWrapper.getNaturalKey());
		teamMemberWrapper.setEditLink(editLink);
	}


	protected List<TeamReferenceWrapper> convertToTeamReferences(final Set<Team> teams) {
		List<TeamReferenceWrapper> result = new ArrayList<TeamReferenceWrapper>();
		for (Team team : teams) {
			TeamReferenceWrapper teamReferenceWrapper = new TeamReferenceWrapper(team);
			result.add(teamReferenceWrapper);
		}
		return result;
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

	public String getHostZip() {
		return hostZip;
	}

	public void setHostZip(String hostZip) {
		this.hostZip = hostZip;
	}

	public MealClass getMealClass() {
		return mealClass;
	}

	public void setMealClass(MealClass mealClass) {
		this.mealClass = mealClass;
	}

	public List<TeamReferenceWrapper> getHostTeams() {
		return hostTeams;
	}

	public void setHostTeams(List<TeamReferenceWrapper> hostTeams) {
		this.hostTeams = hostTeams;
	}

	public List<TeamReferenceWrapper> getGuestTeams() {
		return guestTeams;
	}

	public void setGuestTeams(List<TeamReferenceWrapper> guestTeams) {
		this.guestTeams = guestTeams;
	}

	@Override
	public String toString() {
		return "TeamWrapper [naturalKey=" + naturalKey + ", teamNumber=" + teamNumber + ", teamMembers=" + teamMembers + "]";
	}

	public boolean isWellbalancedDistribution() {
		return wellbalancedDistribution;
	}

	public void setWellbalancedDistribution(boolean wellbalancedDistribution) {
		this.wellbalancedDistribution = wellbalancedDistribution;
		if (wellbalancedDistribution) {
			this.unbalancedDistribution = false;
			this.overbalancedDistribution = false;
		}
	}

	public boolean isUnbalancedDistribution() {
		return unbalancedDistribution;
	}

	public void setUnbalancedDistribution(boolean unbalancedDistribution) {
		this.unbalancedDistribution = unbalancedDistribution;
		if (unbalancedDistribution) {
			this.wellbalancedDistribution = false;
			this.overbalancedDistribution = false;
		}
	}

	public boolean isOverbalancedDistribution() {
		return overbalancedDistribution;
	}

	public void setOverbalancedDistribution(boolean overbalancedDistribution) {
		this.overbalancedDistribution = overbalancedDistribution;
		if (overbalancedDistribution) {
			this.wellbalancedDistribution = false;
			this.unbalancedDistribution = false;
		}
	}	
	
}
