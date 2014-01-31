package org.runningdinner.events;

import java.util.List;

import org.runningdinner.core.Team;
import org.runningdinner.ui.dto.SendDinnerRoutesModel;
import org.springframework.context.ApplicationEvent;

public class SendDinnerRoutesEvent extends ApplicationEvent {

	private static final long serialVersionUID = 3729494458843378610L;

	protected List<Team> teams;
	protected SendDinnerRoutesModel sendDinnerRoutesModel;

	public SendDinnerRoutesEvent(final Object source, List<Team> teams, SendDinnerRoutesModel sendDinnerRoutesModel) {
		super(source);
		this.teams = teams;
		this.sendDinnerRoutesModel = sendDinnerRoutesModel;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public SendDinnerRoutesModel getSendDinnerRoutesModel() {
		return sendDinnerRoutesModel;
	}

}
