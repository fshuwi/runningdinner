package org.runningdinner.model;

import java.util.ArrayList;
import java.util.List;

import org.runningdinner.core.Team;

public class VisitationPlanInfo {

	private Team team;

	private List<Team> hostTeams = new ArrayList<Team>(2);

	private List<Team> guestTeams = new ArrayList<Team>(2);

	public VisitationPlanInfo(Team team) {
		this.team = team;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public List<Team> getHostTeams() {
		return hostTeams;
	}

	public void setHostTeams(List<Team> hostTeams) {
		this.hostTeams = hostTeams;
	}

	public List<Team> getGuestTeams() {
		return guestTeams;
	}

	public void setGuestTeams(List<Team> guestTeams) {
		this.guestTeams = guestTeams;
	}

	public void addHostTeam(final Team team) {
		this.hostTeams.add(team);
	}

	public void addGuestTeam(final Team team) {
		this.guestTeams.add(team);
	}
}
