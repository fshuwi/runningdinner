package org.runningdinner.ui.validator;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.runningdinner.core.MealClass;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.ConversionException.CONVERSION_ERROR;
import org.runningdinner.core.converter.ConverterFactory.INPUT_FILE_TYPE;
import org.runningdinner.core.util.CoreUtil;
import org.runningdinner.service.RunningDinnerService;
import org.runningdinner.ui.dto.ColumnMappingOption;
import org.runningdinner.ui.dto.CreateWizardModel;
import org.runningdinner.ui.dto.UploadFileModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CreateWizardValidator extends CommonBaseValidator implements Validator {

	private RunningDinnerService runningDinnerService;

	@Override
	public boolean supports(Class<?> clazz) {
		return CreateWizardModel.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		validateBasicDinnerOptions(target, errors);

		CreateWizardModel createWizardModel = (CreateWizardModel)target;

		super.validateMealTimes(createWizardModel.getMeals(), errors);

		ValidationUtils.rejectIfEmpty(errors, "email", "error.required.email");
		if (StringUtils.isNotEmpty(createWizardModel.getEmail()) && createWizardModel.getEmail().indexOf("@") < 0) {
			// This simple check is sufficient for now
			errors.rejectValue("email", "error.invalid.email");
		}

		// Actually these are internal attributes that should always exist, but if user maybe accessed the wizard in another way, these may
		// not exist. We just indicate this with a general error message:
		ValidationUtils.rejectIfEmpty(errors, "uploadedFileLocation", "error.wizard.invalidstate");
		ValidationUtils.rejectIfEmpty(errors, "newUuid", "error.wizard.invalidstate");
		ValidationUtils.rejectIfEmpty(errors, "administrationUrl", "error.wizard.invalidstate");
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
	private void validateMeals(Collection<MealClass> meals, Errors errors) {
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

		int startRow = uploadFileModel.getStartRow();
		if (startRow <= 0) {
			errors.rejectValue("startRow", "error.startRow.invalid");
		}

		validateParsingConfiguration(uploadFileModel, errors);
	}

	public void validateMealTimes(CreateWizardModel createWizardModel, BindingResult bindingResult) {
		super.validateMealTimes(createWizardModel.getMeals(), bindingResult);
	}

	/**
	 * Generates a detailed error message when the uploaded file could not successfully be parsed (e.g. wrong file format) and put it to the
	 * passed Errors object.
	 * 
	 * @param convEx
	 * @param locale
	 * @return
	 */
	public void rejectConversionUploadError(Errors errors, ConversionException convEx, String fieldName) {

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

		errors.rejectValue(fieldName, errorCode, params, null);
	}

	private void validateParsingConfiguration(UploadFileModel uploadFileModel, Errors errors) {

		boolean hasFullname = false;
		boolean hasFirstname = false;
		boolean hasLastname = false;

		boolean hasCompleteAddress = false;
		boolean hasZip = false;
		boolean hasStreetAndStreetNr = false;
		boolean hasStreet = false;
		boolean hasStreetNr = false;

		Map<Integer, String> columnMappings = uploadFileModel.getColumnMappings();
		for (Entry<Integer, String> columnMapping : columnMappings.entrySet()) {

			String columnMappingName = columnMapping.getValue();

			if (ColumnMappingOption.FULLNAME.equals(columnMappingName)) {
				hasFullname = true;
			}
			else if (ColumnMappingOption.FIRSTNAME.equals(columnMappingName)) {
				hasFirstname = true;
			}
			else if (ColumnMappingOption.LASTNAME.equals(columnMappingName)) {
				hasLastname = true;
			}
			else if (ColumnMappingOption.COMPLETE_ADDRESS.equals(columnMappingName)) {
				hasCompleteAddress = true;
			}
			else if (ColumnMappingOption.ZIP.equals(columnMappingName) || ColumnMappingOption.ZIP_WITH_CITY.equals(columnMappingName)) {
				hasZip = true;
			}
			else if (ColumnMappingOption.STREET_WITH_NR.equals(columnMappingName)) {
				hasStreetAndStreetNr = true;
			}
			else if (ColumnMappingOption.STREET.equals(columnMappingName)) {
				hasStreet = true;
			}
			else if (ColumnMappingOption.STREET_NR.equals(columnMappingName)) {
				hasStreetNr = true;
			}
		}

		if (!hasFullname && !(hasFirstname && hasLastname)) {
			errors.rejectValue(UploadFileModel.COLUMN_MAPPING_VALIDATION_PATH, "error.parsing.configuration.name");
			return;
		}

		if (hasStreet && hasStreetNr) {
			hasStreetAndStreetNr = true;
		}
		if (!hasCompleteAddress && !(hasZip && hasStreetAndStreetNr)) {
			errors.rejectValue(UploadFileModel.COLUMN_MAPPING_VALIDATION_PATH, "error.parsing.configuration.address");
		}
	}

	@Autowired
	public void setRunningDinnerService(RunningDinnerService runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

}
