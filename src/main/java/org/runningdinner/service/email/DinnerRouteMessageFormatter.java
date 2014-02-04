package org.runningdinner.service.email;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;

public class DinnerRouteMessageFormatter {

	private String messageTemplate;
	private DateFormat timeFormat;

	private String subject;

	public DinnerRouteMessageFormatter() {
		this(null);
	}

	public DinnerRouteMessageFormatter(final DateFormat timeFormat) {
		this.timeFormat = timeFormat;
		if (timeFormat == null) {
			this.timeFormat = new SimpleDateFormat(FormatterUtil.DEFAULT_TIME_FORMAT, Locale.GERMAN); // Fallback
		}
	}

	public String formatDinnerRouteMessage(final Participant teamMember, final Team parentTeam, final List<Team> dinnerRoute) {

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
				plan.append(mealLabel + " bei EUCH").append(FormatterUtil.NEWLINE);
				plan.append("Gekocht wird bei ").append(hostTeamMember.getName().getFullnameFirstnameFirst()).append(
						FormatterUtil.NEWLINE);
				plan.append("Uhrzeit: ").append(CoreUtil.getFormattedTime(mealTime, timeFormat, "Unbekannte Uhrzeit")).append(" Uhr");
			}
			// The plan-part(s) for the host-teams:
			else {
				plan.append(mealLabel).append(FormatterUtil.NEWLINE).append("Wird gekocht bei: ").append(
						hostTeamMember.getName().getLastname()).append(" (Du siehst nur den Nachnamen)").append(FormatterUtil.NEWLINE);
				plan.append(hostTeamMember.getAddress().getStreetWithNr()).append(FormatterUtil.NEWLINE).append(
						hostTeamMember.getAddress().getZipWithCity()).append(FormatterUtil.NEWLINE);
				plan.append("Uhrzeit: ").append(CoreUtil.getFormattedTime(mealTime, timeFormat, "Unbekannte Uhrzeit")).append(" Uhr");
			}

			plan.append(FormatterUtil.TWO_NEWLINES).append(FormatterUtil.NEWLINE);
		}

		theMessage = theMessage.replaceFirst(FormatterUtil.ROUTE, plan.toString());

		return theMessage;

	}

	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
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
