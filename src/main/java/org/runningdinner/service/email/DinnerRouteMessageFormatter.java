package org.runningdinner.service.email;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.ui.dto.SendDinnerRoutesModel;

public class DinnerRouteMessageFormatter {

	private String messageTemplate;
	private SimpleDateFormat timeFormat;

	public DinnerRouteMessageFormatter(SendDinnerRoutesModel sendDinnerRoutesModel, final String timeFormat) {
		this.messageTemplate = sendDinnerRoutesModel.getMessage();

		String theTimeFormat = timeFormat;
		if (StringUtils.isEmpty(theTimeFormat)) {
			theTimeFormat = FormatterConstants.DEFAULT_TIME_FORMAT;
		}
		this.timeFormat = new SimpleDateFormat(theTimeFormat, Locale.GERMAN); // TODO: Hardcoded locale
	}

	public DinnerRouteMessageFormatter(SendDinnerRoutesModel sendDinnerRoutesModel) {
		this(sendDinnerRoutesModel, null);
	}

	public String formatDinnerRouteMessage(final Participant teamMember, final Team parentTeam, final List<Team> dinnerRoute) {

		String theMessage = messageTemplate;
		theMessage = theMessage.replaceAll(FormatterConstants.FIRSTNAME, teamMember.getName().getFirstnamePart());
		theMessage = theMessage.replaceAll(FormatterConstants.LASTNAME, teamMember.getName().getLastname());

		StringBuilder plan = new StringBuilder();
		for (Team dinnerRouteTeam : dinnerRoute) {

			Participant hostTeamMember = dinnerRouteTeam.getHostTeamMember();

			String mealLabel = dinnerRouteTeam.getMealClass().getLabel();
			Date mealTime = dinnerRouteTeam.getMealClass().getTime();

			// The plan-part for the team of the participant to which to send this message:
			if (dinnerRouteTeam.equals(parentTeam)) {
				plan.append(mealLabel + " bei EUCH").append(FormatterConstants.NEWLINE);
				plan.append("Gekocht wird bei ").append(hostTeamMember.getName().getFullnameFirstnameFirst()).append(
						FormatterConstants.NEWLINE);
				plan.append("Uhrzeit: ").append(getFormattedTime(mealTime)).append(" Uhr");
			}
			// The plan-part(s) for the host-teams:
			else {
				plan.append(mealLabel).append(FormatterConstants.NEWLINE).append("Wird gekocht bei: ").append(
						hostTeamMember.getName().getLastname()).append(" (Du siehst nur den Nachnamen)").append(FormatterConstants.NEWLINE);
				plan.append(hostTeamMember.getAddress().getStreetWithNr()).append(FormatterConstants.NEWLINE).append(
						hostTeamMember.getAddress().getZipWithCity()).append(FormatterConstants.NEWLINE);
				plan.append("Uhrzeit: ").append(getFormattedTime(mealTime)).append(" Uhr");
			}

			plan.append(FormatterConstants.TWO_NEWLINES).append(FormatterConstants.NEWLINE);
		}

		theMessage = theMessage.replaceFirst(FormatterConstants.PLAN, plan.toString());

		return theMessage;

	}

	protected String getFormattedTime(Date time) {
		if (time == null) {
			return "Unbekannte Uhrzeit";
		}
		return timeFormat.format(time);
	}
}
