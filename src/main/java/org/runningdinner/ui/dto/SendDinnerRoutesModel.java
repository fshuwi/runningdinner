package org.runningdinner.ui.dto;

import java.util.Locale;

import org.runningdinner.service.email.DinnerRouteMessageFormatter;
import org.springframework.context.MessageSource;

public class SendDinnerRoutesModel extends BaseSendMailsModel {

	public static SendDinnerRoutesModel createWithDefaultMessageTemplate(final MessageSource messageSource, final Locale locale) {
		SendDinnerRoutesModel result = new SendDinnerRoutesModel();
		result.message = messageSource.getMessage("message.template.dinnerroutes", null, locale);
		return result;
	}

	public DinnerRouteMessageFormatter getDinnerRouteMessageFormatter(Locale locale) {
		// MAybe use locale and concrete dateformatter instance!
		DinnerRouteMessageFormatter result = new DinnerRouteMessageFormatter();
		result.setMessageTemplate(message);
		result.setSubject(subject);
		return result;
	}

	@Override
	public String toString() {
		return "SendDinnerRoutesModel [subject=" + subject + ", message=" + message + "]";
	}

}
