package org.runningdinner.ui.util;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.runningdinner.core.MealClass;

/**
 * Provides the means to marshall/unmarshall a set of MealClass objects into/from JSON.<br>
 * Used in Wizard when several meals are dynamically added/removed
 * 
 * @author Clemens Stich
 * 
 */
public class MealClassPropertyEditor extends PropertyEditorSupport {

	@SuppressWarnings("unchecked")
	@Override
	public String getAsText() {
		Set<MealClass> meals = (Set<MealClass>)getValue();
		try {
			return getJsonObjectMapper().writeValueAsString(meals);
		}
		catch (Exception e) {
			throw new RuntimeException("Could not write json string for " + meals, e);
		}
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		try {
			Set<MealClass> meals = getJsonObjectMapper().readValue(text, new TypeReference<Set<MealClass>>() {});
			this.setValue(meals);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Could not convert json string " + text + " to Set<MealClass>", e);
		}
	}

	protected ObjectMapper getJsonObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("HH:mm"));
		return mapper;
	}
}
