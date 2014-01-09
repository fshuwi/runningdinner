package org.runningdinner.model;

import java.util.Date;

import org.runningdinner.core.MealClass;

public class MealWithTime {

	private MealClass mealClass;

	private Date time;

	public MealWithTime(MealClass mealClass) {
		this.mealClass = mealClass;
	}

	public MealClass getMealClass() {
		return mealClass;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getLabel() {
		return mealClass.getLabel();
	}
}
