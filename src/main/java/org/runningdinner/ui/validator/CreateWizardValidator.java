package org.runningdinner.ui.validator;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.MealClass;
import org.runningdinner.core.converter.ConverterFactory.INPUT_FILE_TYPE;
import org.runningdinner.service.impl.RunningDinnerServiceImpl;
import org.runningdinner.ui.dto.CreateWizardModel;
import org.runningdinner.ui.dto.UploadFileModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CreateWizardValidator implements Validator {

	private RunningDinnerServiceImpl runningDinnerService;

	@Override
	public boolean supports(Class<?> clazz) {
		return CreateWizardModel.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		validateBasicDinnerOptions(target, errors);
		validateMealTimes(target, errors);

		// TODO: Add message codes
		ValidationUtils.rejectIfEmpty(errors, "uploadedFileLocation", "TODO");
		ValidationUtils.rejectIfEmpty(errors, "newUuid", "TODO");
		ValidationUtils.rejectIfEmpty(errors, "administrationUrl", "TODO");
	}

	public void validateBasicDinnerOptions(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "title", "error.required.title");
		ValidationUtils.rejectIfEmpty(errors, "date", "error.required.date");

		CreateWizardModel model = (CreateWizardModel)target;
		validateDinnerDate(model.getDate(), errors);
		validateMeals(model.getMeals(), errors);
	}

	/**
	 * Checks that there are at least 2 and max. 6 meals.<br>
	 * Each meal must not be empty.
	 * 
	 * @param meals
	 * @param errors
	 */
	private void validateMeals(Set<MealClass> meals, Errors errors) {
		if (CoreUtil.isEmpty(meals) || meals.size() < 2) {
			errors.rejectValue("meals", "error.meals.invalidsize");
		}
		else if (meals.size() > 6) {
			errors.rejectValue("meals", "error.meals.invalidsize");
		}
		else {
			for (MealClass meal : meals) {
				if (StringUtils.isEmpty(meal.getLabel())) {
					errors.rejectValue("meals", "error.meals.empty");
					break;
				}
			}
		}
	}

	/**
	 * Ensures that date is not in past
	 * 
	 * @param date
	 * @param errors
	 */
	private void validateDinnerDate(Date date, Errors errors) {
		if (date != null) {
			Date now = new Date();
			if (!DateUtils.isSameDay(now, date) && date.before(now)) {
				errors.rejectValue("date", "error.date.invalid");
			}
		}
	}

	/**
	 * Checks whether file is uploaded and if uploaded file has correct file-type
	 * 
	 * @param target
	 * @param errors
	 */
	public void validateFileUploadControls(Object target, Errors errors) {

		UploadFileModel uploadFileModel = (UploadFileModel)target;
		MultipartFile file = uploadFileModel.getFile();
		if (file == null || file.getSize() == 0) {
			errors.rejectValue("file", "error.required.file");
		}

		if (file != null) {
			if (INPUT_FILE_TYPE.UNKNOWN == runningDinnerService.determineFileType(file)) {
				errors.rejectValue("file", "error.file.invalidtype");
			}
		}
	}

	public void validateMealTimes(Object target, Errors errors) {

	}

	@Autowired
	public void setRunningDinnerService(RunningDinnerServiceImpl runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}
}
