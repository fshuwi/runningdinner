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
}
