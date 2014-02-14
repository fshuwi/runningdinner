package org.runningdinner.ui.dto;

import org.runningdinner.core.Gender;
import org.runningdinner.core.GenderAspect;

/**
 * Simple wrapper around enumerations (that are used in select-inputs) for having additional i18n labels as displayname.
 * 
 * @author Clemens Stich
 * 
 */
public class SelectOption {

	private String label;
	private String value;

	protected SelectOption(String value, String label) {
		this.label = label;
		this.value = value;
	}

	protected SelectOption() {
	}

	public static SelectOption newGenderAspectOption(GenderAspect genderAspect, String label) {
		return new SelectOption(genderAspect.name(), label);
	}

	public static SelectOption newGenderOption(Gender gender, String label) {
		return new SelectOption(gender.name(), label);
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "SelectOption [label=" + label + ", value=" + value + "]";
	}

}
