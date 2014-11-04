package org.runningdinner.ui.validator;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.MealClass;
import org.springframework.validation.Errors;

/**
 * Provides some common validation methods that used in other validators
 * 
 * @author Clemens Stich
 * 
 */
public abstract class CommonBaseValidator {

	public void validateMealTimes(Collection<MealClass> meals, Errors errors) {
		// TODO Implement
	}
	
	public boolean isEmailValid(String email) {
		if (StringUtils.isNotEmpty(email)) {
			if (email.contains("@")) {
				return true;
			}
		}
		return false;
	}

}
