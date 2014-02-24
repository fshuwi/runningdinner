package org.runningdinner.events;

import java.util.List;

import org.runningdinner.core.Team;
import org.runningdinner.model.DinnerRouteMailReport;
import org.runningdinner.service.email.DinnerRouteMessageFormatter;
import org.springframework.context.ApplicationEvent;

/**
 * This event is published whenever a user wants to send dinner route messages
 * 
 * @author Clemens Stich
 * 
 */
public class SendDinnerRoutesEvent extends ApplicationEvent {

	private static final long serialVersionUID = 3729494458843378610L;

	protected List<Team> teams;
	protected DinnerRouteMessageFormatter dinnerRouteMessageFormatter;
	protected DinnerRouteMailReport dinnerRouteMailReport;

	public SendDinnerRoutesEvent(final Object source, List<Team> teams, DinnerRouteMessageFormatter dinnerRouteMessageFormatter,
			DinnerRouteMailReport dinnerRouteMailReport) {
		super(source);
		this.teams = teams;
		this.dinnerRouteMessageFormatter = dinnerRouteMessageFormatter;
		this.dinnerRouteMailReport = dinnerRouteMailReport;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public DinnerRouteMessageFormatter getDinnerRouteMessageFormatter() {
		return dinnerRouteMessageFormatter;
	}

	public DinnerRouteMailReport getDinnerRouteMailReport() {
		return dinnerRouteMailReport;
	}

}
