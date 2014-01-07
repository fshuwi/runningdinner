package org.runningdinner.ui.dto;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.runningdinner.core.GenderAspects;
import org.runningdinner.core.MealClass;

public class CreateWizardModel {

	private int teamSize;

	private String title;

	private Date date;

	private String city;

	private boolean equalTeamDistribution;

	private GenderAspects genderTeamDistribution;

	private Set<MealClass> meals;

	private String uploadedFilePath;

	protected CreateWizardModel() {
	}

	public static CreateWizardModel newModelWithDefaults() {
		CreateWizardModel result = new CreateWizardModel();
		result.initDefaults();
		return result;
	}

	public void initDefaults() {
		this.teamSize = 2;
		this.genderTeamDistribution = GenderAspects.IGNORE_GENDER;
		this.equalTeamDistribution = true;

		this.meals = new LinkedHashSet<MealClass>(3);
		this.meals.add(MealClass.APPETIZER);
		this.meals.add(MealClass.MAINCOURSE);
		this.meals.add(MealClass.DESSERT);
	}

	/**
	 * Set default times to each meal
	 */
	public void prepareDefaultTimes() {
		for (MealClass meal : meals) {
			if (meal.getTime() == null) {
				DateTime dinnerTime = new DateTime(getDate().getTime());
				dinnerTime = dinnerTime.withHourOfDay(19);
				meal.setTime(dinnerTime.toDate());
			}
		}
	}

	/**
	 * Set day of dinner-date to the times of each meal
	 */
	public void applyDateToMealTimes() {
		for (MealClass meal : meals) {
			if (meal.getTime() != null) {
				DateTime dinnerTime = new DateTime(meal.getTime().getTime());
				DateTime dinnerDate = new DateTime(getDate().getTime());
				dinnerTime = dinnerTime.withDayOfYear(dinnerDate.getDayOfYear());
				meal.setTime(dinnerTime.toDate());

				// TODO: Set new day for times > 24 Uhr
			}
		}
	}

	public int getTeamSize() {
		return teamSize;
	}

	public void setTeamSize(int teamSize) {
		this.teamSize = teamSize;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public boolean isEqualTeamDistribution() {
		return equalTeamDistribution;
	}

	public void setEqualTeamDistribution(boolean equalTeamDistribution) {
		this.equalTeamDistribution = equalTeamDistribution;
	}

	public GenderAspects getGenderTeamDistribution() {
		return genderTeamDistribution;
	}

	public void setGenderTeamDistribution(GenderAspects genderTeamDistribution) {
		this.genderTeamDistribution = genderTeamDistribution;
	}

	public Set<MealClass> getMeals() {
		return meals;
	}

	public void setMeals(Set<MealClass> meals) {
		this.meals = meals;
	}

	public String getUploadedFilePath() {
		return uploadedFilePath;
	}

	public void setUploadedFilePath(String uploadedFilePath) {
		this.uploadedFilePath = uploadedFilePath;
	}

}
