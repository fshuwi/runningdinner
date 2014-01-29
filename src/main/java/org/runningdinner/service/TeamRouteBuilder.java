package org.runningdinner.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.runningdinner.core.MealClass;
import org.runningdinner.core.Team;

public class TeamRouteBuilder {

	/**
	 * Returns a list which contains per each meal the host-teams for the passed team and also the team itself in the correct order.
	 * The list is ordered by the times of the meals, so it can e.g. be happen the the passed team is in the middle of the list (e.g. if it
	 * cooks the main-course)
	 * 
	 * @param team
	 * @return
	 */
	public static List<Team> generateDinnerRoute(final Team team) {

		Map<MealClass, Team> mealTeamMapping = new HashMap<MealClass, Team>();
		mealTeamMapping.put(team.getMealClass(), team);

		Set<Team> hostTeams = team.getVisitationPlan().getHostTeams();
		for (Team hostTeam : hostTeams) {
			mealTeamMapping.put(hostTeam.getMealClass(), hostTeam);
		}

		List<MealClass> allMeals = new ArrayList<MealClass>(mealTeamMapping.keySet());
		Collections.sort(allMeals, new MealSorter());

		ArrayList<Team> teamDinnerRoute = new ArrayList<Team>();
		for (MealClass orderedMeal : allMeals) {
			teamDinnerRoute.add(mealTeamMapping.get(orderedMeal));
		}

		return teamDinnerRoute;
	}

	static class MealSorter implements Comparator<MealClass> {
		@Override
		public int compare(MealClass mealClass1, MealClass mealClass2) {
			return mealClass1.getTime().compareTo(mealClass2.getTime());
		}

	}
}
