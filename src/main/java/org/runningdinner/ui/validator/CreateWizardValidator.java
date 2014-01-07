package org.runningdinner.ui.validator;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.MealClass;
import org.runningdinner.core.converter.ConverterFactory;
import org.runningdinner.core.converter.ConverterFactory.INPUT_FILE_TYPE;
import org.runningdinner.ui.dto.CreateWizardModel;
import org.runningdinner.ui.dto.UploadFileModel;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CreateWizardValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return CreateWizardModel.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		validateBasicDinnerOptions(target, errors);
		validateMealTimes(target, errors);
		validateFileUploadControls(target, errors);
	}

	public void validateBasicDinnerOptions(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "title", "error.required.title");
		ValidationUtils.rejectIfEmpty(errors, "date", "error.required.date");

		CreateWizardModel model = (CreateWizardModel)target;
		validateDinnerDate(model.getDate(), errors);
		validateMeals(model.getMeals(), errors);

	}

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

	public void validateFileUploadControls(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "file", "error.required.file");

		UploadFileModel uploadFileModel = (UploadFileModel)target;
		MultipartFile file = uploadFileModel.getFile();
		if (file != null) {
			if (INPUT_FILE_TYPE.UNKNOWN == getFileType(file)) {
				errors.rejectValue("file", "error.file.invalidtype");
			}
		}
	}

	public INPUT_FILE_TYPE getFileType(MultipartFile file) {
		if (file != null) {
			INPUT_FILE_TYPE fileType = ConverterFactory.determineFileType(file.getOriginalFilename());
			if (INPUT_FILE_TYPE.UNKNOWN != fileType) {
				return fileType;
			}

			fileType = ConverterFactory.determineFileType(file.getContentType());
			if (INPUT_FILE_TYPE.UNKNOWN != fileType) {
				return fileType;
			}
		}
		return INPUT_FILE_TYPE.UNKNOWN;
	}

	public void validateMealTimes(Object target, Errors errors) {

	}
}
