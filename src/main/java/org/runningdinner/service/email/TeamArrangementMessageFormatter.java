package org.runningdinner.service.email;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.ui.dto.FinalizeTeamsModel;

public class TeamArrangementMessageFormatter {

	public static final String PARTNER = "\\{partner\\}";
	public static final String NAME = "\\{name\\}";
	public static final String MEAL = "\\{meal\\}";
	public static final String MEALTIME = "\\{mealtime\\}";
	public static final String HOST = "\\{host\\}";

	public static final String NEWLINE = "\r\n";
	public static final String TWO_NEWLINES = NEWLINE + NEWLINE;

	private String messageTemplate;
	private String hostMessagePartTemplate;
	private String nonHostMessagePartTemplate;

	private SimpleDateFormat timeFormat;

	public TeamArrangementMessageFormatter(FinalizeTeamsModel finalizeTeamsModel) {
		this(finalizeTeamsModel, null);
	}

	public TeamArrangementMessageFormatter(final FinalizeTeamsModel finalizeTeamsModel, final String timeFormat) {
		messageTemplate = finalizeTeamsModel.getMessage();
		hostMessagePartTemplate = finalizeTeamsModel.getHostMessagePartTemplate();
		nonHostMessagePartTemplate = finalizeTeamsModel.getNonHostMessagePartTemplate();

		String theTimeFormat = timeFormat;
		if (StringUtils.isEmpty(theTimeFormat)) {
			theTimeFormat = "HH:mm";
		}
		this.timeFormat = new SimpleDateFormat(theTimeFormat, Locale.GERMAN); // TODO: Hardcoded locale
	}

	public String formatTeamMemberMessage(final Participant teamMember, final Team parentTeam) {

		String theMessage = messageTemplate;
		theMessage = theMessage.replaceAll(NAME, teamMember.getName().getFullnameFirstnameFirst());
		theMessage = theMessage.replaceAll(MEAL, parentTeam.getMealClass().getLabel());
		theMessage = theMessage.replaceAll(MEALTIME, getFormattedTime(parentTeam.getMealClass().getTime()));

		Set<Participant> partners = CoreUtil.excludeFromSet(teamMember, parentTeam.getTeamMembers());

		int cnt = 0;
		StringBuilder partnerInfo = new StringBuilder();
		for (Participant partner : partners) {

			if (cnt++ > 0) {
				partnerInfo.append(TWO_NEWLINES).append(NEWLINE);
			}

			String partnerName = partner.getName().getFullnameFirstnameFirst();
			String streetWithNr = partner.getAddress().getStreetWithNr();
			String zipWithCity = partner.getAddress().getZipWithCity();
			String partnerMail = "EMail: " + StringUtils.defaultIfEmpty(partner.getEmail(), "Keine EMail");
			String partnerMobile = "Handy-Nr: " + StringUtils.defaultIfEmpty(partner.getMobileNumber(), "Keine Handy-Nr");

			partnerInfo.append(partnerName).append(NEWLINE).append(streetWithNr).append(NEWLINE).append(zipWithCity).append(NEWLINE).append(
					partnerMail).append(NEWLINE).append(partnerMobile);
		}
		theMessage = theMessage.replaceFirst(PARTNER, partnerInfo.toString());

		Participant hostMember = getHostMember(parentTeam);
		String hostReplacement = StringUtils.EMPTY;
		if (teamMember.equals(hostMember)) {
			hostReplacement = hostMessagePartTemplate;
		}
		else {
			hostReplacement = nonHostMessagePartTemplate;
		}

		hostReplacement = hostReplacement.replaceAll(PARTNER, hostMember.getName().getFullnameFirstnameFirst());
		theMessage = theMessage.replaceAll(HOST, hostReplacement);

		return theMessage;
	}

	private Participant getHostMember(final Team parentTeam) {
		Set<Participant> teamMembers = parentTeam.getTeamMembers();
		for (Participant teamMember : teamMembers) {
			if (teamMember.isHost()) {
				return teamMember;
			}
		}
		throw new IllegalStateException("No host found within team " + parentTeam);
	}

	protected String getFormattedTime(Date time) {
		if (time == null) {
			return "Unbekannte Uhrzeit";
		}
		return timeFormat.format(time);
	}
}
