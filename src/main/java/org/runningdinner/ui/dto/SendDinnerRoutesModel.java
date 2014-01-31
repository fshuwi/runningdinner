package org.runningdinner.ui.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.service.email.FormatterConstants;

public class SendDinnerRoutesModel {

	protected String subject = StringUtils.EMPTY;
	protected String message = StringUtils.EMPTY;

	protected List<String> selectedTeams = new ArrayList<String>();
	protected Map<String, String> teamDisplayMap;

	public static SendDinnerRoutesModel createWithDefaultMessageTemplate() {
		SendDinnerRoutesModel result = new SendDinnerRoutesModel();

		StringBuilder tmp = new StringBuilder();
		tmp.append("Hallo {firstname},").append(FormatterConstants.TWO_NEWLINES).append("hier ist eure Dinner-Route: ").append(
				FormatterConstants.TWO_NEWLINES);
		tmp.append("{route}").append(FormatterConstants.TWO_NEWLINES).append("Bitte versucht euch an die Zeitpläne zu halten!");
		result.message = tmp.toString();
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
		return "SendDinnerRoutesModel [subject=" + subject + ", message=" + message + "]";
	}

}
