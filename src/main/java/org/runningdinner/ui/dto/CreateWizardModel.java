package org.runningdinner.ui.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.runningdinner.core.GenderAspect;
import org.runningdinner.core.MealClass;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.runningdinner.model.RunningDinnerInfo;
import org.runningdinner.ui.util.MealClassHelper;

public class CreateWizardModel implements RunningDinnerInfo, Serializable {

	private static final long serialVersionUID = 4185546597131800604L;

	private int teamSize;

	private String title;

	private Date date;

	private String city;

	private boolean equalTeamDistribution;

	private GenderAspect genderTeamDistribution;

	private List<MealClass> meals;

	private String uploadedFileLocation;

	private String newUuid;

	private String administrationUrl;

	private String email;

	private ParsingConfiguration parsingConfiguration; // TODO: Maybe this isn't needed!

	protected CreateWizardModel() {
	}

	public static CreateWizardModel newModelWithDefaults() {
		CreateWizardModel result = new CreateWizardModel();
		result.initDefaults();
		return result;
	}

	public void initDefaults() {
		this.teamSize = 2;
		this.genderTeamDistribution = GenderAspect.IGNORE_GENDER;
		this.equalTeamDistribution = true;

		this.meals = new ArrayList<MealClass>(3);
		this.meals.add(MealClass.APPETIZER);
		this.meals.add(MealClass.MAINCOURSE);
		this.meals.add(MealClass.DESSERT);
	}

	/**
	 * Set default times to each meal
	 */
	public void prepareDefaultTimes() {
		MealClassHelper.prepareDefaultTimes(meals, getDate());
	}

	/**
	 * Set day of dinner-date to the times of each meal
	 */
	public void applyDateToMealTimes() {
		MealClassHelper.applyDateToMealTimes(meals, getDate());
	}

	/**
	 * Constructs a new running dinner configuration instance based upon the current settings in the wizard-model
	 * 
	 * @return
	 */
	public RunningDinnerConfig createRunningDinnerConfiguration() {
		return RunningDinnerConfig.newConfigurer().withEqualDistributedCapacityTeams(isEqualTeamDistribution()).withGenderAspects(
				getGenderTeamDistribution()).withTeamSize(getTeamSize()).havingMeals(getMeals()).build();
	}

	public int getTeamSize() {
		return teamSize;
	}

	public void setTeamSize(int teamSize) {
		this.teamSize = teamSize;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEqualTeamDistribution() {
		return equalTeamDistribution;
	}

	public void setEqualTeamDistribution(boolean equalTeamDistribution) {
		this.equalTeamDistribution = equalTeamDistribution;
	}

	public GenderAspect getGenderTeamDistribution() {
		return genderTeamDistribution;
	}

	public void setGenderTeamDistribution(GenderAspect genderTeamDistribution) {
		this.genderTeamDistribution = genderTeamDistribution;
	}

	public List<MealClass> getMeals() {
		return meals;
	}

	public void setMeals(List<MealClass> meals) {
		this.meals = meals;
	}

	public String getUploadedFileLocation() {
		return uploadedFileLocation;
	}

	public void setUploadedFileLocation(String uploadedFileLocation) {
		this.uploadedFileLocation = uploadedFileLocation;
	}

	public String getNewUuid() {
		return newUuid;
	}

	public void setNewUuid(String newUuid) {
		this.newUuid = newUuid;
	}

	public String getAdministrationUrl() {
		return administrationUrl;
	}

	public void setAdministrationUrl(String administrationUrl) {
		this.administrationUrl = administrationUrl;
	}

	// public ParsingConfiguration getParsingConfiguration() {
	// if (parsingConfiguration == null) {
	// return ParsingConfiguration.newDefaultConfiguration();
	// }
	// return parsingConfiguration;
	// }
	//
	// public void setParsingConfiguration(ParsingConfiguration parsingConfiguration) {
	// this.parsingConfiguration = parsingConfiguration;
	// }

}
