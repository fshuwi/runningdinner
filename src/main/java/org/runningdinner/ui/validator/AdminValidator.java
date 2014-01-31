package org.runningdinner.ui.validator;

import java.util.Collection;

import org.runningdinner.core.model.AbstractEntity;
import org.runningdinner.exceptions.InvalidUuidException;
import org.runningdinner.service.UuidGenerator;
import org.runningdinner.ui.dto.FinalizeTeamsModel;
import org.runningdinner.ui.dto.SendDinnerRoutesModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class AdminValidator {

	@Autowired
	private UuidGenerator uuidGenerator;

	/**
	 * 
	 * @param uuid
	 * @throws InvalidUuidException If passed uuid is not valid
	 */
	public void validateUuid(final String uuid) {
		if (!uuidGenerator.isValid(uuid)) {
			throw new InvalidUuidException("Invalid UUID passed!");
		}
	}

	public void validateNaturalKeys(Collection<String> naturalKeys) {
		for (String naturalKey : naturalKeys) {
			if (!AbstractEntity.validateNaturalKey(naturalKey)) {
				throw new IllegalArgumentException("Invalid natural key passed!");
			}
		}
	}

	public void validateFinalizeTeamsModel(FinalizeTeamsModel finalizeTeamsModel, Errors errors) {
		// TODO
		ValidationUtils.rejectIfEmpty(errors, "subject", "TODO", "Subject ist leer");
		ValidationUtils.rejectIfEmpty(errors, "message", "TODO", "Message ist leer");
	}

	public void validateSendMessagesModel(SendDinnerRoutesModel sendDinnerRoutesModel, Errors errors) {
		// TODO doppelt...
		ValidationUtils.rejectIfEmpty(errors, "subject", "TODO", "Subject ist leer");
		ValidationUtils.rejectIfEmpty(errors, "message", "TODO", "Message ist leer");

	}
}
