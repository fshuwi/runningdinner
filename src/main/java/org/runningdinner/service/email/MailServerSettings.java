package org.runningdinner.service.email;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonDeserialize(as=MailServerSettingsImpl.class)
public interface MailServerSettings extends Serializable {

	public String getMailServer();

	public int getMailServerPort();

	public String getUsername();

	public String getPassword();

	public boolean isUseAuth();

	public boolean isUseTls();

	public String getFrom();

	public String getReplyTo();

	public boolean hasMailServerPort();

}