package org.runningdinner.ui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.runningdinner.core.GenderAspect;
import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.runningdinner.service.impl.AdminUrlGenerator;
import org.runningdinner.service.impl.RunningDinnerServiceImpl;
import org.runningdinner.ui.dto.ColumnMappingOption;
import org.runningdinner.ui.dto.CreateWizardModel;
import org.runningdinner.ui.dto.GenderAspectOption;
import org.runningdinner.ui.dto.UploadFileModel;
import org.runningdinner.ui.util.MealClassPropertyEditor;
import org.runningdinner.ui.validator.CreateWizardValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

// TODO: Remove parsing config from session attributes!

@Controller
@SessionAttributes("createWizardModel")
public class CreateWizardController extends AbstractBaseController {

	private MessageSource messages;
	private CreateWizardValidator validator;
	private RunningDinnerServiceImpl runningDinnerService;
	private AdminUrlGenerator adminUrlGenerator;

	private static Map<Integer, String> wizardViews = new HashMap<Integer, String>(4);

	static {
		wizardViews.put(0, "start");
		wizardViews.put(1, "times");
		wizardViews.put(2, "upload");
		wizardViews.put(3, "save");
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Set.class, "meals", new MealClassPropertyEditor());
		// These fields are modified only on server-side:
		binder.setDisallowedFields("uploadedFileLocation", "newUuid", "administrationUrl");
	}

	@RequestMapping(value = "/wizard", method = RequestMethod.GET)
	public String startWizard(Model model, SessionStatus sessionStatus) {
		CreateWizardModel wizardModel = CreateWizardModel.newModelWithDefaults();
		model.addAttribute("createWizardModel", wizardModel);
		return getFullViewName("start");
	}

	@RequestMapping(value = "/finish", method = RequestMethod.GET)
	public String finishWizard(@ModelAttribute("createWizardModel") CreateWizardModel createWizardModel, Model model) {
		model.addAttribute("createWizardMOdel", createWizardModel);
		return getFullViewName("finish");
	}

	@RequestMapping(value = "/")
	public String index() {
		return "redirect:/wizard";
	}

	@RequestMapping(value = "/wizard", method = RequestMethod.POST)
	public String doWizardStep(HttpServletRequest request, @ModelAttribute("createWizardModel") CreateWizardModel createWizardModel,
			BindingResult bindingResult, Model model, SessionStatus sessionStatus, Locale locale,
			final RedirectAttributes redirectAttributes, @RequestParam("_page") int currentWizardView) {

		if (request.getParameter("_cancel") != null || !isWizardViewIndexInRange(currentWizardView)) {
			sessionStatus.setComplete();
			return "redirect:/wizard";
		}

		if (request.getParameter("_finish") != null) {
			validator.validate(createWizardModel, bindingResult);
			if (bindingResult.hasErrors()) {
				return getFullViewName(wizardViews.get(currentWizardView));
			}

			createNewRunningDinner(createWizardModel);

			sessionStatus.setComplete();

			redirectAttributes.addFlashAttribute("createWizardModel", createWizardModel);
			return "redirect:/finish";
		}

		int targetView = WebUtils.getTargetPage(request, "_target", currentWizardView);

		if (targetView < currentWizardView) {
			// User wants to go to previous page:
			return getFullViewName(wizardViews.get(targetView));
		}

		// User wants to advance to next page:
		switch (currentWizardView) {
			case 0:
				validator.validateBasicDinnerOptions(createWizardModel, bindingResult);
				if (!bindingResult.hasErrors()) {
					// Populate model with valid date-objects for each meal:
					createWizardModel.prepareDefaultTimes();
				}
				break;
			case 1:
				validator.validateMealTimes(createWizardModel, bindingResult);
				if (!bindingResult.hasErrors()) {
					createWizardModel.applyDateToMealTimes();
					prepareUploadView(model, createWizardModel);
				}
				break;
		}

		if (bindingResult.hasErrors()) {
			// Errors, display same view with error information
			return getFullViewName(wizardViews.get(currentWizardView));
		}

		// Advance to next view
		return getFullViewName(wizardViews.get(targetView));
	}

	protected void prepareUploadView(Model model, CreateWizardModel createWizardModel) {
		// Prepare upload form with default parsing config:
		ParsingConfiguration parsingConfig = createWizardModel.getParsingConfiguration();
		model.addAttribute("uploadFileModel", UploadFileModel.newFromParsingConfiguration(parsingConfig));
	}

	private void createNewRunningDinner(CreateWizardModel createWizardModel) {

		try {
			final ParsingConfiguration parsingConfiguration = createWizardModel.getParsingConfiguration();
			List<Participant> participants = runningDinnerService.getParticipantsFromTempLocation(
					createWizardModel.getUploadedFileLocation(), parsingConfiguration);

			RunningDinnerConfig runningDinnerConfig = createWizardModel.createRunningDinnerConfiguration();

			runningDinnerService.createRunningDinner(createWizardModel, runningDinnerConfig, participants, createWizardModel.getNewUuid());
		}
		catch (IOException ex) {
			throw new IllegalStateException("Could not get participants list from location " + createWizardModel.getUploadedFileLocation(),
					ex);
		}
		catch (ConversionException ex) {
			throw new IllegalStateException("Conversion/Parsing error while  getting participants list from location "
					+ createWizardModel.getUploadedFileLocation(), ex);
		}
	}

	/**
	 * Performs the main wizard action (last step before finishing) and uploads the participant file.<br>
	 * 
	 * @param locale
	 * @param request
	 * @param uploadFileModel
	 * @param bindingResult
	 * @param sessionStatus
	 * @param currentWizardView
	 * @return
	 */
	@RequestMapping(value = "/wizard-upload", method = RequestMethod.POST)
	public String doWizardFileUpload(Locale locale, HttpServletRequest request,
			@ModelAttribute("uploadFileModel") UploadFileModel uploadFileModel, Model model, BindingResult bindingResult,
			SessionStatus sessionStatus, @RequestParam("_page") int currentWizardView) {

		if (request.getParameter("_cancel") != null || !isWizardViewIndexInRange(currentWizardView)) {
			sessionStatus.setComplete();
			return "redirect:/wizard";
		}

		int targetView = WebUtils.getTargetPage(request, "_target", currentWizardView);

		validator.validateFileUploadControls(uploadFileModel, bindingResult);
		if (bindingResult.hasErrors()) {
			return renderUploadViewAgainAfterError(currentWizardView, uploadFileModel);
		}

		try {
			handleFileUploadStep(uploadFileModel, bindingResult, request, locale);
			// Advance to next view
			return getFullViewName(wizardViews.get(targetView));
		}
		catch (ConversionException convEx) {
			validator.rejectConversionUploadError(bindingResult, convEx, "file");
			return renderUploadViewAgainAfterError(currentWizardView, uploadFileModel);
		}
		catch (Exception ex) {
			// Technical error => Pass to global exception handler
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	@RequestMapping(value = "/wizard-upload", method = RequestMethod.GET)
	public String wizardFileUploadGETHandler(@ModelAttribute("createWizardModel") CreateWizardModel createWizardModel, Model model) {
		// Prevent HTTP error when user tries to load upload view directly from browser
		prepareUploadView(model, createWizardModel);
		return getFullViewName("upload");
	}

	/**
	 * Displays the passed upload-view again with error information and takes care of correct ordering of column mappings of the
	 * UploadFileModel
	 * 
	 * @param uploadView
	 * @param uploadFileModel
	 * @return
	 */
	protected String renderUploadViewAgainAfterError(final int uploadView, final UploadFileModel uploadFileModel) {
		// Ensure that all mappings are displayed from 1th-Nth column (order may be cluttered due to request)
		uploadFileModel.sortColumnMappings();

		return getFullViewName(wizardViews.get(uploadView));
	}

	/**
	 * Main method in controller which performs the following steps:<br>
	 * - Parse uploaded participants-file<br>
	 * - Copy the uploaded-file (respectively the parsed participants) to a persistent storage location for later retrieval<br>
	 * - Calculate participant preview data and set it into request<br>
	 * - Generate new uuid and administration link for running dinner<br>
	 * 
	 * @param uploadFileModel
	 * @param bindingResult
	 * @param request
	 * @param locale
	 * @throws IOException
	 * @throws ConversionException
	 */
	private void handleFileUploadStep(UploadFileModel uploadFileModel, BindingResult bindingResult, HttpServletRequest request,
			Locale locale) throws IOException, ConversionException {

		HttpSession session = request.getSession();
		CreateWizardModel createWizardModel = (CreateWizardModel)session.getAttribute("createWizardModel");

		// #1 a) Construct ParsingConfiguration
		ParsingConfiguration parsingConfiguration = uploadFileModel.createParsingConfiguration();
		createWizardModel.setParsingConfiguration(parsingConfiguration);

		// #1 b) Try to parse temporary uploaded file with ExcelConverter and Parsing Configuration
		// If fail => show same view with error info (exceptions are thrown
		final MultipartFile file = uploadFileModel.getFile();

		List<Participant> participants = runningDinnerService.parseParticipants(file, parsingConfiguration);

		// #2 Copy file from tmp-directory to personal tmp-directory
		// Save this absolute filepath into session-model
		String location = runningDinnerService.copyParticipantFileToTempLocation(file, session.getId());
		createWizardModel.setUploadedFileLocation(location);

		// #3 Prepare participant table preview:
		setParticipantListViewAttributes(request, participants, createWizardModel.createRunningDinnerConfiguration(), locale);

		// #4 Generate UUID and Admin-Link and set it to model
		String uuid = runningDinnerService.generateNewUUID();
		createWizardModel.setNewUuid(uuid);
		createWizardModel.setAdministrationUrl(adminUrlGenerator.constructAdministrationUrl(uuid, request));
	}

	/**
	 * Performs calculation whether all patricipants can successfully be assigned to teams with the current running diner configuration.<br>
	 * The results are put into request context and can then be rendered inside the view.
	 * 
	 * @param participants
	 * @param createWizardModel
	 * @param request
	 * @param locale
	 */
	private void setParticipantPreviewStatus(List<Participant> participants, CreateWizardModel createWizardModel,
			HttpServletRequest request, Locale locale) {

		final RunningDinnerConfig runningDinnerConfig = createWizardModel.createRunningDinnerConfiguration();

		List<Participant> notAssignableParticipants = runningDinnerService.calculateNotAssignableParticipants(runningDinnerConfig,
				participants);
		request.setAttribute("notAssignableParticipants", notAssignableParticipants);

		if (notAssignableParticipants.size() == 0) {
			// Every participant can be assigned into a team
			request.setAttribute("participantStatus", "success");
			request.setAttribute("participantStatusMessage", messages.getMessage("text.participant.preview.success", null, locale));
		}
		else if (notAssignableParticipants.size() == participants.size()) {
			// Too few participants for assigning them into valid team combinations
			request.setAttribute("participantStatus", "danger");
			request.setAttribute("participantStatusMessage", messages.getMessage("text.participant.preview.error", null, locale));
		}
		else {
			// Not every participant can successfuly be assigned into a team
			request.setAttribute("participantStatus", "warning");
			request.setAttribute("participantStatusMessage", messages.getMessage("text.participant.preview.warning", null, locale));
		}
	}

	/**
	 * Used for select-box in first wizard step
	 * 
	 * @param locale
	 * @return
	 */
	@ModelAttribute("genderAspects")
	public List<GenderAspectOption> popuplateGenderAspects(Locale locale) {
		List<GenderAspectOption> result = new ArrayList<GenderAspectOption>(3);
		result.add(new GenderAspectOption(GenderAspect.IGNORE_GENDER, messages.getMessage("select.gender.random", null, locale)));
		result.add(new GenderAspectOption(GenderAspect.FORCE_GENDER_MIX, messages.getMessage("select.gender.mix", null, locale)));
		result.add(new GenderAspectOption(GenderAspect.FORCE_SAME_GENDER, messages.getMessage("select.gender.same", null, locale)));
		return result;
	}

	/**
	 * Used for select-box in upload wizard step
	 * 
	 * @param locale
	 * @return
	 */
	@ModelAttribute("columnMappingOptionItems")
	public List<ColumnMappingOption> popuplateColumnMappingOptionItems(Locale locale) {
		return ColumnMappingOption.generateColumnMappingOptions(messages, locale);
	}

	/**
	 * Checks whether the passed wizard view index was not manipulated by user and contains an invalid number
	 * 
	 * @param currentWizardViewIndex
	 * @return
	 */
	private static boolean isWizardViewIndexInRange(final int currentWizardViewIndex) {
		return currentWizardViewIndex >= 0 && currentWizardViewIndex < wizardViews.size();
	}

	protected String getFullViewName(final String viewName) {
		return "wizard/" + viewName;
	}

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messages = messageSource;
	}

	@Override
	protected MessageSource getMessageSource() {
		return this.messages;
	}

	@Autowired
	public void setValidator(CreateWizardValidator validator) {
		this.validator = validator;
	}

	@Autowired
	public void setRunningDinnerService(RunningDinnerServiceImpl runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

	@Override
	public RunningDinnerServiceImpl getRunningDinnerService() {
		return runningDinnerService;
	}

	@Autowired
	public void setAdminUrlGenerator(AdminUrlGenerator adminUrlGenerator) {
		this.adminUrlGenerator = adminUrlGenerator;
	}

}
