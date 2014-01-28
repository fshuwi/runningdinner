package org.runningdinner.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.Gender;
import org.runningdinner.core.GeneratedTeamsResult;
import org.runningdinner.core.NoPossibleRunningDinnerException;
import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.core.Team;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.impl.RunningDinnerServiceImpl;
import org.runningdinner.ui.dto.EditMealTimesModel;
import org.runningdinner.ui.dto.FinalizeTeamsModel;
import org.runningdinner.ui.dto.GenderOption;
import org.runningdinner.ui.dto.SimpleStatusMessage;
import org.runningdinner.ui.dto.SingleTeamParticipantChange;
import org.runningdinner.ui.dto.SingleTeamParticipantChange.SwitchTeamMembers;
import org.runningdinner.ui.dto.SingleTeamParticipantChange.TeamHostChangeList;
import org.runningdinner.ui.dto.StandardJsonResponse;
import org.runningdinner.ui.dto.SwitchTeamMembersResponse;
import org.runningdinner.ui.dto.TeamAdministrationModel;
import org.runningdinner.ui.util.MealClassHelper;
import org.runningdinner.ui.util.MealClassPropertyEditor;
import org.runningdinner.ui.validator.AdminValidator;
import org.runningdinner.ui.validator.CommonValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController extends AbstractBaseController {

	public static final String ADMIN_URL_UUID_MARKER = "uuid";
	public static final String ADMIN_URL_PATTERN = "/event/{" + ADMIN_URL_UUID_MARKER + "}/admin";

	private MessageSource messages;
	private RunningDinnerServiceImpl runningDinnerService;

	private AdminValidator adminValidator;
	private CommonValidator commonValidator;

	private static Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Set.class, "meals", new MealClassPropertyEditor());
	}

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

	// TODO: Alles besser machen

	@RequestMapping(value = ADMIN_URL_PATTERN + "/teams/mail", method = RequestMethod.GET)
	public String showSaveTeamsAndSendMailForm(HttpServletRequest request, @PathVariable(ADMIN_URL_UUID_MARKER) String uuid, Model model) {
		adminValidator.validateUuid(uuid);

		FinalizeTeamsModel finalizeTeamsModel = FinalizeTeamsModel.createWithDefaultMessageTemplate();

		Map<String, String> teamDisplayMap = getTeamsForSelection(uuid);

		finalizeTeamsModel.setTeamDisplayMap(teamDisplayMap);

		// Select all Teams:
		if (request.getParameter("fromAdminMenu") == null) {
			finalizeTeamsModel.setSelectedTeams(new ArrayList<String>(teamDisplayMap.keySet()));
		}

		model.addAttribute("uuid", uuid);
		model.addAttribute("finalizeTeamsModel", finalizeTeamsModel);

		return getFullViewName("finalizeTeamsForm");
	}

	private Map<String, String> getTeamsForSelection(final String uuid) {
		List<Team> regularTeams = runningDinnerService.loadRegularTeamsFromDinner(uuid);
		if (CoreUtil.isEmpty(regularTeams)) {
			throw new RuntimeException("Keine Teams für dinner " + uuid);
		}

		Map<String, String> teamDisplayMap = new LinkedHashMap<String, String>();
		for (Team team : regularTeams) {
			teamDisplayMap.put(team.getNaturalKey(), generateTeamLabel(team));
		}
		return teamDisplayMap;
	}

	private String generateTeamLabel(Team team) {
		String result = "Team " + team.getTeamNumber();
		Set<Participant> teamMembers = team.getTeamMembers();
		if (CoreUtil.isEmpty(teamMembers)) {
			return result;
		}
		result += " (";
		int cnt = 0;
		for (Participant p : team.getTeamMembers()) {
			if (cnt++ > 0) {
				result += ", ";
			}
			result += p.getName().getFullnameFirstnameFirst();
		}
		result += ")";
		return result;
	}

	@RequestMapping(value = ADMIN_URL_PATTERN + "/teams/mail", method = RequestMethod.POST)
	public String doFinalizeTeams(HttpServletRequest request, @PathVariable(ADMIN_URL_UUID_MARKER) String uuid,
			@ModelAttribute("finalizeTeamsModel") FinalizeTeamsModel finalizeTeamsModel, BindingResult bindingResult, Model model,
			final RedirectAttributes redirectAttributes) {
		adminValidator.validateUuid(uuid);

		adminValidator.validateFinalizeTeamsModel(finalizeTeamsModel, bindingResult);
		if (bindingResult.hasErrors()) {
			finalizeTeamsModel.setTeamDisplayMap(getTeamsForSelection(uuid)); // Reload teams for display
			model.addAttribute("finalizeTeamsModel", finalizeTeamsModel);
			model.addAttribute("uuid", uuid);
			return getFullViewName("finalizeTeamsForm");
		}

		finalizeTeamsModel.setSendMessages(true); // TODO: Remove

		int numTeams = runningDinnerService.sendTeamMessages(uuid, finalizeTeamsModel);
		return generateStatusPageRedirect(uuid, redirectAttributes, new SimpleStatusMessage(SimpleStatusMessage.SUCCESS_STATUS,
				"Sent emails for " + numTeams + " teams!"));

	}

	@RequestMapping(value = ADMIN_URL_PATTERN + "/statuspage", method = RequestMethod.GET)
	public String showStatusPage(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid,
			@ModelAttribute("statusMessage") SimpleStatusMessage simpleStatusMessage, Model model) {
		model.addAttribute("uuid", uuid);
		return getFullViewName("statuspage");
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

	@RequestMapping(value = ADMIN_URL_PATTERN + "/mealtimes", method = RequestMethod.GET)
	public String showMealTimesForm(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid, HttpServletRequest request, Model model) {
		adminValidator.validateUuid(uuid);
		RunningDinner dinner = runningDinnerService.loadDinnerWithBasicDetails(uuid);

		EditMealTimesModel editMealTimesModel = new EditMealTimesModel();
		editMealTimesModel.setMeals(dinner.getConfiguration().getMealClasses());

		model.addAttribute("editMealTimesModel", editMealTimesModel);
		model.addAttribute("uuid", uuid);

		return getFullViewName("editMealTimesForm");
	}

	@RequestMapping(value = ADMIN_URL_PATTERN + "/mealtimes", method = RequestMethod.POST)
	public String editMealTimes(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid,
			@ModelAttribute("editMealTimesModel") EditMealTimesModel editMealTimesModel, HttpServletRequest request,
			BindingResult bindingResult, Model model, final RedirectAttributes redirectAttributes) {

		adminValidator.validateUuid(uuid);

		if (request.getParameter("cancel") != null) {
			return generateStatusPageRedirect(uuid, redirectAttributes, new SimpleStatusMessage(SimpleStatusMessage.INFO_STATUS,
					"Action cancelled"));
		}

		commonValidator.validateMealTimes(editMealTimesModel.getMeals(), bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("editMealTimesModel", editMealTimesModel);
			model.addAttribute("uuid", uuid);
			return getFullViewName("finalizeTeamsForm");
		}

		RunningDinner dinner = runningDinnerService.loadDinnerWithBasicDetails(uuid);
		Date dateOfDinner = dinner.getDate();
		MealClassHelper.applyDateToMealTimes(editMealTimesModel.getMeals(), dateOfDinner);

		runningDinnerService.updateMealTimes(uuid, editMealTimesModel.getMeals());

		return generateStatusPageRedirect(uuid, redirectAttributes, new SimpleStatusMessage(SimpleStatusMessage.SUCCESS_STATUS,
				"Meal-Times successfully edited!"));
	}

	@RequestMapping(value = ADMIN_URL_PATTERN + "/participant/{key}/edit", method = RequestMethod.GET)
	public String showEditParticipantForm(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid, @PathVariable("key") String participantKey,
			Model model) {

		adminValidator.validateNaturalKeys(Arrays.asList(participantKey));

		Participant participant = runningDinnerService.loadParticipant(participantKey);

		model.addAttribute("participant", participant);
		model.addAttribute("uuid", uuid);

		return getFullViewName("editParticipantForm");
	}

	@RequestMapping(value = ADMIN_URL_PATTERN + "/participant/{key}/edit", method = RequestMethod.POST)
	public String editParticipant(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid, @PathVariable("key") String participantKey,
			@ModelAttribute("participant") Participant participant, HttpServletRequest request, BindingResult bindingResult, Model model,
			final RedirectAttributes redirectAttributes) {

		adminValidator.validateNaturalKeys(Arrays.asList(participantKey));

		if (request.getParameter("cancel") != null) {
			return generateStatusPageRedirect(uuid, redirectAttributes, new SimpleStatusMessage(SimpleStatusMessage.INFO_STATUS,
					"Action cancelled"));
		}

		commonValidator.validateParticipant(participant, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("participant", participant);
			model.addAttribute("uuid", uuid);
			return getFullViewName("editParticipantForm");
		}
		runningDinnerService.updateParticipant(participantKey, participant);

		return generateStatusPageRedirect(uuid, redirectAttributes, new SimpleStatusMessage(SimpleStatusMessage.SUCCESS_STATUS,
				"Participant successfully edited!"));
	}

	/**
	 * Used for select-box when editing participant
	 * 
	 * @param locale
	 * @return
	 */
	@ModelAttribute("genders")
	public List<GenderOption> popuplateGenderAspects(Locale locale) {
		List<GenderOption> result = new ArrayList<GenderOption>(3);
		result.add(new GenderOption(Gender.UNDEFINED, "Unbekannt"));
		result.add(new GenderOption(Gender.MALE, "männlich"));
		result.add(new GenderOption(Gender.FEMALE, "weiblich"));
		return result;
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
	public StandardJsonResponse saveTeamHosts(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid,
			@RequestBody TeamHostChangeList changedHostTeams) {
		adminValidator.validateUuid(uuid);

		if (CoreUtil.isEmpty(changedHostTeams)) {
			return StandardJsonResponse.createSuccessResponse();
		}

		Map<String, String> teamHostMappings = TeamHostChangeList.generateTeamHostsMap(changedHostTeams);

		try {
			runningDinnerService.updateTeamHosters(uuid, teamHostMappings);
		}
		catch (Exception ex) {
			LOGGER.error("Failed to update team hosters for dinner {} with number of passed teamHostMappings {}", uuid,
					teamHostMappings.size(), ex);
			return StandardJsonResponse.createErrorResponse(ex.getMessage());
		}

		return StandardJsonResponse.createSuccessResponse();
	}

	@RequestMapping(value = "/event/{uuid}/admin/teams/switchmembers", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public SwitchTeamMembersResponse switchTeamMembers(@PathVariable("uuid") String uuid, @RequestBody SwitchTeamMembers switchTeamMembers) {
		adminValidator.validateUuid(uuid);

		if (switchTeamMembers == null || switchTeamMembers.size() != 2) {
			return SwitchTeamMembersResponse.createErrorResponse("Expected size of participants with 2, but was "
					+ (switchTeamMembers == null ? "empty" : switchTeamMembers.size()));
		}

		Set<String> naturalKeysTmp = new HashSet<String>();
		for (SingleTeamParticipantChange teamParticipantChange : switchTeamMembers) {
			naturalKeysTmp.add(teamParticipantChange.getParticipantKey());
		}

		try {
			adminValidator.validateNaturalKeys(naturalKeysTmp);

			List<Team> result = runningDinnerService.switchTeamMembers(uuid, switchTeamMembers.get(0).getParticipantKey(),
					switchTeamMembers.get(1).getParticipantKey());
			SwitchTeamMembersResponse response = SwitchTeamMembersResponse.createSuccessResponse(result);
			return response;
		}
		catch (Exception ex) {
			LOGGER.error("Failed to switch teams", ex);
			return SwitchTeamMembersResponse.createErrorResponse("Could not switch team members. Please try again or contact administrator");
		}
	}

	protected String getFullViewName(final String viewName) {
		return "admin/" + viewName;
	}

	protected String generateStatusPageRedirect(String uuid, RedirectAttributes redirectAttributes, SimpleStatusMessage simpleStatusMessage) {
		redirectAttributes.addFlashAttribute("statusMessage", simpleStatusMessage);
		String redirectUrl = "redirect:/" + ADMIN_URL_PATTERN + "/statuspage";
		redirectUrl = redirectUrl.replaceFirst("\\{" + ADMIN_URL_UUID_MARKER + "\\}", uuid);
		return redirectUrl;
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

	@Autowired
	public void setCommonValidator(CommonValidator commonValidator) {
		this.commonValidator = commonValidator;
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
