package org.runningdinner.service.email;

public interface FormatterConstants {

	public static final String PARTNER = "\\{partner\\}";
	public static final String NAME = "\\{name\\}";
	public static final String MEAL = "\\{meal\\}";
	public static final String MEALTIME = "\\{mealtime\\}";
	public static final String HOST = "\\{host\\}";

	public static final String NEWLINE = "\r\n";
	public static final String TWO_NEWLINES = NEWLINE + NEWLINE;

	public static final String DEFAULT_TIME_FORMAT = "HH:mm";
}
