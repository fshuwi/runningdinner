package org.runningdinner.ui;

import java.text.DateFormat;
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
import org.runningdinner.core.MealClass;
import org.runningdinner.core.NoPossibleRunningDinnerException;
import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.core.Team;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.RunningDinnerService;
import org.runningdinner.service.email.FormatterUtil;
import org.runningdinner.ui.dto.EditMealTimesModel;
import org.runningdinner.ui.dto.SelectOption;
import org.runningdinner.ui.dto.SendDinnerRoutesModel;
import org.runningdinner.ui.dto.SendTeamArrangementsModel;
import org.runningdinner.ui.dto.SimpleStatusMessage;
import org.runningdinner.ui.json.SingleTeamParticipantChange;
import org.runningdinner.ui.json.StandardJsonResponse;
import org.runningdinner.ui.json.SwitchTeamMembers;
import org.runningdinner.ui.json.SwitchTeamMembersResponse;
import org.runningdinner.ui.json.TeamHostChangeList;
import org.runningdinner.ui.util.MealClassHelper;
import org.runningdinner.ui.util.MealClassPropertyEditor;
import org.runningdinner.ui.validator.AdminValidator;
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

/**
 * Provides all methods for managing a created running dinner.<br>
 * Every method gets the UUID of a running dinner from the request path and loads then the appropriate data (similar to Doodle). Thus we use
 * currently no sessions at all.
 * 
 * @author Clemens Stich
 * 
 */
@Controller
public class AdminController extends AbstractBaseController {

	private MessageSource messages;
	private RunningDinnerService runningDinnerService;

	private AdminValidator adminValidator;

	public static final String SELECT_ALL_TEAMS_PARAMETER = "selectAll";

