package org.runningdinner.ui;

import java.util.Arrays;
import java.util.List;

import org.runningdinner.core.Team;
import org.runningdinner.service.RunningDinnerService;
import org.runningdinner.service.TeamRouteBuilder;
import org.runningdinner.service.email.FormatterUtil;
import org.runningdinner.ui.validator.AdminValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TeamRouteController {

	private RunningDinnerService runningDinnerService;

	private AdminValidator adminValidator;

	@RequestMapping(value = RequestMappings.TEAM_DINNER_ROUTE, method = RequestMethod.GET)
	public String showTeamDinnerRoute(@PathVariable("key") String teamKey, Model model) {

		adminValidator.validateNaturalKeys(Arrays.asList(teamKey));

		Team team = runningDinnerService.loadSingleTeamWithVisitationPlan(teamKey);

		String participantNames = FormatterUtil.generateParticipantNames(team);

		List<Team> teamDinnerRoute = TeamRouteBuilder.generateDinnerRoute(team);

		model.addAttribute("participantNames", participantNames);
		model.addAttribute("teamDinnerRoute", teamDinnerRoute);
		model.addAttribute("currentTeamKey", teamKey);

		return getFullViewName("route");
	}

	protected String getFullViewName(final String viewName) {
		return "dinnerroute/" + viewName;
	}

	@Autowired
	public void setRunningDinnerService(RunningDinnerService runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

	@Autowired
	public void setAdminValidator(AdminValidator adminValidator) {
		this.adminValidator = adminValidator;
	}

}
