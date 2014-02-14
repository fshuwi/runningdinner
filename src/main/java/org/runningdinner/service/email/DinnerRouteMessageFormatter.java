package org.runningdinner.service.email;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.springframework.context.MessageSource;

public class DinnerRouteMessageFormatter {

	private String messageTemplate;
	private String selfTemplate;
	private String hostsTemplate;

	private DateFormat timeFormat;

	private String subject;

	private MessageSource messageSource;
	private Locale locale;

	public DinnerRouteMessageFormatter(final MessageSource messageSource, final Locale locale) {
		this(messageSource, locale, null);
	}

	public DinnerRouteMessageFormatter(final MessageSource messageSource, final Locale locale, final DateFormat timeFormat) {
		this.timeFormat = timeFormat;
		this.messageSource = messageSource;
		this.locale = locale;
		if (timeFormat == null) {
			this.timeFormat = new SimpleDateFormat(FormatterUtil.DEFAULT_TIME_FORMAT, Locale.GERMAN); // Fallback
		}
	}

	public String formatDinnerRouteMessage(final Participant teamMember, final Team parentTeam, final List<Team> dinnerRoute) {

		final String noTimeText = messageSource.getMessage("message.template.no.time", null, locale);

		String theMessage = messageTemplate;
		theMessage = theMessage.replaceAll(FormatterUtil.FIRSTNAME, teamMember.getName().getFirstnamePart());
		theMessage = theMessage.replaceAll(FormatterUtil.LASTNAME, teamMember.getName().getLastname());

		StringBuilder plan = new StringBuilder();
		for (Team dinnerRouteTeam : dinnerRoute) {

			Participant hostTeamMember = dinnerRouteTeam.getHostTeamMember();

			String mealLabel = dinnerRouteTeam.getMealClass().getLabel();
			Date mealTime = dinnerRouteTeam.getMealClass().getTime();

			// The plan-part for the team of the participant to which to send this message:
			if (dinnerRouteTeam.equals(parentTeam)) {
				String self = selfTemplate;
				self = self.replaceAll(FormatterUtil.FIRSTNAME, hostTeamMember.getName().getFirstnamePart());
				self = self.replaceAll(FormatterUtil.LASTNAME, hostTeamMember.getName().getLastname());
				self = self.replaceAll(FormatterUtil.MEAL, mealLabel);
				self = self.replaceAll(FormatterUtil.MEALTIME, CoreUtil.getFormattedTime(mealTime, timeFormat, noTimeText));
				plan.append(self);
			}
			// The plan-part(s) for the host-teams:
			else {
				String host = hostsTemplate;
				host = host.replaceAll(FormatterUtil.FIRSTNAME, hostTeamMember.getName().getFirstnamePart());
				host = host.replaceAll(FormatterUtil.LASTNAME, hostTeamMember.getName().getLastname());
				host = host.replaceAll(FormatterUtil.MEAL, mealLabel);
				host = host.replaceAll(FormatterUtil.MEALTIME, CoreUtil.getFormattedTime(mealTime, timeFormat, noTimeText));

				String address = hostTeamMember.getAddress().getStreetWithNr() + FormatterUtil.NEWLINE
						+ hostTeamMember.getAddress().getZipWithCity();
				host = host.replaceFirst(FormatterUtil.HOSTADDRESS, address);

				plan.append(host);
			}

			plan.append(FormatterUtil.TWO_NEWLINES).append(FormatterUtil.NEWLINE);
		}

		theMessage = theMessage.replaceFirst(FormatterUtil.ROUTE, plan.toString());

		return theMessage;

	}

	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public void setSelfTemplate(String selfTemplate) {
		this.selfTemplate = selfTemplate;
	}

	public void setHostsTemplate(String hostsTemplate) {
		this.hostsTemplate = hostsTemplate;
	}

	public void setTimeFormat(DateFormat timeFormat) {
		this.timeFormat = timeFormat;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
