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
import org.runningdinner.model.ChangeTeamHost;
import org.runningdinner.model.GeocodingResult;
import org.runningdinner.service.RunningDinnerService;
import org.runningdinner.service.email.FormatterUtil;
import org.runningdinner.service.geocoder.impl.GeocoderServiceCachedImpl;
import org.runningdinner.ui.frontend.host.ChangeTeamHostTO;
import org.runningdinner.ui.frontend.to.managehost.TeamTO;
import org.runningdinner.ui.frontend.to.route.HostTO;
import org.runningdinner.ui.frontend.to.route.TeamRouteEntryTO;
import org.runningdinner.ui.frontend.to.route.TeamRouteListTO;
import org.runningdinner.ui.json.SingleTeamParticipantChange;
import org.runningdinner.ui.validator.AdminValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TeamFrontendController {

	private RunningDinnerService runningDinnerService;

	private AdminValidator adminValidator;

	private GeocoderServiceCachedImpl geocoderService;

	private MessageSource messages;

	private static Logger LOGGER = LoggerFactory.getLogger(TeamFrontendController.class);

	@RequestMapping(value = RequestMappings.TEAM_DINNER_ROUTE_FOR_ADMINS, method = RequestMethod.GET)
	public String showTeamDinnerRouteForAdmin(@PathVariable("teamKey") String teamKey, Model model, HttpServletRequest request) {

		adminValidator.validateNaturalKeys(Arrays.asList(teamKey));
		Team team = runningDinnerService.loadSingleTeamWithVisitationPlan(teamKey);
		return showTeamDinnerRoute(team, model, request);
	}

	@RequestMapping(value = RequestMappings.TEAM_DINNER_ROUTE_FOR_PARTICIPANT, method = RequestMethod.GET)
	public String showTeamDinnerRouteForParticipant(@PathVariable("teamKey") final String teamKey,
			@PathVariable("participantKey") final String participantKey, Model model, HttpServletRequest request, Locale locale) {

		adminValidator.validateNaturalKeys(Arrays.asList(teamKey, participantKey));

		final Team team = runningDinnerService.loadSingleTeamWithVisitationPlan(teamKey);
		
		validateParticipantContainedInTeam(team, participantKey);

		return showTeamDinnerRoute(team, model, request);
	}

	@RequestMapping(value = RequestMappings.TEAM_MANAGE_HOST, method = RequestMethod.GET)
	public String showManageHostForParticipant(@PathVariable("teamKey") final String teamKey,
			@PathVariable("participantKey") final String participantKey, Model model, HttpServletRequest request, Locale locale) {

		adminValidator.validateNaturalKeys(Arrays.asList(teamKey, participantKey));

		final Team team = runningDinnerService.loadSingleTeamWithVisitationPlan(teamKey);
		
		validateParticipantContainedInTeam(team, participantKey);

		TeamTO teamTO = new TeamTO(team);
		
		model.addAttribute("teamjson", transformToJson(teamTO));

		model.addAttribute("participantEditorKey", participantKey);
		
		return "dinnerroute-ng/manage-host-ng";
	}

	@RequestMapping(value = RequestMappings.TEAM_MANAGE_HOST, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TeamTO updateHost(@PathVariable("teamKey") final String teamKey,
			@PathVariable("participantKey") final String participantKey, @RequestBody final ChangeTeamHostTO changeTeamHostTO) {
		
		adminValidator.validateNaturalKeys(Arrays.asList(teamKey, participantKey));

		ChangeTeamHost changeTeamHost = toEntity(changeTeamHostTO, participantKey);
		
		Team changedTeam = runningDinnerService.changeSingleTeamHost(changeTeamHost);
		TeamTO changedTeamTO = new TeamTO(changedTeam);
		return changedTeamTO;
	}
	
	
	public static ChangeTeamHost toEntity(final ChangeTeamHostTO changeTeamHostTO, final String participantKey) {
		SingleTeamParticipantChange changedTeamHost = changeTeamHostTO.getChangedTeamHost();
		ChangeTeamHost result = new ChangeTeamHost(changedTeamHost.getTeamKey(), changedTeamHost.getParticipantKey(),
				changeTeamHostTO.getComment(), participantKey, changeTeamHostTO.isSendMailToMe());
		return result;
	}

	
	private void validateParticipantContainedInTeam(final Team team, final String participantKey) {
		Assert.notNull(team, "Team with key " + team.getNaturalKey() + " could not be found!");
		Assert.isTrue(team.isParticipantTeamMember(participantKey), "Modifying Participant must be member of team " + team.getNaturalKey());
	}

	private String showTeamDinnerRoute(final Team team, final Model model, final HttpServletRequest request) {
		List<Team> teamDinnerRoute = TeamRouteBuilder.generateDinnerRoute(team);

		TeamRouteListTO result = new TeamRouteListTO();
		for (Team teamInDinnerRoute : teamDinnerRoute) {
			TeamRouteEntryTO teamRouteEntry = toTeamRouteEntryTO(teamInDinnerRoute, team.getNaturalKey());

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

	private <T> String transformToJson(final T result) {

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
			result.setGeocodes(Collections.<GeocodingResult> emptyList());
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

	@Autowired
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}

}
