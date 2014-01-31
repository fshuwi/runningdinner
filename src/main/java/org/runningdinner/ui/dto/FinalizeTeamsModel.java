package org.runningdinner.ui.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.service.email.FormatterConstants;

public class FinalizeTeamsModel {

	protected boolean sendMessages = false;

	protected String subject = StringUtils.EMPTY;
	protected String message = StringUtils.EMPTY;

	protected String hostMessagePartTemplate;
	protected String nonHostMessagePartTemplate;

	protected List<String> selectedTeams = new ArrayList<String>();

	protected Map<String, String> teamDisplayMap;

	public static FinalizeTeamsModel createWithDefaultMessageTemplate() {
		FinalizeTeamsModel result = new FinalizeTeamsModel();

		StringBuilder tmp = new StringBuilder();
		tmp.append("Hallo {firstname} {lastname},").append(FormatterConstants.TWO_NEWLINES).append("dein(e) Tempartner ist/sind: ").append(
				FormatterConstants.NEWLINE);
		tmp.append("{partner}").append(FormatterConstants.TWO_NEWLINES).append("Ihr seid für folgende Speise verantwortlich: {meal}.");
		tmp.append("Diese soll um {mealtime} eingenommen werden.").append(FormatterConstants.TWO_NEWLINES);
		tmp.append("{host}");
		result.message = tmp.toString();
		result.hostMessagePartTemplate = "Es wird vorgeschlagen, dass du als Gastgeber fungierst. Wenn dies nicht in Ordnung ist, dann sprecht euch bitte ab und gebt uns bis spätestens Donnerstag Rückmeldung wer als neuer Gastgeber fungieren soll.";
		result.nonHostMessagePartTemplate = "Als Gastgeber wurde {partner} vorgeschlagen.";
		return result;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSendMessages() {
		return sendMessages;
	}

	public void setSendMessages(boolean sendMessages) {
		this.sendMessages = sendMessages;
	}

	public String getHostMessagePartTemplate() {
		return hostMessagePartTemplate;
	}

	public void setHostMessagePartTemplate(String hostMessagePartTemplate) {
		this.hostMessagePartTemplate = hostMessagePartTemplate;
	}

	public String getNonHostMessagePartTemplate() {
		return nonHostMessagePartTemplate;
	}

	public void setNonHostMessagePartTemplate(String nonHostMessagePartTemplate) {
		this.nonHostMessagePartTemplate = nonHostMessagePartTemplate;
	}

	public List<String> getSelectedTeams() {
		return selectedTeams;
	}

	public void setSelectedTeams(List<String> selectedTeams) {
		this.selectedTeams = selectedTeams;
	}

	public Map<String, String> getTeamDisplayMap() {
		return teamDisplayMap;
	}

	public void setTeamDisplayMap(Map<String, String> teamDisplayMap) {
		this.teamDisplayMap = teamDisplayMap;
	}

	@Override
	public String toString() {
		return "FinalizeTeamsModel [sendMessages=" + sendMessages + ", subject=" + subject + ", message=" + message + "]";
	}

}
