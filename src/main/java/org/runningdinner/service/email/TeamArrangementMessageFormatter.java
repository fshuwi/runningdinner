package org.runningdinner.service.email;

import java.text.DateFormat;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.core.util.CoreUtil;
import org.runningdinner.service.impl.UrlGenerator;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;

public class TeamArrangementMessageFormatter extends AbstractMessageFormatter {

	protected String hostMessagePartTemplate;
	protected String nonHostMessagePartTemplate;
	
	protected UrlGenerator urlGenerator;

	public TeamArrangementMessageFormatter(final MessageSource messageSource, final Locale locale) {
		super(messageSource, locale, null);
	}

	public TeamArrangementMessageFormatter(final MessageSource messageSource, final Locale locale, final DateFormat timeFormat) {
		super(messageSource, locale, timeFormat);
	}

	public String formatTeamMemberMessage(final Participant teamMember, final Team parentTeam) {

		Assert.state(StringUtils.isNotEmpty(messageTemplate), "Message template must not be empty!");
		Assert.state(StringUtils.isNotEmpty(hostMessagePartTemplate), "Hosting part template must not be empty!");
		Assert.state(StringUtils.isNotEmpty(nonHostMessagePartTemplate), "Non Hosting part template must not be empty!");

		final String noTimeText = messageSource.getMessage("message.template.no.time", null, locale);
		final String noEmailText = messageSource.getMessage("message.template.no.email", null, locale);
		final String noMobileText = messageSource.getMessage("message.template.no.mobile", null, locale);
		final String mobileLabel = messageSource.getMessage("label.participant.mobile", null, locale);
		final String emailLabel = messageSource.getMessage("label.participant.email", null, locale);

		String theMessage = messageTemplate;
		theMessage = theMessage.replaceAll(FormatterUtil.FIRSTNAME, teamMember.getName().getFirstnamePart());
		theMessage = theMessage.replaceAll(FormatterUtil.LASTNAME, teamMember.getName().getLastname());
		theMessage = theMessage.replaceAll(FormatterUtil.MEAL, parentTeam.getMealClass().getLabel());
		theMessage = theMessage.replaceAll(FormatterUtil.MEALTIME,
				CoreUtil.getFormattedTime(parentTeam.getMealClass().getTime(), timeFormat, noTimeText));

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
			String partnerMail = emailLabel + ": " + StringUtils.defaultIfEmpty(partner.getEmail(), noEmailText);
			String partnerMobile = mobileLabel + ": " + StringUtils.defaultIfEmpty(partner.getMobileNumber(), noMobileText);

			partnerInfo.append(partnerName).append(FormatterUtil.NEWLINE).append(streetWithNr).append(FormatterUtil.NEWLINE).append(
					zipWithCity).append(FormatterUtil.NEWLINE).append(partnerMail).append(FormatterUtil.NEWLINE).append(partnerMobile);
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

		final String manageHostLink = urlGenerator.constructManageHostUrl(parentTeam.getNaturalKey(), teamMember.getNaturalKey());
		theMessage = theMessage.replaceAll(FormatterUtil.MANGE_HOST_LINK, manageHostLink);
		
		return theMessage;
	}

	public void setHostMessagePartTemplate(String hostMessagePartTemplate) {
		this.hostMessagePartTemplate = hostMessagePartTemplate;
	}

	public void setNonHostMessagePartTemplate(String nonHostMessagePartTemplate) {
		this.nonHostMessagePartTemplate = nonHostMessagePartTemplate;
	}

	public void setUrlGenerator(UrlGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
}