	private static Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = CoreUtil.getDefaultDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(List.class, "meals", new MealClassPropertyEditor());
	}

	@RequestMapping(value = RequestMappings.ADMIN_OVERVIEW, method = RequestMethod.GET)
	public String adminOverview(@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid, Model model) {
		adminValidator.validateUuid(uuid);

		RunningDinner foundRunningDinner = runningDinnerService.loadDinnerWithBasicDetails(uuid);
		model.addAttribute("runningDinner", foundRunningDinner);
		model.addAttribute("uuid", foundRunningDinner.getUuid()); // Convenience access

		return getFullViewName("overview");
	}

	@RequestMapping(value = RequestMappings.SHOW_TEAMS, method = RequestMethod.GET)
	public String showTeamArrangement(@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid, Model model) {
		adminValidator.validateUuid(uuid);

		// TODO: Method should be revised!

		int numberOfTeamsForDinner = runningDinnerService.loadNumberOfTeamsForDinner(uuid);

		List<Team> regularTeams = null;
		List<Participant> notAssignedParticipants = null;

		// TeamAdministrationModel teamAdminModel = null;
		RunningDinner dinner = runningDinnerService.loadDinnerWithBasicDetails(uuid);

		if (numberOfTeamsForDinner > 0) {
			// Team/Dinner-plan already persisted, fetch it from DB:
			regularTeams = runningDinnerService.loadRegularTeamsWithVisitationPlanFromDinner(uuid);
			notAssignedParticipants = runningDinnerService.loadNotAssignableParticipantsOfDinner(uuid);

			// teamAdminModel = TeamAdministrationModel.fromActivities(dinner.getActivities(), true);
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

			// teamAdminModel = TeamAdministrationModel.fromFirstTeamGeneration(regularTeams.size() > 0);
		}

		model.addAttribute("regularTeams", regularTeams);
		model.addAttribute("notAssignedParticipants", notAssignedParticipants);
		model.addAttribute("uuid", uuid);
		// model.addAttribute("teamAdministration", teamAdminModel);

		return getFullViewName("teams");
	}

	@RequestMapping(value = RequestMappings.SEND_TEAM_MAILS, method = RequestMethod.GET)
	public String showSendTeamArrangementsForm(HttpServletRequest request,
			@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid, Model model, Locale locale) {
		adminValidator.validateUuid(uuid);

		SendTeamArrangementsModel sendTeamsModel = SendTeamArrangementsModel.createWithDefaultMessageTemplate(messages, locale);

		Map<String, String> teamDisplayMap = getTeamsToSelect(uuid);

		sendTeamsModel.setTeamDisplayMap(teamDisplayMap);

		// Select all Teams:
		if (request.getParameter(SELECT_ALL_TEAMS_PARAMETER) != null) {
			sendTeamsModel.setSelectedTeams(new ArrayList<String>(teamDisplayMap.keySet()));
		}

		model.addAttribute("uuid", uuid);
		model.addAttribute("sendTeamsModel", sendTeamsModel);

		return getFullViewName("sendTeamsForm");
	}

	private Map<String, String> getTeamsToSelect(final String uuid) {
		List<Team> regularTeams = runningDinnerService.loadRegularTeamsFromDinner(uuid);
		if (CoreUtil.isEmpty(regularTeams)) {
			throw new RuntimeException("Keine Teams für dinner " + uuid);
		}

		Map<String, String> teamDisplayMap = new LinkedHashMap<String, String>();
		for (Team team : regularTeams) {
			teamDisplayMap.put(team.getNaturalKey(), FormatterUtil.generateTeamLabel(team));
		}
		return teamDisplayMap;
	}

	@RequestMapping(value = RequestMappings.SEND_TEAM_MAILS, method = RequestMethod.POST)
	public String doSendTeamArrangements(HttpServletRequest request, @PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
			@ModelAttribute("sendTeamsModel") SendTeamArrangementsModel sendTeamsModel, BindingResult bindingResult, Model model,
			final RedirectAttributes redirectAttributes, Locale locale) {
		adminValidator.validateUuid(uuid);

		adminValidator.validateSendMessagesModel(sendTeamsModel, bindingResult);
		if (bindingResult.hasErrors()) {
			sendTeamsModel.setTeamDisplayMap(getTeamsToSelect(uuid)); // Reload teams for display
			model.addAttribute("sendTeamsModel", sendTeamsModel);
			model.addAttribute("uuid", uuid);
			return getFullViewName("sendTeamsForm");
		}

		int numTeams = runningDinnerService.sendTeamMessages(uuid, sendTeamsModel.getSelectedTeams(),
				sendTeamsModel.getTeamArrangementMessageFormatter(messages, locale));

		return generateStatusPageRedirect(RequestMappings.SEND_TEAM_MAILS, uuid, redirectAttributes, new SimpleStatusMessage(
				SimpleStatusMessage.SUCCESS_STATUS, "Sent emails for " + numTeams + " teams!"));
	}

	@RequestMapping(value = RequestMappings.SEND_DINNERROUTES_MAIL, method = RequestMethod.GET)
	public String showSendDinnerRoutesForm(HttpServletRequest request, @PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
			Model model, Locale locale) {
		adminValidator.validateUuid(uuid);

		SendDinnerRoutesModel sendDinnerRoutesModel = SendDinnerRoutesModel.createWithDefaultMessageTemplate(messages, locale);

		Map<String, String> teamDisplayMap = getTeamsToSelect(uuid);

		// Select all Teams:
		ArrayList<String> selectedTeams = new ArrayList<String>(teamDisplayMap.keySet());
		sendDinnerRoutesModel.setTeamDisplayMap(teamDisplayMap);
		sendDinnerRoutesModel.setSelectedTeams(selectedTeams);

		model.addAttribute("uuid", uuid);
		model.addAttribute("sendDinnerRoutesModel", sendDinnerRoutesModel);

		return getFullViewName("sendDinnerRoutesForm");
	}

	@RequestMapping(value = RequestMappings.SEND_DINNERROUTES_MAIL, method = RequestMethod.POST)
	public String doSendDinnerRoutes(HttpServletRequest request, @PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
			@ModelAttribute("sendDinnerRoutesModel") SendDinnerRoutesModel sendDinnerRoutesModel, BindingResult bindingResult, Model model,
			final RedirectAttributes redirectAttributes, Locale locale) {
		adminValidator.validateUuid(uuid);

		if (request.getParameter("cancel") != null) {
			return adminOverview(uuid, model);
		}

		adminValidator.validateSendMessagesModel(sendDinnerRoutesModel, bindingResult);
		if (bindingResult.hasErrors()) {
			sendDinnerRoutesModel.setTeamDisplayMap(getTeamsToSelect(uuid)); // Reload teams for display
			model.addAttribute("sendDinnerRoutesModel", sendDinnerRoutesModel);
			model.addAttribute("uuid", uuid);
			return getFullViewName("sendDinnerRoutesForm");
		}

		int numTeams = runningDinnerService.sendDinnerRouteMessages(uuid, sendDinnerRoutesModel.getSelectedTeams(),
				sendDinnerRoutesModel.getDinnerRouteMessageFormatter(messages, Locale.GERMAN));

		return generateStatusPageRedirect(RequestMappings.SEND_DINNERROUTES_MAIL, uuid, redirectAttributes, new SimpleStatusMessage(
				SimpleStatusMessage.SUCCESS_STATUS, "Sent emails for " + numTeams + " teams!"));
	}

	@RequestMapping(value = RequestMappings.SHOW_PARTICIPANTS, method = RequestMethod.GET)
	public String showParticipantsList(@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid, Locale locale,
			HttpServletRequest request, Model model) {
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

	@RequestMapping(value = RequestMappings.EDIT_MEALTIMES, method = RequestMethod.GET)
	public String showMealTimesForm(@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid, HttpServletRequest request,
			Model model) {
		adminValidator.validateUuid(uuid);
		RunningDinner dinner = runningDinnerService.loadDinnerWithBasicDetails(uuid);

		EditMealTimesModel editMealTimesModel = new EditMealTimesModel();
		editMealTimesModel.setMeals(new ArrayList<MealClass>(dinner.getConfiguration().getMealClasses()));

		model.addAttribute("editMealTimesModel", editMealTimesModel);
		model.addAttribute("uuid", uuid);

		return getFullViewName("editMealTimesForm");
	}

	@RequestMapping(value = RequestMappings.EDIT_MEALTIMES, method = RequestMethod.POST)
	public String editMealTimes(@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
			@ModelAttribute("editMealTimesModel") EditMealTimesModel editMealTimesModel, HttpServletRequest request,
			BindingResult bindingResult, Model model, final RedirectAttributes redirectAttributes) {

		adminValidator.validateUuid(uuid);

		if (request.getParameter("cancel") != null) {
			return adminOverview(uuid, model);
		}

		adminValidator.validateMealTimes(editMealTimesModel.getMeals(), bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("editMealTimesModel", editMealTimesModel);
			model.addAttribute("uuid", uuid);
			return getFullViewName("editMealTimesForm");
		}

		RunningDinner dinner = runningDinnerService.loadDinnerWithBasicDetails(uuid);
		Date dateOfDinner = dinner.getDate();
		MealClassHelper.applyDateToMealTimes(editMealTimesModel.getMeals(), dateOfDinner);

		runningDinnerService.updateMealTimes(uuid, new HashSet<MealClass>(editMealTimesModel.getMeals()));

		return generateStatusPageRedirect(RequestMappings.EDIT_MEALTIMES, uuid, redirectAttributes, new SimpleStatusMessage(
				SimpleStatusMessage.SUCCESS_STATUS, "Meal-Times successfully edited!"));
	}

	@RequestMapping(value = RequestMappings.EDIT_PARTICIPANT, method = RequestMethod.GET)
	public String showEditParticipantForm(@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
			@PathVariable("key") String participantKey, Model model) {

		adminValidator.validateNaturalKeys(Arrays.asList(participantKey));

		Participant participant = runningDinnerService.loadParticipant(participantKey);

		model.addAttribute("participant", participant);
		model.addAttribute("uuid", uuid);

		return getFullViewName("editParticipantForm");
	}

	@RequestMapping(value = RequestMappings.EDIT_PARTICIPANT, method = RequestMethod.POST)
	public String editParticipant(@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
			@PathVariable("key") String participantKey, @ModelAttribute("participant") Participant participant, HttpServletRequest request,
			BindingResult bindingResult, Model model, final RedirectAttributes redirectAttributes) {

		adminValidator.validateNaturalKeys(Arrays.asList(participantKey));

		String redirectUrl = RequestMappings.EDIT_PARTICIPANT;
		redirectUrl = redirectUrl.replaceFirst("\\{key\\}", participantKey);
		if (request.getParameter("cancel") != null) {
			return generateStatusPageRedirect(redirectUrl, uuid, redirectAttributes, new SimpleStatusMessage(
					SimpleStatusMessage.INFO_STATUS, "Action cancelled"));
		}

		adminValidator.validateParticipant(participant, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("participant", participant);
			model.addAttribute("uuid", uuid);
			return getFullViewName("editParticipantForm");
		}
		runningDinnerService.updateParticipant(participantKey, participant);

		return generateStatusPageRedirect(redirectUrl, uuid, redirectAttributes, new SimpleStatusMessage(
				SimpleStatusMessage.SUCCESS_STATUS, "Participant successfully edited!"));
	}

	@RequestMapping(value = RequestMappings.EXPORT_TEAMS, method = RequestMethod.GET)
	public String exportTeams(@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid, Model model) {
		adminValidator.validateUuid(uuid);
		throw new UnsupportedOperationException("not yet implemented");
	}

	/**
	 * Used for select-box when editing participant
	 * 
	 * @param locale
	 * @return
	 */
	@ModelAttribute("genders")
	public List<SelectOption> popuplateGenderAspects(Locale locale) {
		List<SelectOption> result = new ArrayList<SelectOption>(3);
		result.add(SelectOption.newGenderOption(Gender.UNDEFINED, messages.getMessage("label.gender.unknown", null, locale)));
		result.add(SelectOption.newGenderOption(Gender.MALE, messages.getMessage("label.gender.male", null, locale)));
		result.add(SelectOption.newGenderOption(Gender.FEMALE, messages.getMessage("label.gender.female", null, locale)));
		return result;
	}

	/**
	 * AJAX request for saving new hosts in passed teams.
	 * 
	 * @param uuid
	 * @param changedHostTeams
	 * @return
	 */
	@RequestMapping(value = RequestMappings.AJAX_SAVE_HOSTS, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public StandardJsonResponse saveTeamHosts(@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
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

	/**
	 * AJAX request for switching one member of a team with another member of another team
	 * 
	 * @param uuid
	 * @param switchTeamMembers
	 * @return
	 */
	@RequestMapping(value = RequestMappings.AJAX_SWITCH_TEAMMEMBERS, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
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

	protected String generateStatusPageRedirect(final String redirectUrl, final String uuid, final RedirectAttributes redirectAttributes,
			final SimpleStatusMessage simpleStatusMessage) {
		redirectAttributes.addFlashAttribute("statusMessage", simpleStatusMessage);
		String theRedirectUrl = "redirect:/" + redirectUrl;
		theRedirectUrl = theRedirectUrl.replaceFirst("\\{" + RequestMappings.ADMIN_URL_UUID_MARKER + "\\}", uuid);
		return theRedirectUrl;
	}

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messages = messageSource;
	}

	@Autowired
	public void setRunningDinnerService(RunningDinnerService runningDinnerService) {
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
	public RunningDinnerService getRunningDinnerService() {
		return runningDinnerService;
	}

}
