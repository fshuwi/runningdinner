package org.runningdinner.ui.dto;

import java.util.Locale;

import org.runningdinner.service.email.DinnerRouteMessageFormatter;
import org.runningdinner.service.impl.UrlGenerator;
import org.springframework.context.MessageSource;

public class SendDinnerRoutesModel extends BaseSendMailsModel {

	private static final long serialVersionUID = -8005797744787342783L;

	protected String selfTemplate;
	protected String hostsTemplate;

	public static SendDinnerRoutesModel createWithDefaultMessageTemplate(final MessageSource messageSource, final Locale locale) {
		SendDinnerRoutesModel result = new SendDinnerRoutesModel();

		result.message = messageSource.getMessage("message.template.dinnerroutes", null, locale);
		result.selfTemplate = messageSource.getMessage("message.template.dinnerroutes.self", null, locale);
		result.hostsTemplate = messageSource.getMessage("message.template.dinnerroutes.hosts", null, locale);

		return result;
	}

	public DinnerRouteMessageFormatter getDinnerRouteMessageFormatter(final MessageSource messageSource, final Locale locale, final UrlGenerator urlGenerator) {
		// MAybe use locale and concrete dateformatter instance!
		DinnerRouteMessageFormatter result = new DinnerRouteMessageFormatter(messageSource, locale);
		result.setMessageTemplate(message);
		result.setSelfTemplate(selfTemplate);
		result.setHostsTemplate(hostsTemplate);
		result.setSubject(subject);
		result.setUrlGenerator(urlGenerator);
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
