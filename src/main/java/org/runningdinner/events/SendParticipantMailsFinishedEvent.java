package org.runningdinner.events;

import java.util.Map;

import org.runningdinner.model.ParticipantMailReport;
import org.springframework.context.ApplicationEvent;

public class SendParticipantMailsFinishedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 4345522409306569878L;

	protected ParticipantMailReport participantMailReport;
	protected Map<String, Boolean> sendingResults;

	public SendParticipantMailsFinishedEvent(Object source, ParticipantMailReport participantMailReport, Map<String, Boolean> sendingResults) {
		super(source);
		this.participantMailReport = participantMailReport;
		this.sendingResults = sendingResults;
	}

	public ParticipantMailReport getParticipantMailReport() {
		return participantMailReport;
	}

	public void setParticipantMailReport(ParticipantMailReport participantMailReport) {
		this.participantMailReport = participantMailReport;
	}

	public Map<String, Boolean> getSendingResults() {
		return sendingResults;
	}

	public void setSendingResults(Map<String, Boolean> sendingResults) {
		this.sendingResults = sendingResults;
	}

}
