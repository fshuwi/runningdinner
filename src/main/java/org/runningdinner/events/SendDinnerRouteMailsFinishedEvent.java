package org.runningdinner.events;

import java.util.Map;

import org.runningdinner.model.DinnerRouteMailReport;
import org.springframework.context.ApplicationEvent;

public class SendDinnerRouteMailsFinishedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 664748909930716072L;

	protected DinnerRouteMailReport dinnerRouteMailReport;
	protected Map<String, Boolean> sendingResults;

	public SendDinnerRouteMailsFinishedEvent(Object source, DinnerRouteMailReport dinnerRouteMailReport, Map<String, Boolean> sendingResults) {
		super(source);
		this.dinnerRouteMailReport = dinnerRouteMailReport;
		this.sendingResults = sendingResults;
	}

	public DinnerRouteMailReport getDinnerRouteMailReport() {
		return dinnerRouteMailReport;
	}

	public Map<String, Boolean> getSendingResults() {
		return sendingResults;
	}
}
