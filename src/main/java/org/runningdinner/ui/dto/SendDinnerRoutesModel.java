package org.runningdinner.ui.dto;

import java.util.Locale;

import org.runningdinner.service.email.DinnerRouteMessageFormatter;
import org.springframework.context.MessageSource;

public class SendDinnerRoutesModel extends BaseSendMailsModel {

	protected String selfTemplate;
	protected String hostsTemplate;

	public static SendDinnerRoutesModel createWithDefaultMessageTemplate(final MessageSource messageSource, final Locale locale) {
		SendDinnerRoutesModel result = new SendDinnerRoutesModel();

		result.message = messageSource.getMessage("message.template.dinnerroutes", null, locale);
		result.selfTemplate = messageSource.getMessage("message.template.dinnerroutes.self", null, locale);
		result.hostsTemplate = messageSource.getMessage("message.template.dinnerroutes.hosts", null, locale);

		return result;
	}

	public DinnerRouteMessageFormatter getDinnerRouteMessageFormatter(final MessageSource messageSource, final Locale locale) {
		// MAybe use locale and concrete dateformatter instance!
		DinnerRouteMessageFormatter result = new DinnerRouteMessageFormatter(messageSource, locale);
		result.setMessageTemplate(message);
		result.setSelfTemplate(selfTemplate);
		result.setHostsTemplate(hostsTemplate);
		result.setSubject(subject);
		return result;
	}

	public String getSelfTemplate() {
		return selfTemplate;
	}

	public void setSelfTemplate(String selfTemplate) {
		this.selfTemplate = selfTemplate;
	}

	public String getHostsTemplate() {
		return hostsTemplate;
	}

	public void setHostsTemplate(String hostsTemplate) {
		this.hostsTemplate = hostsTemplate;
	}

	@Override
	public String toString() {
		return "SendDinnerRoutesModel [subject=" + subject + ", message=" + message + "]";
	}

}
