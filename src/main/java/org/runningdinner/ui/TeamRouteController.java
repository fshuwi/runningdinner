package org.runningdinner.ui;

import java.util.Arrays;

import org.runningdinner.core.Team;
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

		return getFullViewName("route");
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
