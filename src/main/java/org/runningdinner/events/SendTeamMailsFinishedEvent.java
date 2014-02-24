package org.runningdinner.events;

import java.util.Map;

import org.runningdinner.model.TeamMailReport;
import org.springframework.context.ApplicationEvent;

public class SendTeamMailsFinishedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 664748909930716072L;

	protected TeamMailReport teamMailReport;
	protected Map<String, Boolean> sendingResults;

	public SendTeamMailsFinishedEvent(Object source, TeamMailReport teamMailReport, Map<String, Boolean> sendingResults) {
		super(source);
		this.teamMailReport = teamMailReport;
		this.sendingResults = sendingResults;
	}

	public TeamMailReport getTeamMailReport() {
		return teamMailReport;
	}

	public void setTeamMailReport(TeamMailReport teamMailReport) {
		this.teamMailReport = teamMailReport;
	}

	public Map<String, Boolean> getSendingResults() {
		return sendingResults;
	}

	public void setSendingResults(Map<String, Boolean> sendingResults) {
		this.sendingResults = sendingResults;
	}

}
