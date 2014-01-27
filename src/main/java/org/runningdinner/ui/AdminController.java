package org.runningdinner.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.GeneratedTeamsResult;
import org.runningdinner.core.NoPossibleRunningDinnerException;
import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.core.Team;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.impl.RunningDinnerServiceImpl;
import org.runningdinner.ui.dto.SingleTeamHostChange.TeamHostChangeList;
import org.runningdinner.ui.dto.TeamAdministrationModel;
import org.runningdinner.ui.dto.TeamHostsChangeResponse;
import org.runningdinner.ui.validator.AdminValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AdminController extends AbstractBaseController {

	public static final String ADMIN_URL_UUID_MARKER = "uuid";
	public static final String ADMIN_URL_PATTERN = "/event/{" + ADMIN_URL_UUID_MARKER + "}/admin";

	private MessageSource messages;
	private RunningDinnerServiceImpl runningDinnerService;
	private AdminValidator adminValidator;

	private static Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

	@RequestMapping(value = ADMIN_URL_PATTERN, method = RequestMethod.GET)
	public String adminOverview(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid, Model model) {
		adminValidator.validateUuid(uuid);

		RunningDinner foundRunningDinner = runningDinnerService.loadDinnerWithBasicDetails(uuid);
		model.addAttribute("runningDinner", foundRunningDinner);
		model.addAttribute("uuid", foundRunningDinner.getUuid()); // Convenience access

		return getFullViewName("overview");
	}

	@RequestMapping(value = ADMIN_URL_PATTERN + "/teams", method = RequestMethod.GET)
	public String showTeamArrangement(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid, Model model) {
		adminValidator.validateUuid(uuid);

		int numberOfTeamsForDinner = runningDinnerService.loadNumberOfTeamsForDinner(uuid);

		List<Team> regularTeams = null;
		List<Participant> notAssignedParticipants = null;

		TeamAdministrationModel teamAdminModel = null;

		if (numberOfTeamsForDinner > 0) {
			// Team/Dinner-plan already persisted, fetch it from DB:
			regularTeams = runningDinnerService.loadRegularTeamsWithVisitationPlanFromDinner(uuid);
			notAssignedParticipants = runningDinnerService.loadNotAssignableParticipantsOfDinner(uuid);

			RunningDinner dinner = runningDinnerService.loadDinnerWithBasicDetails(uuid);
			teamAdminModel = TeamAdministrationModel.fromActivities(dinner.getActivities(), true);
		}
		else {
			// Team/Dinner-plan not yet persisted, generate new one and persist it to DB:
			try {
				GeneratedTeamsResult generatedTeamsResult = runningDinnerService.createTeamAndVisitationPlans(uuid);
				regularTeams = generatedTeamsResult.getRegularTeams();
				notAssignedParticipants = generatedTeamsResult.getNotAssignedParticipants();
			}
			catch (NoPossibleRunningDinnerException ex) {
				LOGGER.warn("Could not create team assignments for dinner {} due too few participants", uuid, ex);
				regularTeams = Collections.emptyList();
				notAssignedParticipants = runningDinnerService.loadAllParticipantsOfDinner(uuid);
			}

			teamAdminModel = TeamAdministrationModel.fromFirstTeamGeneration(regularTeams.size() > 0);
		}

		model.addAttribute("regularTeams", regularTeams);
		model.addAttribute("notAssignedParticipants", notAssignedParticipants);
		model.addAttribute("uuid", uuid);
		model.addAttribute("teamAdministration", teamAdminModel);

		return getFullViewName("teams");
	}

	@RequestMapping(value = ADMIN_URL_PATTERN + "/teams/save", method = RequestMethod.GET)
	public String showSaveTeamsAndSendMailForm(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid, Model model) {
		adminValidator.validateUuid(uuid);

		RunningDinner dinner = runningDinnerService.loadDinnerWithBasicDetails(uuid);
		int numberOfTeamsForDinner = runningDinnerService.loadNumberOfTeamsForDinner(uuid);

		TeamAdministrationModel teamAdminModel = TeamAdministrationModel.fromActivities(dinner.getActivities(), numberOfTeamsForDinner > 0);

		model.addAttribute("uuid", uuid);
		model.addAttribute("teamAdministration", teamAdminModel);

		return getFullViewName("finalizeTeamsForm");
	}

	@RequestMapping(value = ADMIN_URL_PATTERN + "/teams/save", method = RequestMethod.POST)
	public String doFinalizeTeams(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid, Model model) {
		adminValidator.validateUuid(uuid);

		model.addAttribute("uuid", uuid);

		return getFullViewName("finalizeTeamsForm");
	}

	@RequestMapping(value = ADMIN_URL_PATTERN + "/participants", method = RequestMethod.GET)
	public String showParticipantsList(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid, Locale locale, HttpServletRequest request,
			Model model) {
		adminValidator.validateUuid(uuid);

		RunningDinner dinner = runningDinnerService.loadDinnerWithParticipants(uuid);

		List<Participant> participants = dinner.getParticipants();
		RunningDinnerConfig configuration = dinner.getConfiguration();

		// List<Participant> loadNotAssignableParticipantsOfDinner = runningDinnerService.loadNotAssignableParticipantsOfDinner(uuid);
		// Normally we would also execute above query for this in database, but it's faster for computing this information in-memory as it
		// is done in setParticpantListViewAttributes:
		setParticipantListViewAttributes(request, participants, configuration, locale);

		return getFullViewName("participants");
	}

	/**
	 * AJAX request for saving new hosts in passed teams.
	 * 
	 * @param uuid
	 * @param changedHostTeams
	 * @return
	 */
	@RequestMapping(value = ADMIN_URL_PATTERN + "/teams/savehosts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TeamHostsChangeResponse saveTeamHosts(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid,
			@RequestBody TeamHostChangeList changedHostTeams) {
		adminValidator.validateUuid(uuid);

		if (CoreUtil.isEmpty(changedHostTeams)) {
			return TeamHostsChangeResponse.createSuccessResponse();
		}

		Map<String, String> teamHostMappings = TeamHostChangeList.generateTeamHostsMap(changedHostTeams);

		try {
			runningDinnerService.updateTeamHosters(uuid, teamHostMappings);
		}
		catch (Exception ex) {
			LOGGER.error("Failed to update team hosters for dinner {} with number of passed teamHostMappings {}", uuid,
					teamHostMappings.size(), ex);
			return TeamHostsChangeResponse.createErrorResponse(ex.getMessage());
		}

		return TeamHostsChangeResponse.createSuccessResponse();
	}

	@RequestMapping(value = ADMIN_URL_PATTERN + "/teams/switchmembers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public void switchTeamMembers(@PathVariable(ADMIN_URL_PATTERN) String uuid, @RequestBody String[] teamMembers) {
		adminValidator.validateUuid(uuid);

		if (teamMembers == null || teamMembers.length != 2) {
			// TODO: Throw exception!
		}

		adminValidator.validateNaturalKeys(Arrays.asList(teamMembers));

		List<Team> result = runningDinnerService.switchTeamMembers(uuid, teamMembers[0], teamMembers[1]);

	}

	protected String getFullViewName(final String viewName) {
		return "admin/" + viewName;
	}

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messages = messageSource;
	}

	@Autowired
	public void setRunningDinnerService(RunningDinnerServiceImpl runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

	@Autowired
	public void setAdminValidator(AdminValidator adminValidator) {
		this.adminValidator = adminValidator;
	}

	@Override
	protected MessageSource getMessageSource() {
		return this.messages;
	}

	@Override
	public RunningDinnerServiceImpl getRunningDinnerService() {
		return runningDinnerService;
	}

}
