package org.runningdinner.ui.dto;

import java.util.Date;
import java.util.List;

import org.runningdinner.core.MealClass;
import org.runningdinner.ui.util.MealClassHelper;

public class EditMealTimesModel {

	private List<MealClass> meals;

	public List<MealClass> getMeals() {
		return meals;
	}

	public void setMeals(List<MealClass> meals) {
		this.meals = meals;
	}

	/**
	 * Set day of dinner-date to the times of each meal
	 */
	public void applyDateToMealTimes(Date dateOfDinner) {
		MealClassHelper.applyDateToMealTimes(meals, dateOfDinner);
	}
}
