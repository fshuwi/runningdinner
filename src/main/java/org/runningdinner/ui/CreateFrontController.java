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

import org.runningdinner.core.GenderAspects;
import org.runningdinner.core.Participant;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.ConversionException.CONVERSION_ERROR;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.runningdinner.service.impl.RunningDinnerWebServiceImpl;
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
import org.springframework.web.util.WebUtils;

@Controller
@SessionAttributes("createWizardModel")
public class CreateFrontController {

	private MessageSource messages;
	private CreateWizardValidator validator;
	private RunningDinnerWebServiceImpl runningDinnerService;

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

	@RequestMapping(value = "/")
	public String index() {
		return "redirect:/wizard";
	}

	@RequestMapping(value = "/wizard", method = RequestMethod.POST)
	public String doWizardStep(HttpServletRequest request, @ModelAttribute("createWizardModel") CreateWizardModel createWizardModel,
			BindingResult bindingResult, Model model, SessionStatus sessionStatus, @RequestParam("_page") int currentWizardView) {

		if (request.getParameter("_cancel") != null || !isWizardViewIndexInRange(currentWizardView)) {
			sessionStatus.setComplete();
			return "redirect:/start";
		}

		if (request.getParameter("_finish") != null) {
			validator.validate(createWizardModel, bindingResult);
			if (bindingResult.hasErrors()) {
				return getFullViewName(wizardViews.get(currentWizardView));
			}

			// TODO: Persist!
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
					model.addAttribute("uploadFileModel", new UploadFileModel());
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

	@RequestMapping(value = "/wizard-upload", method = RequestMethod.POST)
	public String doWizardFileUpload(Locale locale, HttpServletRequest request,
			@ModelAttribute("uploadFileModel") UploadFileModel uploadFileModel, BindingResult bindingResult, SessionStatus sessionStatus,
			@RequestParam("_page") int currentWizardView) {

		if (request.getParameter("_cancel") != null || !isWizardViewIndexInRange(currentWizardView)) {
			sessionStatus.setComplete();
			return "redirect:/wizard";
		}

		int targetView = WebUtils.getTargetPage(request, "_target", currentWizardView);

		validator.validateFileUploadControls(uploadFileModel, bindingResult);
		if (bindingResult.hasErrors()) {
			// Errors, display same view with error information
			return getFullViewName(wizardViews.get(currentWizardView));
		}

		try {
			handleFileUploadStep(uploadFileModel, bindingResult, request);

			// Advance to next view
			return getFullViewName(wizardViews.get(targetView));
		}
		catch (ConversionException convEx) {
			bindingResult.rejectValue("file", constructErrorMessage(convEx, locale));
			return getFullViewName(wizardViews.get(currentWizardView));
		}
		catch (Exception ex) {
			// Technical error
			// TODO: Need generic error handling (view, logging)
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	private String constructErrorMessage(final ConversionException convEx, final Locale locale) {
		final CONVERSION_ERROR conversionError = convEx.getConversionError();
		final int rowNumber = convEx.getRowNumber();

		String errorCode = null;
		Object[] params = null;
		if (rowNumber < 0) {
			errorCode = "error.conversion.row.unknown";
			params = new Object[] { conversionError.name() };
		}
		else {
			errorCode = "error.conversion.row";
			params = new Object[] { conversionError.name(), convEx.getRowNumber() };
		}

		return messages.getMessage(errorCode, params, locale);
	}

	private void handleFileUploadStep(UploadFileModel uploadFileModel, BindingResult bindingResult, HttpServletRequest request)
			throws IOException, ConversionException {

		HttpSession session = request.getSession();
		CreateWizardModel createWizardModel = (CreateWizardModel)session.getAttribute("createWizardModel");

		// #1 a) Construct ParsingConfiguration
		ParsingConfiguration parsingConfiguration = ParsingConfiguration.newDefaultConfiguration(); // TODO for now

		// #1 b) Try to parse temporary uploaded file with ExcelConverter and Parsing Configuration
		// If fail => show same view with error info (exceptions are thrown
		final MultipartFile file = uploadFileModel.getFile();

		List<Participant> participants = runningDinnerService.parseParticipants(file, parsingConfiguration);

		// #2 Copy file from tmp-directory to personal tmp-directory
		// Save this absolute filepath into session-model
		String location = runningDinnerService.copyParticipantFileToTempLocation(file, session.getId());
		createWizardModel.setUploadedFileLocation(location);

		// #3 Set parsed participants into request-attribute, don't use session as this list might be quite big
		request.setAttribute("participants", participants);

		// #4 Generate UUID and Admin-Link and set it to model
		String uuid = runningDinnerService.generateNewUUID();
		createWizardModel.setNewUuid(uuid);
		createWizardModel.setAdministrationUrl(runningDinnerService.constructAdministrationUrl(uuid, request));
	}

	@ModelAttribute("genderAspects")
	public List<GenderAspectOption> popuplateGenderAspects(Locale locale) {
		List<GenderAspectOption> result = new ArrayList<GenderAspectOption>(3);
		result.add(new GenderAspectOption(GenderAspects.IGNORE_GENDER, messages.getMessage("select.gender.random", null, locale)));
		result.add(new GenderAspectOption(GenderAspects.FORCE_GENDER_MIX, messages.getMessage("select.gender.mix", null, locale)));
		result.add(new GenderAspectOption(GenderAspects.FORCE_SAME_GENDER, messages.getMessage("select.gender.same", null, locale)));
		return result;
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
		return "welcome/" + viewName;
	}

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messages = messageSource;
	}

	@Autowired
	public void setValidator(CreateWizardValidator validator) {
		this.validator = validator;
	}

	@Autowired
	public void setRunningDinnerService(RunningDinnerWebServiceImpl runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

}
