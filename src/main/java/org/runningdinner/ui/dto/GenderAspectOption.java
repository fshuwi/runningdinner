package org.runningdinner.ui.dto;

import org.runningdinner.core.GenderAspects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenderAspectOption {

	private static transient final Logger LOGGER = LoggerFactory.getLogger(GenderAspectOption.class);

	private String label;
	private String value;

	public GenderAspectOption(GenderAspects genderAspect, String label) {
		this.label = label;
		this.value = genderAspect.name();
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	public GenderAspects toGenderAspect() {
		try {
			return GenderAspects.valueOf(value);
		}
		catch (Exception ex) {
			LOGGER.error("Could not convert value {} to GenderAspects. Fallback to IGNORE_GENDER", value, ex);
			return GenderAspects.IGNORE_GENDER;
		}
	}
}
