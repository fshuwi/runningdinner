package org.runningdinner.ui.dto;

import org.runningdinner.core.Gender;

public class GenderOption {
	private String label;
	private String value;

	public GenderOption(Gender gender, String label) {
		this.label = label;
		this.value = gender.name();
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}
}
