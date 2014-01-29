package org.runningdinner.service.email;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;

public class DinnerRouteMessageFormatter {

	private String messageTemplate;
	private SimpleDateFormat timeFormat;

	public DinnerRouteMessageFormatter(final String messageTemplate, final String timeFormat) {
		this.messageTemplate = messageTemplate;

		String theTimeFormat = timeFormat;
		if (StringUtils.isEmpty(theTimeFormat)) {
			theTimeFormat = FormatterConstants.DEFAULT_TIME_FORMAT;
		}
		this.timeFormat = new SimpleDateFormat(theTimeFormat, Locale.GERMAN); // TODO: Hardcoded locale
	}

	public String formatDinnerRouteMessage(final Participant teamMember, final Team parentTeam) {
		throw new UnsupportedOperationException("not yet impl");
	}
}
