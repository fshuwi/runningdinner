package org.runningdinner.service.email;

import java.text.DateFormat;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Participant;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;

public class ParticipantMessageFormatter extends AbstractMessageFormatter {

	public ParticipantMessageFormatter(final MessageSource messageSource, final Locale locale) {
		super(messageSource, locale, null);
	}

	public ParticipantMessageFormatter(final MessageSource messageSource, final Locale locale, final DateFormat timeFormat) {
		super(messageSource, locale, timeFormat);
	}

	public String formatParticipantMessage(final Participant participant) {

		Assert.state(StringUtils.isNotEmpty(messageTemplate), "Message template must not be empty!");

		String theMessage = messageTemplate;
		theMessage = theMessage.replaceAll(FormatterUtil.FIRSTNAME, participant.getName().getFirstnamePart());
		theMessage = theMessage.replaceAll(FormatterUtil.LASTNAME, participant.getName().getLastname());

		return theMessage;
	}

}
