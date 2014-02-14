package org.runningdinner.ui.validator;

import java.util.Collection;

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

}
