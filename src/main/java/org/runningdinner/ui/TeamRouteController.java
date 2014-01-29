package org.runningdinner.ui;

import java.util.Arrays;
import java.util.List;

import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.service.TeamRouteBuilder;
import org.runningdinner.service.impl.RunningDinnerServiceImpl;
import org.runningdinner.ui.validator.AdminValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TeamRouteController {

	private RunningDinnerServiceImpl runningDinnerService;

	private AdminValidator adminValidator;

	@RequestMapping(value = "/team/{key}/route", method = RequestMethod.GET)
	public String showTeamDinnerRoute(@PathVariable("key") String teamKey, Model model) {

		adminValidator.validateNaturalKeys(Arrays.asList(teamKey));

		Team team = runningDinnerService.loadSingleTeamWithVisitationPlan(teamKey);

		String participantNames = generateParticipantNames(team);

		List<Team> teamDinnerRoute = TeamRouteBuilder.generateDinnerRoute(team);

		model.addAttribute("participantNames", participantNames);
		model.addAttribute("teamDinnerRoute", teamDinnerRoute);
		model.addAttribute("currentTeamKey", teamKey);

		return getFullViewName("route");
	}

	protected String generateParticipantNames(Team team) {
		StringBuilder result = new StringBuilder();
		int cnt = 0;
		for (Participant teamMember : team.getTeamMembers()) {
			if (cnt++ > 0) {
				result.append(", ");
			}
			String fullname = teamMember.getName().getFullnameFirstnameFirst();
			result.append(fullname);
		}
		return result.toString();
	}

	protected String getFullViewName(final String viewName) {
		return "dinnerroute/" + viewName;
	}

	@Autowired
	public void setRunningDinnerService(RunningDinnerServiceImpl runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

	@Autowired
	public void setAdminValidator(AdminValidator adminValidator) {
		this.adminValidator = adminValidator;
	}

}
