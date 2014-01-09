package org.runningdinner.ui.dto;

import org.runningdinner.core.GenderAspect;

/**
 * Simple wrapper around GenderAspect for having additional i18n labels
 * 
 * @author i01002492
 * 
 */
public class GenderAspectOption {

	private String label;
	private String value;

	public GenderAspectOption(GenderAspect genderAspect, String label) {
		this.label = label;
		this.value = genderAspect.name();
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

}
