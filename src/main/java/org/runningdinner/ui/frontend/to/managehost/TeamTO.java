package org.runningdinner.ui.frontend.to.managehost;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;

public class TeamTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String naturalKey;

	private List<ParticipantTO> teamMembers = new ArrayList<>();

	public TeamTO() {
		super();
	}

	public TeamTO(final Team team) {
		this.naturalKey = team.getNaturalKey();
		
		Set<Participant> teamMembers = team.getTeamMembers();
		for (Participant teamMember : teamMembers) {
			this.addParticipant(new ParticipantTO(teamMember));
		}
	}

	public String getNaturalKey() {
		return naturalKey;
	}

	public void setNaturalKey(String naturalKey) {
		this.naturalKey = naturalKey;
	}

	public List<ParticipantTO> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(List<ParticipantTO> teamMembers) {
		this.teamMembers = teamMembers;
	}

	public void addParticipant(final ParticipantTO participant) {
		this.teamMembers.add(participant);
	}
	
}
