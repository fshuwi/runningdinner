package org.runningdinner.ui.validator;

import java.util.Set;

import org.runningdinner.core.MealClass;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class CommonValidator {

	public void validateMealTimes(Set<MealClass> meals, Errors errors) {
	}

}
