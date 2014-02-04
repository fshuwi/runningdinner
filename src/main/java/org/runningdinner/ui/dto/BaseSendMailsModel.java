package org.runningdinner.ui.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public abstract class BaseSendMailsModel {

	protected String subject = StringUtils.EMPTY;
	protected String message = StringUtils.EMPTY;

	protected List<String> selectedTeams = new ArrayList<String>();
	protected Map<String, String> teamDisplayMap;

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
		return "BaseSendMailsModel [subject=" + subject + ", message=" + message + ", selectedTeams=" + selectedTeams + "]";
	}

}