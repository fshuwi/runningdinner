package org.runningdinner.ui.util;

import java.util.Date;
import java.util.Set;

import org.joda.time.DateTime;
import org.runningdinner.core.MealClass;

public class MealClassHelper {

	/**
	 * Set default times to each meal
	 */
	public static void prepareDefaultTimes(final Set<MealClass> meals, final Date date) {
		for (MealClass meal : meals) {
			if (meal.getTime() == null) {
				DateTime dinnerTime = new DateTime(date.getTime());
				dinnerTime = dinnerTime.withHourOfDay(19);
				meal.setTime(dinnerTime.toDate());
			}
		}
	}

	/**
	 * Set day of dinner-date to the times of each meal
	 */
	public static void applyDateToMealTimes(final Set<MealClass> meals, final Date dateOfDinner) {
		for (MealClass meal : meals) {
			if (meal.getTime() != null) {
				DateTime dinnerTime = new DateTime(meal.getTime().getTime());
				DateTime dinnerDate = new DateTime(dateOfDinner.getTime());
				dinnerTime = dinnerTime.withDayOfYear(dinnerDate.getDayOfYear());
				meal.setTime(dinnerTime.toDate());

				// TODO: Set new day for times > 24 Uhr
			}
		}
	}
}
