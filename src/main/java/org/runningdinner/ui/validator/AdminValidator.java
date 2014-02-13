package org.runningdinner.ui.validator;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Participant;
import org.runningdinner.core.model.AbstractEntity;
import org.runningdinner.exceptions.InvalidUuidException;
import org.runningdinner.service.UuidGenerator;
import org.runningdinner.ui.dto.BaseSendMailsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class AdminValidator extends CommonBaseValidator {

	@Autowired
	private UuidGenerator uuidGenerator;

	/**
	 * Performs a pre-validation whether a passed dinner-UUID is valid after all. Thus we avoid passing an invalid UUID to the database.
	 * 
	 * @param uuid
	 * @throws InvalidUuidException If passed uuid is not valid
	 */
	public void validateUuid(final String uuid) {
		if (!uuidGenerator.isValid(uuid)) {
			throw new InvalidUuidException("Invalid UUID passed!");
		}
	}

	/**
	 * Performs a pre-validation whether a passed naturalKey of a database-entity is valid after all. Thus we avoid passing an invalid
	 * entity key to the database.
	 * 
	 * @param naturalKey
	 */
	public void validateNaturalKey(final String naturalKey) {
		if (!AbstractEntity.isValid(naturalKey)) {
			throw new IllegalArgumentException("Invalid natural key passed!");
		}
	}

	/**
	 * Convenience method for validation of several natural keys. See {@link validateNaturalKey}
	 * 
	 * @param naturalKeys
	 */
	public void validateNaturalKeys(Collection<String> naturalKeys) {
		for (String naturalKey : naturalKeys) {
			validateNaturalKey(naturalKey);
		}
	}

	public void validateSendMessagesModel(BaseSendMailsModel sendMailsModel, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "subject", "error.required.subject");
		ValidationUtils.rejectIfEmpty(errors, "message", "error.required.message");
	}

	public void validateParticipant(Participant participant, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "name.firstnamePart", "error.required.firstname", "Vorname fehlt");
		ValidationUtils.rejectIfEmpty(errors, "name.lastname", "error.required.lastname", "Nachname fehlt");
		ValidationUtils.rejectIfEmpty(errors, "address.street", "error.required.street", "Strasse fehlt");
		ValidationUtils.rejectIfEmpty(errors, "address.streetNr", "error.required.streetnr", "Strassen-Nr fehlt");
		ValidationUtils.rejectIfEmpty(errors, "address.zip", "error.required.zip", "PLZ fehlt");
		ValidationUtils.rejectIfEmpty(errors, "numSeats", "error.required.numseats");
		ValidationUtils.rejectIfEmpty(errors, "email", "error.required.email");

		if (StringUtils.isNotEmpty(participant.getEmail())) {
			if (!participant.getEmail().contains("@")) {
				errors.rejectValue("email", "error.invalid.email");
			}
		}

	}
}
