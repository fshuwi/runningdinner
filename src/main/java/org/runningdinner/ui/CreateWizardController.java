package org.runningdinner.ui;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.runningdinner.core.GenderAspect;
import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.runningdinner.core.util.CoreUtil;
import org.runningdinner.service.RunningDinnerService;
import org.runningdinner.service.impl.UrlGenerator;
import org.runningdinner.ui.dto.ColumnMappingOption;
import org.runningdinner.ui.dto.CreateWizardModel;
import org.runningdinner.ui.dto.SelectOption;
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

/**
 * This controller acts as the landing page and contains all methods for creating a new running dinner.<br>
 * The state of the wizard is hold in a session object during execution of wizard.
 * 
 * @author Clemens Stich
 * 
 */
@Controller
@SessionAttributes("createWizardModel")
public class CreateWizardController extends AbstractBaseController {

	private MessageSource messages;
	private CreateWizardValidator validator;
	private RunningDinnerService runningDinnerService;
	private UrlGenerator urlGenerator;

	private static Map<Integer, String> wizardViews = new HashMap<Integer, String>(4);

	static {
		wizardViews.put(0, "wizard-start");
		wizardViews.put(1, "times");
		wizardViews.put(2, "upload");
		wizardViews.put(3, "save");
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat dateFormat = CoreUtil.getDefaultDateFormat();
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(List.class, "meals", new MealClassPropertyEditor());
		// These fields are modified only on server-side:
		binder.setDisallowedFields("uploadedFileLocation", "newUuid", "administrationUrl");
	}

	@RequestMapping(value = RequestMappings.WIZARD_STEP, method = RequestMethod.GET)
	public String startWizard(Model model, SessionStatus sessionStatus, Locale locale) {
		CreateWizardModel wizardModel = CreateWizardModel.newModelWithDefaultSettingsAndMeals(messages, locale);
		model.addAttribute("createWizardModel", wizardModel);
		return getFullViewName("wizard-start");
	}

	@RequestMapping(value = RequestMappings.WIZARD_FINISH, method = RequestMethod.GET)
	public String finishWizard(@ModelAttribute("createWizardModel") CreateWizardModel createWizardModel, Model model) {
		// model.addAttribute("createWizardModel", createWizardModel);
		return getFullViewName("finish");
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {
		return "redirect:/wizard";
	}

	@RequestMapping(value = RequestMappings.WIZARD_STEP, method = RequestMethod.POST)
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
				reloadParticipantsIntoModel(createWizardModel, request, locale);
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

	protected void reloadParticipantsIntoModel(CreateWizardModel createWizardModel, HttpServletRequest request, Locale locale) {
		try {
			List<Participant> participants = runningDinnerService.getParticipantListFromTempLocation(createWizardModel.getUploadedFileLocation());
			setParticipantListViewAttributes(request, participants, createWizardModel.createRunningDinnerConfiguration(), locale);
		}
		catch (IOException ex) {
			throw new IllegalStateException("Could not get participants list from location " + createWizardModel.getUploadedFileLocation(),
					ex);
		}
	}

	protected void prepareUploadView(Model model, CreateWizardModel createWizardModel) {
		// Prepare upload form with default parsing config:
		ParsingConfiguration parsingConfig = ParsingConfiguration.newDefaultConfiguration();// createWizardModel.getParsingConfiguration();
		model.addAttribute("uploadFileModel", UploadFileModel.newFromParsingConfiguration(parsingConfig));
	}

	private void createNewRunningDinner(CreateWizardModel createWizardModel) {

		try {
			// final ParsingConfiguration parsingConfiguration = createWizardModel.getParsingConfiguration();
			List<Participant> participants = runningDinnerService.getParticipantListFromTempLocation(createWizardModel.getUploadedFileLocation());

			RunningDinnerConfig runningDinnerConfig = createWizardModel.createRunningDinnerConfiguration();

			runningDinnerService.createRunningDinner(createWizardModel, runningDinnerConfig, participants, createWizardModel.getNewUuid());
		}
		catch (IOException ex) {
			throw new IllegalStateException("Could not get participants list from location " + createWizardModel.getUploadedFileLocation(),
					ex);
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
	@RequestMapping(value = RequestMappings.WIZARD_UPLOAD, method = RequestMethod.POST)
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
		catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@RequestMapping(value = RequestMappings.WIZARD_UPLOAD, method = RequestMethod.GET)
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
	 * @throws ParsingConfigurationException 
	 */
	private void handleFileUploadStep(UploadFileModel uploadFileModel, BindingResult bindingResult, HttpServletRequest request,
			Locale locale) throws IOException, ConversionException {

		HttpSession session = request.getSession();
		CreateWizardModel createWizardModel = (CreateWizardModel)session.getAttribute("createWizardModel");

		// #1 a) Construct ParsingConfiguration
		ParsingConfiguration parsingConfiguration = uploadFileModel.createParsingConfiguration();
		// createWizardModel.setParsingConfiguration(parsingConfiguration);

		// #1 b) Try to parse temporary uploaded file with ExcelConverter and Parsing Configuration
		// If fail => show same view with error info (exceptions are thrown
		final MultipartFile file = uploadFileModel.getFile();

		List<Participant> participants = runningDinnerService.parseParticipants(file, parsingConfiguration);

		// #2 Copy file from tmp-directory to personal tmp-directory
		// Save this absolute filepath into session-model
		String location = runningDinnerService.copyParticipantListToTempLocation(participants, session.getId());
		createWizardModel.setUploadedFileLocation(location);

		// #3 Prepare participant table preview:
		setParticipantListViewAttributes(request, participants, createWizardModel.createRunningDinnerConfiguration(), locale);

		// #4 Generate UUID and Admin-Link and set it to model
		String uuid = runningDinnerService.generateNewUUID();
		createWizardModel.setNewUuid(uuid);
		createWizardModel.setAdministrationUrl(urlGenerator.constructAdministrationUrl(uuid, request));
	}

	/**
	 * Used for select-box in first wizard step
	 * 
	 * @param locale
	 * @return
	 */
	@ModelAttribute("genderAspects")
	public List<SelectOption> popuplateGenderAspects(Locale locale) {
		List<SelectOption> result = new ArrayList<SelectOption>(3);
		result.add(SelectOption.newGenderAspectOption(GenderAspect.IGNORE_GENDER, messages.getMessage("select.gender.random", null, locale)));
		result.add(SelectOption.newGenderAspectOption(GenderAspect.FORCE_GENDER_MIX, messages.getMessage("select.gender.mix", null, locale)));
		result.add(SelectOption.newGenderAspectOption(GenderAspect.FORCE_SAME_GENDER,
				messages.getMessage("select.gender.same", null, locale)));
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
	public void setRunningDinnerService(RunningDinnerService runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

	@Override
	public RunningDinnerService getRunningDinnerService() {
		return runningDinnerService;
	}

	@Autowired
	public void setUrlGenerator(UrlGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

}
