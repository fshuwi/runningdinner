package org.runningdinner.ui.validator;

import java.util.Set;

import org.runningdinner.core.MealClass;
import org.springframework.validation.Errors;

public abstract class CommonBaseValidator {

	public void validateMealTimes(Set<MealClass> meals, Errors errors) {
		// TODO Implement
	}

}
