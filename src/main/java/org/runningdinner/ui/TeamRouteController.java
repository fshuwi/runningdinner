package org.runningdinner.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.core.dinnerplan.TeamRouteBuilder;
import org.runningdinner.core.util.CoreUtil;
import org.runningdinner.exceptions.GeocodingException;
import org.runningdinner.model.GeocodingResult;
import org.runningdinner.service.RunningDinnerService;
import org.runningdinner.service.email.FormatterUtil;
import org.runningdinner.service.geocoder.impl.GeocoderServiceCachedImpl;
import org.runningdinner.ui.route.HostTO;
import org.runningdinner.ui.route.TeamRouteEntryTO;
import org.runningdinner.ui.route.TeamRouteListTO;
import org.runningdinner.ui.validator.AdminValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private GeocoderServiceCachedImpl geocoderService;
	
	private static Logger LOGGER = LoggerFactory.getLogger(TeamRouteController.class);
	
	@RequestMapping(value = RequestMappings.TEAM_DINNER_ROUTE, method = RequestMethod.GET)
	public String showTeamDinnerRoute(@PathVariable("key") String teamKey, Model model, HttpServletRequest request) {

		adminValidator.validateNaturalKeys(Arrays.asList(teamKey));

		Team team = runningDinnerService.loadSingleTeamWithVisitationPlan(teamKey);

		List<Team> teamDinnerRoute = TeamRouteBuilder.generateDinnerRoute(team);

		TeamRouteListTO result = new TeamRouteListTO();
		for (Team teamInDinnerRoute : teamDinnerRoute) {
			TeamRouteEntryTO teamRouteEntry = toTeamRouteEntryTO(teamInDinnerRoute, teamKey);

			if (teamRouteEntry.isCurrentTeam()) {
				result.setTeamMemberNames(FormatterUtil.generateParticipantNamesWithCommas(teamInDinnerRoute));
			}
			result.addTeamRouteEntry(teamRouteEntry);
		}
		model.addAttribute("route", result);
		model.addAttribute("routejson", transformToJson(result));
		model.addAttribute("mobile", CoreUtil.isMobileBrowser(request.getHeader("User-Agent")));
		
		return "dinnerroute-ng/route-ng";
	}
	
	private String transformToJson(final TeamRouteListTO result) {
		
		ObjectMapper jsonMapper = new ObjectMapper();
		try {
			return jsonMapper.writeValueAsString(result);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	private TeamRouteEntryTO toTeamRouteEntryTO(final Team team, final String teamKey) {
		TeamRouteEntryTO result = new TeamRouteEntryTO();
		result.setMeal(team.getMealClass());
		result.setCurrentTeam(team.getNaturalKey().equals(teamKey));
		HostTO host = toHostTO(team.getHostTeamMember(), result.isCurrentTeam());
		result.setHost(host);
		result.setTeamNumber(team.getTeamNumber());
		return result;
	}

	private HostTO toHostTO(final Participant hostTeamMember, boolean isCurrentTeam) {
		HostTO result = new HostTO();
		result.setAddress(hostTeamMember.getAddress());
		result.setOnlyLastname(!isCurrentTeam);
		result.setName(result.isOnlyLastname() ? hostTeamMember.getName().getLastname() : hostTeamMember.getName().getFullnameFirstnameFirst());

		try {
			result.setGeocodes(geocoderService.geocodeAddress(hostTeamMember.getAddress(), Locale.GERMAN));
		}
		catch (GeocodingException e) {
			LOGGER.error("Failed to geocode adresse {} for participant {}", hostTeamMember.getAddress(), hostTeamMember.getName(), e);
			result.setGeocodes(Collections.<GeocodingResult>emptyList());
		}

		return result;
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

	@Autowired
	public void setGeocoderService(GeocoderServiceCachedImpl geocoderService) {
		this.geocoderService = geocoderService;
	}

	
}
