package org.runningdinner.ui.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.model.BaseMailReport;

public class BaseSendMailsModel {

	protected String subject = StringUtils.EMPTY;
	protected String message = StringUtils.EMPTY;

	protected List<String> selectedEntities = new ArrayList<String>();
	protected Map<String, String> entityDisplayMap;

	protected BaseMailReport lastMailReport;

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

	/**
	 * Returns the natural keys of all selected entities for sending mails (this can either be participants or teams)
	 * 
	 * @return
	 */
	public List<String> getSelectedEntities() {
		return selectedEntities;
	}

	public void setSelectedEntities(List<String> selectedEntities) {
		this.selectedEntities = selectedEntities;
	}

	/**
	 * Holds all entities that are capable for sending mails (which can be either participants or teams).<br>
	 * The keys of the map holds the natural key whereas the value holds a human readable label of the entity
	 * 
	 * @return
	 */
	public Map<String, String> getEntityDisplayMap() {
		return entityDisplayMap;
	}

	public void setEntityDisplayMap(Map<String, String> entityDisplayMap) {
		this.entityDisplayMap = entityDisplayMap;
	}

	public BaseMailReport getLastMailReport() {
		return lastMailReport;
	}

	public boolean isCurrentlySending() {
		return lastMailReport != null && lastMailReport.isSending();
	}

	public void setLastMailReport(BaseMailReport lastMailReport) {
		this.lastMailReport = lastMailReport;
	}

	@Override
	public String toString() {
		return "subject=" + subject + ", message=" + message + ", selectedEntities=" + selectedEntities;
	}

}
