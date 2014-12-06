package org.runningdinner.ui.route;

import org.runningdinner.core.MealClass;

public class TeamRouteEntryTO {

	protected boolean isCurrentTeam;

	protected MealClass meal;

	protected HostTO host;

	public boolean isCurrentTeam() {
		return isCurrentTeam;
	}

	public void setCurrentTeam(boolean isCurrentTeam) {
		this.isCurrentTeam = isCurrentTeam;
	}

	public MealClass getMeal() {
		return meal;
	}

	public void setMeal(MealClass meal) {
		this.meal = meal;
	}

	public HostTO getHost() {
		return host;
	}

	public void setHost(HostTO host) {
		this.host = host;
	}

}
