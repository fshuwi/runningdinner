package org.runningdinner.ui.dto;

import java.util.ArrayList;
import java.util.List;

import org.runningdinner.core.Team;

public class SendMailsPreviewModel {

	protected Team team;
	protected String participantNames;

	protected String subject;
	protected List<String> messages;

	public SendMailsPreviewModel() {
	}

	public SendMailsPreviewModel(Team team) {
		this.team = team;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Short-hand for printing out comma separated list with names of all team members in JSP
	 * 
	 * @return
	 */
	public String getParticipantNames() {
		return participantNames;
	}

	public void setParticipantNames(String participantNames) {
		this.participantNames = participantNames;
	}

	public void addMessage(final String message) {
		if (this.messages == null) {
			this.messages = new ArrayList<String>();
		}
		this.messages.add(message);
	}
}
