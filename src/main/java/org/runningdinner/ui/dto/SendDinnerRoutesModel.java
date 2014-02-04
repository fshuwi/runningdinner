package org.runningdinner.ui.dto;

import java.util.Locale;

import org.runningdinner.service.email.DinnerRouteMessageFormatter;
import org.runningdinner.service.email.FormatterUtil;

public class SendDinnerRoutesModel extends BaseSendMailsModel {

	public static SendDinnerRoutesModel createWithDefaultMessageTemplate() {
		SendDinnerRoutesModel result = new SendDinnerRoutesModel();

		StringBuilder tmp = new StringBuilder();
		tmp.append("Hallo {firstname},").append(FormatterUtil.TWO_NEWLINES).append("hier ist eure Dinner-Route: ").append(
				FormatterUtil.TWO_NEWLINES);
		tmp.append("{route}").append(FormatterUtil.TWO_NEWLINES).append("Bitte versucht euch an die Zeitpläne zu halten!");
		result.message = tmp.toString();
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
