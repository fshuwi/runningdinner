package org.runningdinner.ui.dto;

import java.util.Locale;

import org.runningdinner.service.email.TeamArrangementMessageFormatter;
import org.springframework.context.MessageSource;

public class SendTeamArrangementsModel extends BaseSendMailsModel {

	protected String hostMessagePartTemplate;
	protected String nonHostMessagePartTemplate;

	public static SendTeamArrangementsModel createWithDefaultMessageTemplate(final MessageSource messageSource, final Locale locale) {
		SendTeamArrangementsModel result = new SendTeamArrangementsModel();

		result.message = messageSource.getMessage("message.template.teams", null, locale);
		result.hostMessagePartTemplate = messageSource.getMessage("message.template.teams.host", null, locale);
		result.nonHostMessagePartTemplate = messageSource.getMessage("message.template.teams.nonhost", null, locale);

		return result;
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

	public TeamArrangementMessageFormatter getTeamArrangementMessageFormatter(final MessageSource messageSource, final Locale locale) {
		// Maybe use locale for dateformat!
		TeamArrangementMessageFormatter result = new TeamArrangementMessageFormatter(messageSource, locale);
		result.setMessageTemplate(message);
		result.setHostMessagePartTemplate(hostMessagePartTemplate);
		result.setNonHostMessagePartTemplate(nonHostMessagePartTemplate);
		result.setSubject(subject);
		return result;
	}
}
