package org.runningdinner.service;

import java.util.List;

import org.runningdinner.core.Participant;
import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.core.Team;

public class DistanceCalculator {

	protected List<Team> teams;
	
	protected int[][] distances;
	
	public DistanceCalculator(List<Team> teams) {
		this.teams = teams;
		this.distances = new int[teams.size()][teams.size()];
	}
	
	public void calculate() {
		for (Team team : teams) {
			Participant host = team.getHostTeamMember();
			ParticipantAddress address = host.getAddress();
			
		}
	}
	
}
