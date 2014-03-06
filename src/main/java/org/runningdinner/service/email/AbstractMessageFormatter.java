package org.runningdinner.service.email;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.web.util.HtmlUtils;

public abstract class AbstractMessageFormatter {

	protected String subject;
	protected String messageTemplate;

	protected DateFormat timeFormat;

	protected MessageSource messageSource;
	protected Locale locale;

	public AbstractMessageFormatter(final MessageSource messageSource, final Locale locale) {
		this(messageSource, locale, null);
	}

	public AbstractMessageFormatter(final MessageSource messageSource, final Locale locale, final DateFormat timeFormat) {
		this.timeFormat = timeFormat;
		this.messageSource = messageSource;
		this.locale = locale;
		if (timeFormat == null) {
			this.timeFormat = new SimpleDateFormat(FormatterUtil.DEFAULT_TIME_FORMAT, Locale.GERMAN); // Fallback
		}
	}

	/**
	 * Takes the given input string and formats it as html escaped message
	 * 
	 * @param inputMessage
	 * @return
	 */
	public String getHtmlFormattedMessage(final String inputMessage) {
		String htmlMessage = HtmlUtils.htmlEscape(inputMessage);
		htmlMessage = htmlMessage.replaceAll("\r\n", "<br/>");
		return htmlMessage;
	}

	public String getMessageTemplate() {
		return messageTemplate;
	}

	public DateFormat getTimeFormat() {
		return timeFormat;
	}

	public MessageSource getMessageSource() {
		return messageSource;
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

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
