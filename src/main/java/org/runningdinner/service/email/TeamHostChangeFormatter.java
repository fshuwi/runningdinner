package org.runningdinner.service.email;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.service.impl.UrlGenerator;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;

public class TeamHostChangeFormatter extends AbstractMessageFormatter {

	private UrlGenerator urlGenerator;

	public TeamHostChangeFormatter(MessageSource messageSource, Locale locale, UrlGenerator urlGenerator) {
		super(messageSource, locale);
		this.urlGenerator = urlGenerator;
		this.messageTemplate = messageSource.getMessage("message.template.team.host.changed", null, locale);
	}

	public String formatTeamHostChangeMessage(final Team team, final Participant participant, final String comment,
			final Participant teamHostEditor) {

		Assert.state(StringUtils.isNotEmpty(messageTemplate), "Message template must not be empty!");

		String message = this.messageTemplate.replaceAll(FormatterUtil.PARTNER, teamHostEditor.getName().getFullnameFirstnameFirst());

		if (StringUtils.isNotEmpty(comment)) {
			String commentTemplate = messageSource.getMessage("message.template.team.host.changed.comment", null, locale);
			commentTemplate = commentTemplate.replaceAll(FormatterUtil.PARTNER_MESSAGE, comment);
			commentTemplate = commentTemplate.replaceAll(FormatterUtil.FIRSTNAME, teamHostEditor.getName().getFirstnamePart());
			message = message.replaceAll(FormatterUtil.PARTNER_MESSAGE, commentTemplate);
		}
		else {
			message = message.replaceAll(FormatterUtil.PARTNER_MESSAGE, StringUtils.EMPTY);
		}

		message = message.replaceAll(FormatterUtil.FIRSTNAME, participant.getName().getFirstnamePart());
		String partnerEmail = StringUtils.isEmpty(teamHostEditor.getEmail()) ? messageSource.getMessage("message.template.no.email", null,
				locale) : teamHostEditor.getEmail();
		message = message.replaceAll(FormatterUtil.EMAIL, partnerEmail);

		String arrangementMessage = getArrangementMessage(team, participant);

		String manageHostLink = urlGenerator.constructManageHostUrl(team.getNaturalKey(), participant.getNaturalKey());
		message = message.replaceAll(FormatterUtil.MANGE_HOST_LINK, manageHostLink);
		message = message.replaceAll(FormatterUtil.ARRANGEMENT, arrangementMessage);

		return message;
	}

	private String getArrangementMessage(final Team team, final Participant participant) {

		String result = null;
		final Participant hostTeamMember = team.getHostTeamMember();

		if (hostTeamMember.equals(participant)) {
			result = messageSource.getMessage("message.template.team.host.changed.arrangement.you", null, locale);
		}
		else {
			result = messageSource.getMessage("message.template.team.host.changed.arrangement.other", null, locale);
			result = result.replaceAll(FormatterUtil.FIRSTNAME, hostTeamMember.getName().getFirstnamePart());
			result = result.replaceAll(FormatterUtil.LASTNAME, hostTeamMember.getName().getLastname());
		}

		return result;
	}
}
