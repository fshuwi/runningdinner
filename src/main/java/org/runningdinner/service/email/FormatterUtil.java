package org.runningdinner.service.email;

import java.util.Set;

import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.core.util.CoreUtil;

/**
 * Util class that is used for formatting email messages
 * 
 * @author Clemens Stich
 * 
 */
public class FormatterUtil {

	public static final String PARTNER = "\\{partner\\}";

	public static final String FIRSTNAME = "\\{firstname\\}";
	public static final String LASTNAME = "\\{lastname\\}";
	public static final String EMAIL = "\\{email\\}";

	public static final String MEAL = "\\{meal\\}";
	public static final String MEALTIME = "\\{mealtime\\}";
	public static final String HOST = "\\{host\\}";

	public static final String PARTNER_MESSAGE = "\\{partnermessage\\}";
	public static final String ARRANGEMENT = "\\{arrangement\\}";
	public static final String MANGE_HOST_LINK = "\\{managehostlink\\}";
	
	public static final String ROUTE = "\\{route\\}";
	public static final String ROUTELINK = "\\{routelink\\}";
	public static final String HOSTADDRESS = "\\{hostaddress\\}";

	public static final String NEWLINE = "\r\n";
	public static final String TWO_NEWLINES = NEWLINE + NEWLINE;

	public static final String DEFAULT_TIME_FORMAT = CoreUtil.getDefaultTimeFormat();

	/**
	 * Returns a comma separated string with all members of the passed team
	 * 
	 * @param team
	 * @return
	 */
	public static String generateParticipantNamesWithCommas(Team team) {
		return generateParticipantNames(team, ", ");
	}

	public static String generateParticipantNamesWithNewlines(Team team) {
		return generateParticipantNames(team, "\n");
	}

	public static String generateParticipantNames(Team team, String delimiter) {
		StringBuilder result = new StringBuilder();
		int cnt = 0;
		for (Participant teamMember : team.getTeamMembers()) {
			if (cnt++ > 0) {
				result.append(delimiter);
			}
			String fullname = teamMember.getName().getFullnameFirstnameFirst();
			result.append(fullname);
		}
		return result.toString();
	}

	/**
	 * Generates a string with the number of the passed team and its team-members
	 * 
	 * @param team
	 * @return
	 */
	public static String generateTeamLabel(Team team) {
		String result = "Team " + team.getTeamNumber();
		Set<Participant> teamMembers = team.getTeamMembers();
		if (CoreUtil.isEmpty(teamMembers)) {
			return result;
		}
		result += " (";
		result += generateParticipantNamesWithCommas(team);
		result += ")";
		return result;
	}

}
