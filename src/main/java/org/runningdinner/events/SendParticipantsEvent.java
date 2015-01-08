package org.runningdinner.events;

import java.util.List;

import org.runningdinner.core.Participant;
import org.runningdinner.model.ParticipantMailReport;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.email.MailServerSettings;
import org.runningdinner.service.email.ParticipantMessageFormatter;

/**
 * This event is published whenever a user wants to send messages to (all) participants
 * 
 * @author Clemens Stich
 * 
 */
public class SendParticipantsEvent extends BaseAdminMailEvent {

	private static final long serialVersionUID = -268961430347730710L;

	protected List<Participant> participants;
	protected ParticipantMessageFormatter participantMessageFormatter;
	protected ParticipantMailReport participantMailReport;

	public SendParticipantsEvent(final Object source, RunningDinner dinner, List<Participant> participants,
			ParticipantMessageFormatter participantMessageFormatter, ParticipantMailReport participantMailReport,
			MailServerSettings customMailServerSettings) {
		super(source, dinner, customMailServerSettings);
		this.participants = participants;
		this.participantMessageFormatter = participantMessageFormatter;
		this.participantMailReport = participantMailReport;
	}

	public List<Participant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}

	public ParticipantMessageFormatter getParticipantMessageFormatter() {
		return participantMessageFormatter;
	}

	public void setParticipantMessageFormatter(ParticipantMessageFormatter participantMessageFormatter) {
		this.participantMessageFormatter = participantMessageFormatter;
	}

	public ParticipantMailReport getParticipantMailReport() {
		return participantMailReport;
	}

	public void setParticipantMailReport(ParticipantMailReport participantMailReport) {
		this.participantMailReport = participantMailReport;
	}

}
