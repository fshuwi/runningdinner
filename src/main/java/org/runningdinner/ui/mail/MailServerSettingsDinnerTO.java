package org.runningdinner.ui.mail;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.runningdinner.service.email.MailServerSettings;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MailServerSettingsDinnerTO {

	private String dinnerUuid;

	private MailServerSettings mailServerSettings;

	public MailServerSettingsDinnerTO() {
	}

	public MailServerSettingsDinnerTO(String dinnerUuid, MailServerSettings mailServerSettings) {
		this.dinnerUuid = dinnerUuid;
		this.mailServerSettings = mailServerSettings;
	}

	public MailServerSettingsDinnerTO(MailServerSettings mailServerSettings) {
		this.mailServerSettings = mailServerSettings;
	}

	public String getDinnerUuid() {
		return dinnerUuid;
	}

	public void setDinnerUuid(String dinnerUuid) {
		this.dinnerUuid = dinnerUuid;
	}

	public MailServerSettings getMailServerSettings() {
		return mailServerSettings;
	}

	public void setMailServerSettings(MailServerSettings mailServerSettings) {
		this.mailServerSettings = mailServerSettings;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 2).append(dinnerUuid).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof MailServerSettingsDinnerTO)) {
			return false;
		}

		MailServerSettingsDinnerTO mailServerSettingsDinnerTO = (MailServerSettingsDinnerTO)obj;
		return new EqualsBuilder().append(dinnerUuid, mailServerSettingsDinnerTO.dinnerUuid).isEquals();
	}

	@Override
	public String toString() {
		return dinnerUuid + " - " + mailServerSettings.toString();
	}

}
