package org.runningdinner.ui.util;

import java.util.Collection;

/**
 * Simple helper class which provides a JSP tag methods that do not exist in standard
 * 
 * @author i01002492
 * 
 */
public class JspTagUtility {

	public static <T> boolean contains(final Collection<T> collection, T element) {
		return collection != null && collection.contains(element);
	}

	public static <T> T getValueOrDefault(final T value, final T defaultValue) {
		if (value == null) {
			return defaultValue;
		}

		if (value instanceof String) {
			String strValue = (String)value;
			if (strValue.trim().length() == 0) {
				return defaultValue;
			}
		}

		return value;

	}
}
