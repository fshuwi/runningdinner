package org.runningdinner.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.GenderAspects;
import org.runningdinner.core.Participant;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.ConverterFactory;
import org.runningdinner.core.converter.ConverterFactory.INPUT_FILE_TYPE;
import org.runningdinner.core.converter.FileConverter;
import org.runningdinner.core.converter.config.ParsingConfiguration;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.util.WebUtils;

@Controller
@SessionAttributes("createWizardModel")
public class CreateFrontController {

	private MessageSource messages;

	private CreateWizardValidator validator;

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
	public String doWizardFileUpload(HttpServletRequest request, @ModelAttribute("uploadFileModel") UploadFileModel uploadFileModel,
			BindingResult bindingResult, SessionStatus sessionStatus, @RequestParam("_page") int currentWizardView) {

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
			bindingResult.rejectValue("file", constructErrorMessage(convEx));
			return getFullViewName(wizardViews.get(currentWizardView));
		}
		catch (Exception ex) {
			// Generic (technical) error, show error-view:
			return "error"; // TODO
		}
	}

	private String constructErrorMessage(final ConversionException convEx) {
		// TODO Auto-generated method stub
		return StringUtils.EMPTY;
	}

	private void handleFileUploadStep(UploadFileModel uploadFileModel, BindingResult bindingResult, HttpServletRequest request)
			throws IOException, ConversionException {
		// TODO

		HttpSession session = request.getSession();
		CreateWizardModel createWizardModel = (CreateWizardModel)session.getAttribute("createWizardModel");

		// #1 a) Construct ParsingConfiguration
		ParsingConfiguration parsingConfiguration = ParsingConfiguration.newDefaultConfiguration(); // TODO for now

		// #1 b) Try to parse temporary uploaded file with ExcelConverter and Parsing Configuration
		// If fail => show same view with error info (exceptions are thrown
		final MultipartFile file = uploadFileModel.getFile();
		INPUT_FILE_TYPE fileType = validator.getFileType(file);

		// TODO: Den Code-Block auslagern
		InputStream inputStream = null;
		List<Participant> participants = null;
		try {
			inputStream = file.getInputStream();
			FileConverter fileConverter = ConverterFactory.newConverter(parsingConfiguration, fileType);
			participants = fileConverter.parseParticipants(inputStream);
		}
		finally {
			CoreUtil.closeStream(inputStream);
		}
		// End TODO

		// #2 Copy file from tmp-directory to personal temp-directory
		// Filename: session.getId() + "_" + originalFilename
		// Save this absolute filepath into session-model
		final String filename = file.getOriginalFilename();
		final String tempDir = "D:\\Apps\\";

		File newFile = new File(tempDir + session.getId() + "_" + filename);
		if (!newFile.exists()) {
			newFile.createNewFile();
		}
		IOUtils.copy(file.getInputStream(), new FileOutputStream(newFile));

		createWizardModel.setUploadedFilePath(newFile.getAbsolutePath());

		// #3 Set parsed participants into request-attribute
		request.setAttribute("participants", participants);

		// #4 Generate UUID and Admin-Link and set it to model
		UUID generatedUUID = UUID.randomUUID();
		String uuid = generatedUUID.toString();
		// TODO
	}

	private String handleFileUpload(UploadFileModel uploadFileModel, BindingResult bindingResult) throws IOException {
		InputStream inputStream = null;
		OutputStream outputStream = null;

		MultipartFile file = uploadFileModel.getFile();
		String fileName = file.getOriginalFilename();

		if (file instanceof CommonsMultipartFile) {
			CommonsMultipartFile multiPartFile = (CommonsMultipartFile)file;
			// multiPartFile.getFileItem().
			System.out.println(multiPartFile.getStorageDescription());
		}

		try {
			inputStream = file.getInputStream();

			// TODO: Just for now (testing purposes)
			File newFile = new File("D:\\Apps\\" + fileName);
			if (!newFile.exists()) {
				newFile.createNewFile();
			}
			outputStream = new FileOutputStream(newFile);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}
		finally {
			CoreUtil.closeStream(outputStream);
			CoreUtil.closeStream(inputStream);
		}

		return fileName;

	}

	// @RequestMapping("/fileUpload")
	// public ModelAndView fileUploaded(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result) {
	// InputStream inputStream = null;
	// OutputStream outputStream = null;
	//
	// MultipartFile file = uploadedFile.getFile();
	// String fileName = file.getOriginalFilename();
	//
	// if (result.hasErrors()) {
	// return new ModelAndView("uploadForm");
	// }
	//
	// try {
	// inputStream = file.getInputStream();
	//
	// File newFile = new File("D:\\Apps\\" + fileName);
	// if (!newFile.exists()) {
	// newFile.createNewFile();
	// }
	// outputStream = new FileOutputStream(newFile);
	// int read = 0;
	// byte[] bytes = new byte[1024];
	//
	// while ((read = inputStream.read(bytes)) != -1) {
	// outputStream.write(bytes, 0, read);
	// }
	// }
	// catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return new ModelAndView("showFile", "message", fileName);
	// }

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

}
