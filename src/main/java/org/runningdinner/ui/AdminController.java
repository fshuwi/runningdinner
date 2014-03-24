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
import org.runningdinner.model.BaseMailReport;
import org.runningdinner.model.ParticipantMailReport;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.RunningDinnerService;
import org.runningdinner.service.TeamRouteBuilder;
import org.runningdinner.service.email.DinnerRouteMessageFormatter;
import org.runningdinner.service.email.FormatterUtil;
import org.runningdinner.service.email.ParticipantMessageFormatter;
import org.runningdinner.service.email.TeamArrangementMessageFormatter;
import org.runningdinner.ui.dto.BaseSendMailsModel;
import org.runningdinner.ui.dto.EditMealTimesModel;
import org.runningdinner.ui.dto.SelectOption;
import org.runningdinner.ui.dto.SendDinnerRoutesModel;
import org.runningdinner.ui.dto.SendMailsPreviewModel;
import org.runningdinner.ui.dto.SendTeamArrangementsModel;
import org.runningdinner.ui.dto.SimpleStatusMessage;
import org.runningdinner.ui.json.SaveTeamHostsResponse;
import org.runningdinner.ui.json.SingleTeamParticipantChange;
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

		final int numberOfTeamsForDinner = runningDinnerService.loadNumberOfTeamsForDinner(uuid);

		List<Team> regularTeams = null;
		List<Participant> notAssignedParticipants = null;

		if (numberOfTeamsForDinner > 0) {
			// Team/Dinner-plan already persisted, fetch it from DB:
			regularTeams = runningDinnerService.loadRegularTeamsWithVisitationPlanFromDinner(uuid);
			notAssignedParticipants = runningDinnerService.loadNotAssignableParticipantsOfDinner(uuid);
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
		}

		model.addAttribute("regularTeams", regularTeams);
		model.addAttribute("notAssignedParticipants", notAssignedParticipants);
		model.addAttribute("uuid", uuid);

		return getFullViewName("teams");
	}

	@RequestMapping(value = RequestMappings.SEND_TEAM_MAILS, method = RequestMethod.GET)
	public String showSendTeamArrangementsForm(HttpServletRequest request,
			@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid, Model model, RedirectAttributes redirectAttributes,
			Locale locale) {
		adminValidator.validateUuid(uuid);

		SendTeamArrangementsModel sendTeamsModel = SendTeamArrangementsModel.createWithDefaultMessageTemplate(messages, locale);

		bindCommonMailAttributesAndLoadTeamDisplayMap(model, sendTeamsModel, uuid, runningDinnerService.findLastTeamMailReport(uuid));
		Map<String, String> teamDisplayMap = sendTeamsModel.getEntityDisplayMap();

		if (teamDisplayMap.size() == 0) {
			LOGGER.warn("Tried to call send team mails for dinner {} without any existing teams", uuid);
			return generateStatusPageRedirect(RequestMappings.ADMIN_OVERVIEW, uuid, redirectAttributes, new SimpleStatusMessage(
					SimpleStatusMessage.WARN_STATUS, messages.getMessage("error.no.teams", null, locale)));
		}

		// Select all Teams:
		if (request.getParameter(RequestMappings.SELECT_ALL_TEAMS_PARAMETER) != null) {
			sendTeamsModel.setSelectedEntities(new ArrayList<String>(teamDisplayMap.keySet()));
		}

		return getFullViewName("sendTeamsForm");
	}

	/**
	 * Retrieves a map which contains every team of a running dinner as map-key (backed by the naturalKey of a team). The map-value
	 * represents a human readable label for the team used for display.
	 * 
	 * @param uuid
	 * @param throwExceptionOnNoTeams If set to true, then an exception will be thrown if no teams exist yet (they need maybe first
	 *            generated). Otherwise the method returns just an empty map.
	 * @return
	 */
	private Map<String, String> getTeamsToSelect(final String uuid, boolean throwExceptionOnNoTeams) {
		List<Team> regularTeams = runningDinnerService.loadRegularTeamsFromDinner(uuid);
		if (CoreUtil.isEmpty(regularTeams)) {
			if (throwExceptionOnNoTeams) {
				throw new IllegalStateException("No teams for dinner " + uuid);
			}
			else {
				return Collections.emptyMap();
			}
		}

		Map<String, String> teamDisplayMap = new LinkedHashMap<String, String>();
		for (Team team : regularTeams) {
			teamDisplayMap.put(team.getNaturalKey(), FormatterUtil.generateTeamLabel(team));
		}
		return teamDisplayMap;
	}

	@RequestMapping(value = RequestMappings.SEND_TEAM_MAILS, method = RequestMethod.POST)
	public String doSendTeamArrangements(HttpServletRequest request, @PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
			@ModelAttribute("sendMailsModel") SendTeamArrangementsModel sendTeamsModel, BindingResult bindingResult, Model model,
			final RedirectAttributes redirectAttributes, Locale locale) {

		adminValidator.validateUuid(uuid);

		adminValidator.validateSendMessagesModel(sendTeamsModel, bindingResult);
		if (bindingResult.hasErrors()) {
			bindCommonMailAttributesAndLoadTeamDisplayMap(model, sendTeamsModel, uuid, runningDinnerService.findLastTeamMailReport(uuid));
			return getFullViewName("sendTeamsForm");
		}

		if (request.getParameter("preview") != null) {
			return doSendTeamArrangementsPreview(uuid, sendTeamsModel, model, locale);
		}

		int numTeams = runningDinnerService.sendTeamMessages(uuid, sendTeamsModel.getSelectedEntities(),
				sendTeamsModel.getTeamArrangementMessageFormatter(messages, locale));

		String messageText = messages.getMessage("text.sendmessage.notification.teams", new Object[] { numTeams }, locale);
		return generateStatusPageRedirect(RequestMappings.SEND_TEAM_MAILS, uuid, redirectAttributes, new SimpleStatusMessage(
				SimpleStatusMessage.SUCCESS_STATUS, messageText));
	}

	/**
	 * Sets up the data for showing a preview for sending team arrangement mails. This method shows the same form again from which it was
	 * called.
	 * 
	 * @param request
	 * @param uuid
	 * @param sendTeamsModel
	 * @param bindingResult
	 * @param model
	 * @param redirectAttributes
	 * @param locale
	 * @return
	 */
	protected String doSendTeamArrangementsPreview(String uuid, SendTeamArrangementsModel sendTeamsModel, Model model, Locale locale) {

		// Construct preview object
		SendMailsPreviewModel sendMailsPreviewModel = createSendMailsPreviewModel(sendTeamsModel, uuid, false);
		Team firstTeam = sendMailsPreviewModel.getTeam();

		// ... and add formatted messages to it:
		TeamArrangementMessageFormatter formatter = sendTeamsModel.getTeamArrangementMessageFormatter(messages, locale);
		Set<Participant> teamMembers = firstTeam.getTeamMembers();
		for (Participant teamMember : teamMembers) {
			String message = formatter.formatTeamMemberMessage(teamMember, firstTeam);
			sendMailsPreviewModel.addMessage(formatter.getHtmlFormattedMessage(message));
		}

		model.addAttribute("sendMailsPreviewModel", sendMailsPreviewModel);

		// Add model attributes (form binding) to be displayed again:
		bindCommonMailAttributesAndLoadTeamDisplayMap(model, sendTeamsModel, uuid, runningDinnerService.findLastTeamMailReport(uuid));

		return getFullViewName("sendTeamsForm");
	}

	@RequestMapping(value = RequestMappings.SEND_DINNERROUTES_MAIL, method = RequestMethod.GET)
	public String showSendDinnerRoutesForm(HttpServletRequest request, @PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
			Model model, RedirectAttributes redirectAttributes, Locale locale) {
		adminValidator.validateUuid(uuid);

		SendDinnerRoutesModel sendDinnerRoutesModel = SendDinnerRoutesModel.createWithDefaultMessageTemplate(messages, locale);

		bindCommonMailAttributesAndLoadTeamDisplayMap(model, sendDinnerRoutesModel, uuid,
				runningDinnerService.findLastDinnerRouteMailReport(uuid));

		Map<String, String> teamDisplayMap = sendDinnerRoutesModel.getEntityDisplayMap();
		if (teamDisplayMap.size() == 0) {
			LOGGER.warn("Tried to call send dinner route mails for dinner {} without any existing teams", uuid);
			return generateStatusPageRedirect(RequestMappings.ADMIN_OVERVIEW, uuid, redirectAttributes, new SimpleStatusMessage(
					SimpleStatusMessage.WARN_STATUS, messages.getMessage("error.no.teams", null, locale)));
		}

		// Select all Teams:
		sendDinnerRoutesModel.setSelectedEntities(new ArrayList<String>(teamDisplayMap.keySet()));

		return getFullViewName("sendDinnerRoutesForm");
	}

	@RequestMapping(value = RequestMappings.SEND_DINNERROUTES_MAIL, method = RequestMethod.POST)
	public String doSendDinnerRoutes(HttpServletRequest request, @PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
			@ModelAttribute("sendMailsModel") SendDinnerRoutesModel sendDinnerRoutesModel, BindingResult bindingResult, Model model,
			final RedirectAttributes redirectAttributes, Locale locale) {

		adminValidator.validateUuid(uuid);

		adminValidator.validateSendMessagesModel(sendDinnerRoutesModel, bindingResult);
		if (bindingResult.hasErrors()) {
			bindCommonMailAttributesAndLoadTeamDisplayMap(model, sendDinnerRoutesModel, uuid,
					runningDinnerService.findLastDinnerRouteMailReport(uuid));
			return getFullViewName("sendDinnerRoutesForm");
		}

		if (request.getParameter("preview") != null) {
			return doSendDinnerRoutesPreview(uuid, sendDinnerRoutesModel, model, locale);
		}

		int numTeams = runningDinnerService.sendDinnerRouteMessages(uuid, sendDinnerRoutesModel.getSelectedEntities(),
				sendDinnerRoutesModel.getDinnerRouteMessageFormatter(messages, Locale.GERMAN));

		String messageText = messages.getMessage("text.sendmessage.notification.teams", new Object[] { numTeams }, locale);
		return generateStatusPageRedirect(RequestMappings.SEND_DINNERROUTES_MAIL, uuid, redirectAttributes, new SimpleStatusMessage(
				SimpleStatusMessage.SUCCESS_STATUS, messageText));
	}

	protected String doSendDinnerRoutesPreview(String uuid, SendDinnerRoutesModel sendDinnerRoutesModel, Model model, Locale locale) {

		// Construct preview object ...
		SendMailsPreviewModel sendMailsPreviewModel = createSendMailsPreviewModel(sendDinnerRoutesModel, uuid, true);

		// ... and add formatted messages to it:
		DinnerRouteMessageFormatter formatter = sendDinnerRoutesModel.getDinnerRouteMessageFormatter(messages, locale);

		Team firstTeam = sendMailsPreviewModel.getTeam();

		Set<Participant> teamMembers = firstTeam.getTeamMembers();
		List<Team> dinnerRoute = TeamRouteBuilder.generateDinnerRoute(firstTeam);
		for (Participant teamMember : teamMembers) {
			String message = formatter.formatDinnerRouteMessage(teamMember, firstTeam, dinnerRoute);
			sendMailsPreviewModel.addMessage(formatter.getHtmlFormattedMessage(message));
		}

		model.addAttribute("sendMailsPreviewModel", sendMailsPreviewModel);

		// Add model attributes (form binding) to be displayed again:
		bindCommonMailAttributesAndLoadTeamDisplayMap(model, sendDinnerRoutesModel, uuid,
				runningDinnerService.findLastDinnerRouteMailReport(uuid));

		return getFullViewName("sendDinnerRoutesForm");
	}

	/**
	 * Instantiates a new object for handling previews in JSP-views. Main purpose of this method is to pick the first selected team (which
	 * is passed by the sendMailsModel) and construct the preview model out of it
	 * 
	 * @param sendMailsModel
	 * @param uuid
	 * @return
	 */
	protected SendMailsPreviewModel createSendMailsPreviewModel(final BaseSendMailsModel sendMailsModel, final String uuid,
			boolean fetchVisitationPlan) {
		// Load first team of selection:
		List<String> selectedTeamKeys = sendMailsModel.getSelectedEntities();
		String firstTeamKey = selectedTeamKeys.iterator().next();

		Team firstTeam = null;
		if (fetchVisitationPlan) {
			firstTeam = runningDinnerService.loadSingleTeamWithVisitationPlan(firstTeamKey);
		}
		else {
			List<Team> selectedTeams = runningDinnerService.loadTeamsFromDinnerByKeys(new HashSet<String>(Arrays.asList(firstTeamKey)),
					uuid);
			firstTeam = selectedTeams.iterator().next();
		}

		// And construct preview object:
		SendMailsPreviewModel sendMailsPreviewModel = new SendMailsPreviewModel(firstTeam);
		sendMailsPreviewModel.setSubject(sendMailsModel.getSubject());
		sendMailsPreviewModel.setParticipantNames(FormatterUtil.generateParticipantNamesWithCommas(firstTeam));

		return sendMailsPreviewModel;
	}

	/**
	 * Helper method that can be used from different mail sending views to add common attributes to the model (like dinner-uuid, mail-model,
	 * ...)
	 * 
	 * @param model
	 * @param sendMailsModel
	 * @param uuid
	 * @param lastMailReport
	 */
	protected void bindCommonMailAttributesAndLoadTeamDisplayMap(Model model, BaseSendMailsModel sendMailsModel, final String uuid,
			BaseMailReport lastMailReport) {
		sendMailsModel.setEntityDisplayMap(getTeamsToSelect(uuid, false)); // Reload teams for display
		sendMailsModel.setLastMailReport(lastMailReport);
		model.addAttribute("sendMailsModel", sendMailsModel);
		model.addAttribute("uuid", uuid);
	}

	@RequestMapping(value = RequestMappings.SEND_PARTICIPANT_MAILS, method = RequestMethod.GET)
	public String showSendParticipantsForm(HttpServletRequest request, @PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
			Model model, RedirectAttributes redirectAttributes, Locale locale) {
		adminValidator.validateUuid(uuid);

		BaseSendMailsModel sendMailsModel = new BaseSendMailsModel();
		sendMailsModel.setMessage(messages.getMessage("message.template.participants", null, locale));

		bindAndSetupParticipantMailAttributes(model, sendMailsModel, uuid, locale);

		Map<String, String> participantDisplayMap = sendMailsModel.getEntityDisplayMap();
		if (participantDisplayMap.size() == 0) {
			LOGGER.warn("Tried to call send participant mails for dinner {} without any existing participants", uuid);
			return generateStatusPageRedirect(RequestMappings.ADMIN_OVERVIEW, uuid, redirectAttributes, new SimpleStatusMessage(
					SimpleStatusMessage.WARN_STATUS, messages.getMessage("error.no.participants", null, locale)));
		}

		// Select all participants:
		sendMailsModel.setSelectedEntities(new ArrayList<String>(participantDisplayMap.keySet()));

		return getFullViewName("sendParticipantsForm");
	}

	@RequestMapping(value = RequestMappings.SEND_PARTICIPANT_MAILS, method = RequestMethod.POST)
	public String doSendParticipants(HttpServletRequest request, @PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
			@ModelAttribute("sendMailsModel") BaseSendMailsModel sendMailsModel, BindingResult bindingResult, Model model,
			final RedirectAttributes redirectAttributes, Locale locale) {

		adminValidator.validateUuid(uuid);

		adminValidator.validateSendMessagesModel(sendMailsModel, bindingResult);
		if (bindingResult.hasErrors()) {
			bindAndSetupParticipantMailAttributes(model, sendMailsModel, uuid, locale);
			return getFullViewName("sendParticipantsForm");
		}

		// Create Formatter...
		ParticipantMessageFormatter messageFormatter = new ParticipantMessageFormatter(messages, locale);
		messageFormatter.setMessageTemplate(sendMailsModel.getMessage());
		messageFormatter.setSubject(sendMailsModel.getSubject());

		if (request.getParameter("preview") != null) {
			return doSendParticipantsPreview(uuid, sendMailsModel, messageFormatter, model, locale);
		}

		// ... And send mails (if not ran into preview above):
		int numParticipants = runningDinnerService.sendParticipantMessages(uuid, sendMailsModel.getSelectedEntities(), messageFormatter);

		String messageText = messages.getMessage("text.sendmessage.notification.participants", new Object[] { numParticipants }, locale);
		return generateStatusPageRedirect(RequestMappings.SEND_PARTICIPANT_MAILS, uuid, redirectAttributes, new SimpleStatusMessage(
				SimpleStatusMessage.SUCCESS_STATUS, messageText));
	}

	protected String doSendParticipantsPreview(String uuid, BaseSendMailsModel sendMailsModel,
			ParticipantMessageFormatter messageFormatter, Model model, Locale locale) {

		List<String> selectedParticipantKeys = sendMailsModel.getSelectedEntities();
		String firstParticipantKey = selectedParticipantKeys.iterator().next();
		final Participant firstParticipant = runningDinnerService.loadParticipant(firstParticipantKey);

		// Construct preview object ...
		SendMailsPreviewModel sendMailsPreviewModel = new SendMailsPreviewModel();
		sendMailsPreviewModel.setSubject(sendMailsModel.getSubject());
		sendMailsPreviewModel.setParticipantNames(firstParticipant.getName().getFullnameFirstnameFirst());

		String message = messageFormatter.formatParticipantMessage(firstParticipant);
		sendMailsPreviewModel.addMessage(messageFormatter.getHtmlFormattedMessage(message));

		model.addAttribute("sendMailsPreviewModel", sendMailsPreviewModel);
		bindAndSetupParticipantMailAttributes(model, sendMailsModel, uuid, locale);

		return getFullViewName("sendParticipantsForm");
	}

	protected void bindAndSetupParticipantMailAttributes(Model model, BaseSendMailsModel sendMailsModel, String uuid, Locale locale) {
		List<Participant> allParticipants = runningDinnerService.loadAllParticipantsOfDinner(uuid);

		Map<String, String> participantDisplayMap = new LinkedHashMap<String, String>();
		for (Participant participant : allParticipants) {
			participantDisplayMap.put(participant.getNaturalKey(), participant.getName().getFullnameFirstnameFirst());
		}

		sendMailsModel.setEntityDisplayMap(participantDisplayMap);
		model.addAttribute("sendMailsModel", sendMailsModel);
		model.addAttribute("uuid", uuid);

		ParticipantMailReport lastMailReport = runningDinnerService.findLastParticipantMailReport(uuid);
		sendMailsModel.setLastMailReport(lastMailReport);
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
			@ModelAttribute("editMealTimesModel") EditMealTimesModel editMealTimesModel, BindingResult bindingResult,
			HttpServletRequest request, Model model, RedirectAttributes redirectAttributes, Locale locale) {

		adminValidator.validateUuid(uuid);

		if (request.getParameter("cancel") != null) {
			return generateStatusPageRedirect(RequestMappings.ADMIN_OVERVIEW, uuid, redirectAttributes, new SimpleStatusMessage());
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

		SimpleStatusMessage statusMessage = new SimpleStatusMessage(SimpleStatusMessage.SUCCESS_STATUS, messages.getMessage(
				"label.meals.edit.success", null, locale));
		return generateStatusPageRedirect(RequestMappings.EDIT_MEALTIMES, uuid, redirectAttributes, statusMessage);
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
			BindingResult bindingResult, Model model, final RedirectAttributes redirectAttributes, Locale locale) {

		adminValidator.validateNaturalKeys(Arrays.asList(participantKey));

		if (request.getParameter("cancel") != null) {
			return generateStatusPageRedirect(RequestMappings.SHOW_TEAMS, uuid, redirectAttributes, new SimpleStatusMessage());
		}

		adminValidator.validateParticipant(participant, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("participant", participant);
			model.addAttribute("uuid", uuid);
			return getFullViewName("editParticipantForm");
		}
		runningDinnerService.updateParticipant(participantKey, participant);

		String redirectUrl = RequestMappings.EDIT_PARTICIPANT;
		redirectUrl = redirectUrl.replaceFirst("\\{key\\}", participantKey);
		return generateStatusPageRedirect(redirectUrl, uuid, redirectAttributes, new SimpleStatusMessage(
				SimpleStatusMessage.SUCCESS_STATUS, messages.getMessage("label.participant.edit.success", null, locale)));
	}

	@RequestMapping(value = RequestMappings.EXCHANGE_TEAM, method = RequestMethod.GET)
	public String exchangeTeam(@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid, Model model) {
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
	public SaveTeamHostsResponse saveTeamHosts(@PathVariable(RequestMappings.ADMIN_URL_UUID_MARKER) String uuid,
			@RequestBody TeamHostChangeList changedHostTeams) {
		adminValidator.validateUuid(uuid);

		if (CoreUtil.isEmpty(changedHostTeams)) {
			return SaveTeamHostsResponse.createSuccessResponse(Collections.<Team> emptyList());
		}

		Map<String, String> teamHostMappings = TeamHostChangeList.generateTeamHostsMap(changedHostTeams);

		try {
			List<Team> updatedTeamHosters = runningDinnerService.updateTeamHosters(uuid, teamHostMappings);
			return SaveTeamHostsResponse.createSuccessResponse(updatedTeamHosters);
		}
		catch (Exception ex) {
			LOGGER.error("Failed to update team hosters for dinner {} with number of passed teamHostMappings {}", uuid,
					teamHostMappings.size(), ex);
			return SaveTeamHostsResponse.createErrorResponse(ex.getMessage());
		}
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
			SwitchTeamMembersResponse response = SwitchTeamMembersResponse.createSuccessResponse(result, uuid);
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

	protected String generateStatusPageRedirect(String redirectUrl, String uuid, RedirectAttributes redirectAttributes,
			SimpleStatusMessage statusMessage) {

		redirectAttributes.addFlashAttribute("statusMessage", statusMessage);

		String theRedirectUrl = redirectUrl.replaceFirst("\\{" + RequestMappings.ADMIN_URL_UUID_MARKER + "\\}", uuid);

		return "redirect:" + theRedirectUrl;
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
