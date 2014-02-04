package org.runningdinner.service.email;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;

public class TeamArrangementMessageFormatter {

	private String messageTemplate;
	private String hostMessagePartTemplate;
	private String nonHostMessagePartTemplate;

	private DateFormat timeFormat;

	private String subject;

	public TeamArrangementMessageFormatter() {
		this(null);
	}

	public TeamArrangementMessageFormatter(final DateFormat timeFormat) {
		this.timeFormat = timeFormat;
		if (timeFormat == null) {
			this.timeFormat = new SimpleDateFormat(FormatterUtil.DEFAULT_TIME_FORMAT, Locale.GERMAN); // Fallback
		}
	}

	public String formatTeamMemberMessage(final Participant teamMember, final Team parentTeam) {

		String theMessage = messageTemplate;
		theMessage = theMessage.replaceAll(FormatterUtil.FIRSTNAME, teamMember.getName().getFirstnamePart());
		theMessage = theMessage.replaceAll(FormatterUtil.LASTNAME, teamMember.getName().getLastname());
		theMessage = theMessage.replaceAll(FormatterUtil.MEAL, parentTeam.getMealClass().getLabel());
		theMessage = theMessage.replaceAll(FormatterUtil.MEALTIME,
				CoreUtil.getFormattedTime(parentTeam.getMealClass().getTime(), timeFormat, "Unbekannte Uhrzeit"));

		Set<Participant> partners = CoreUtil.excludeFromSet(teamMember, parentTeam.getTeamMembers());

		int cnt = 0;
		StringBuilder partnerInfo = new StringBuilder();
		for (Participant partner : partners) {

			if (cnt++ > 0) {
				partnerInfo.append(FormatterUtil.TWO_NEWLINES).append(FormatterUtil.NEWLINE);
			}

			String partnerName = partner.getName().getFullnameFirstnameFirst();
			String streetWithNr = partner.getAddress().getStreetWithNr();
			String zipWithCity = partner.getAddress().getZipWithCity();
			String partnerMail = "EMail: " + StringUtils.defaultIfEmpty(partner.getEmail(), "Keine EMail");
			String partnerMobile = "Handy-Nr: " + StringUtils.defaultIfEmpty(partner.getMobileNumber(), "Keine Handy-Nr");

			partnerInfo.append(partnerName).append(FormatterUtil.NEWLINE).append(streetWithNr).append(FormatterUtil.NEWLINE).append(
					zipWithCity).append(FormatterUtil.NEWLINE).append(partnerMail).append(FormatterUtil.NEWLINE).append(
					partnerMobile);
		}
		theMessage = theMessage.replaceFirst(FormatterUtil.PARTNER, partnerInfo.toString());

		Participant hostMember = parentTeam.getHostTeamMember();
		String hostReplacement = StringUtils.EMPTY;
		if (teamMember.equals(hostMember)) {
			hostReplacement = hostMessagePartTemplate;
		}
		else {
			hostReplacement = nonHostMessagePartTemplate;
		}

		hostReplacement = hostReplacement.replaceAll(FormatterUtil.PARTNER, hostMember.getName().getFullnameFirstnameFirst());
		theMessage = theMessage.replaceAll(FormatterUtil.HOST, hostReplacement);

		return theMessage;
	}

	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public void setHostMessagePartTemplate(String hostMessagePartTemplate) {
		this.hostMessagePartTemplate = hostMessagePartTemplate;
	}

	public void setNonHostMessagePartTemplate(String nonHostMessagePartTemplate) {
		this.nonHostMessagePartTemplate = nonHostMessagePartTemplate;
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
