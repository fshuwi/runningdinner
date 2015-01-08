package org.runningdinner.events;

import org.runningdinner.core.Team;
import org.runningdinner.model.ChangeTeamHost;
import org.springframework.context.ApplicationEvent;

public class TeamHostChangedByParticipantEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private ChangeTeamHost changeTeamHost;
	private Team team;

	public TeamHostChangedByParticipantEvent(final Object source, final Team team, final ChangeTeamHost changeTeamHost) {
		super(source);
		this.changeTeamHost = changeTeamHost;
		this.team = team;
	}

	public ChangeTeamHost getChangeTeamHost() {
		return changeTeamHost;
	}

	public void setChangeTeamHost(ChangeTeamHost changeTeamHost) {
		this.changeTeamHost = changeTeamHost;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

}
