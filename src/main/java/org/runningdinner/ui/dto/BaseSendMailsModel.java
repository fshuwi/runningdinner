package org.runningdinner.ui.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.model.BaseMailReport;
import org.runningdinner.service.email.MailServerSettings;
import org.runningdinner.service.email.MailServerSettingsImpl;

public class BaseSendMailsModel implements MailServerSettings {

	protected String subject = StringUtils.EMPTY;
	protected String message = StringUtils.EMPTY;

	protected List<String> selectedEntities = new ArrayList<String>();
	protected Map<String, String> entityDisplayMap;

	protected BaseMailReport lastMailReport;

	protected MailServerSettingsImpl mailServerSettings = new MailServerSettingsImpl();

	protected boolean useCustomMailServer;

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

	public boolean isUseCustomMailServer() {
		return useCustomMailServer;
	}

	public void setUseCustomMailServer(boolean useCustomMailServer) {
		this.useCustomMailServer = useCustomMailServer;
	}

	// Delegate Methods:
	@Override
	public String getMailServer() {
		return mailServerSettings.getMailServer();
	}

	@Override
	public int getMailServerPort() {
		return mailServerSettings.getMailServerPort();
	}

	@Override
	public String getUsername() {
		return mailServerSettings.getUsername();
	}

	@Override
	public String getPassword() {
		return mailServerSettings.getPassword();
	}

	@Override
	public boolean isUseAuth() {
		return StringUtils.isNotEmpty(mailServerSettings.getUsername());
	}

	@Override
	public boolean isUseTls() {
		return mailServerSettings.isUseTls();
	}

	@Override
	public String getFrom() {
		return mailServerSettings.getFrom();
	}

	@Override
	public String getReplyTo() {
		return mailServerSettings.getReplyTo();
	}
	
	@Override
	public boolean hasMailServerPort() {
		return mailServerSettings.hasMailServerPort();
	}

	public void setMailServer(String mailServer) {
		mailServerSettings.setMailServer(mailServer);
	}

	public void setMailServerPort(int mailServerPort) {
		mailServerSettings.setMailServerPort(mailServerPort);
	}

	public void setUsername(String username) {
		mailServerSettings.setUsername(username);
		if (StringUtils.isNotEmpty(username)) {
			setUseAuth(true);
		}
	}

	public void setPassword(String password) {
		mailServerSettings.setPassword(password);
	}

	public void setUseAuth(boolean useAuth) {
		mailServerSettings.setUseAuth(useAuth);
	}

	public void setUseTls(boolean useTls) {
		mailServerSettings.setUseTls(useTls);
	}

	public void setFrom(String from) {
		mailServerSettings.setFrom(from);
		setReplyTo(from);
	}

	public void setReplyTo(String replyTo) {
		mailServerSettings.setReplyTo(replyTo);
	}
	
	
	/**
	 * Convenience method for setting all mail server settings at once 
	 * @param mailServerSettings
	 */
	public void setMailServerSettings(MailServerSettingsImpl mailServerSettings) {
		this.mailServerSettings = mailServerSettings;
	}

	@Override
	public String toString() {
		return "subject=" + subject + ", message=" + message + ", selectedEntities=" + selectedEntities;
	}
 
}
