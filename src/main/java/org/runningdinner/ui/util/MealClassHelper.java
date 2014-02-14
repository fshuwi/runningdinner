package org.runningdinner.ui.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.runningdinner.core.MealClass;

public class MealClassHelper {

	/**
	 * Set default times to each meal
	 */
	public static void prepareDefaultTimes(final List<MealClass> meals, final Date date) {
		int scrollingHour = 19;
		for (MealClass meal : meals) {
			if (meal.getTime() == null) {
				DateTime dinnerTime = new DateTime(date.getTime());
				dinnerTime = dinnerTime.withHourOfDay(scrollingHour);
				scrollingHour += 2; // This is just the default and can be edited by user
				meal.setTime(dinnerTime.toDate());
			}
		}

		Collections.sort(meals, new MealClassSorter());
	}

	/**
	 * Set day of dinner-date to the times of each meal. TODO Test
	 */
	public static void applyDateToMealTimes(final Collection<MealClass> meals, final Date dateOfDinner) {
		for (MealClass meal : meals) {
			if (meal.getTime() != null) {
				DateTime dinnerTime = new DateTime(meal.getTime().getTime()); // Date of meal (with exact time)
				DateTime dinnerDate = new DateTime(dateOfDinner.getTime()); // Date of dinner (just date, without time)

				final int mealHour = dinnerTime.getHourOfDay();
				final int mealMinute = dinnerTime.getMinuteOfHour();

				if (mealHour >= 0 && mealHour <= 6) {
					// Heuristic assumption: The dinner is performed on a nightly date that lasts to a new day (after midnight).
					// If so, increase the day by one:
					dinnerDate = dinnerDate.plusDays(1);
				}

				dinnerTime = dinnerDate.withHourOfDay(mealHour).withMinuteOfHour(mealMinute).withSecondOfMinute(0);

				meal.setTime(dinnerTime.toDate());
			}
		}
	}

	public static class MealClassSorter implements Comparator<MealClass> {

		@Override
		public int compare(MealClass mc1, MealClass mc2) {
			if (mc1.getTime() != null && mc2.getTime() != null) {
				return mc1.getTime().compareTo(mc2.getTime());
			}
			return 0;
		}

	}
}
