package org.runningdinner.ui.dto;

import java.util.Locale;

import org.runningdinner.service.email.FormatterUtil;
import org.runningdinner.service.email.TeamArrangementMessageFormatter;

public class SendTeamArrangementsModel extends BaseSendMailsModel {

	protected String hostMessagePartTemplate;
	protected String nonHostMessagePartTemplate;

	public static SendTeamArrangementsModel createWithDefaultMessageTemplate() {
		SendTeamArrangementsModel result = new SendTeamArrangementsModel();

		StringBuilder tmp = new StringBuilder();
		tmp.append("Hallo {firstname} {lastname},").append(FormatterUtil.TWO_NEWLINES).append("dein(e) Tempartner ist/sind: ").append(
				FormatterUtil.NEWLINE);
		tmp.append("{partner}").append(FormatterUtil.TWO_NEWLINES).append("Ihr seid für folgende Speise verantwortlich: {meal}.");
		tmp.append("Diese soll um {mealtime} eingenommen werden.").append(FormatterUtil.TWO_NEWLINES);
		tmp.append("{host}");
		result.message = tmp.toString();
		result.hostMessagePartTemplate = "Es wird vorgeschlagen, dass du als Gastgeber fungierst. Wenn dies nicht in Ordnung ist, dann sprecht euch bitte ab und gebt uns bis spätestens Donnerstag Rückmeldung wer als neuer Gastgeber fungieren soll.";
		result.nonHostMessagePartTemplate = "Als Gastgeber wurde {partner} vorgeschlagen.";
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

	public TeamArrangementMessageFormatter getTeamArrangementMessageFormatter(Locale locale) {
		// Maybe use locale for dateformat!
		TeamArrangementMessageFormatter result = new TeamArrangementMessageFormatter();
		result.setMessageTemplate(message);
		result.setHostMessagePartTemplate(hostMessagePartTemplate);
		result.setNonHostMessagePartTemplate(nonHostMessagePartTemplate);
		result.setSubject(subject);
		return result;
	}
}
