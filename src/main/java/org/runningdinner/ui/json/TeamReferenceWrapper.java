package org.runningdinner.ui.json;

import org.runningdinner.core.MealClass;
import org.runningdinner.core.Team;

public class TeamReferenceWrapper {

	private int teamNumber;
	
	private MealClass mealClass;

	
	public TeamReferenceWrapper() {
	}

	public TeamReferenceWrapper(final Team team) {
		this.teamNumber = team.getTeamNumber();
		this.mealClass = team.getMealClass();
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public void setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
	}

	public MealClass getMealClass() {
		return mealClass;
	}

	public void setMealClass(MealClass mealClass) {
		this.mealClass = mealClass;
	}
	
	
	
}

