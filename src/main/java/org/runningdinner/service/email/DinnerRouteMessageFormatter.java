package org.runningdinner.service.email;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;

public class DinnerRouteMessageFormatter extends AbstractMessageFormatter {

	protected String selfTemplate;
	protected String hostsTemplate;

	public DinnerRouteMessageFormatter(final MessageSource messageSource, final Locale locale) {
		super(messageSource, locale, null);
	}

	public DinnerRouteMessageFormatter(final MessageSource messageSource, final Locale locale, final DateFormat timeFormat) {
		super(messageSource, locale, timeFormat);
	}

	public String formatDinnerRouteMessage(final Participant teamMember, final Team parentTeam, final List<Team> dinnerRoute) {

		Assert.state(StringUtils.isNotEmpty(messageTemplate), "Message template must not be empty!");
		Assert.state(StringUtils.isNotEmpty(selfTemplate), "Self part template must not be empty!");
		Assert.state(StringUtils.isNotEmpty(hostsTemplate), "Hosts part template must not be empty!");

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

	public void setSelfTemplate(String selfTemplate) {
		this.selfTemplate = selfTemplate;
	}

	public void setHostsTemplate(String hostsTemplate) {
		this.hostsTemplate = hostsTemplate;
	}

}
