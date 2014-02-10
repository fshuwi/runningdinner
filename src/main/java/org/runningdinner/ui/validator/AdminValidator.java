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
			validateNaturalKey(naturalKey);
		}
	}

	public void validateNaturalKey(final String naturalKey) {
		if (!AbstractEntity.isValid(naturalKey)) {
			throw new IllegalArgumentException("Invalid natural key passed!");
		}
	}

	public void validateSendMessagesModel(BaseSendMailsModel sendMailsModel, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "subject", "error.required.subject");
		ValidationUtils.rejectIfEmpty(errors, "message", "error.required.message");
	}

	public void validateParticipant(Participant participant, Errors errors) {
		// TODO i18n
		ValidationUtils.rejectIfEmpty(errors, "name.firstnamePart", "TODO", "Vorname fehlt");
		ValidationUtils.rejectIfEmpty(errors, "name.lastname", "TODO", "Nachname fehlt");
		ValidationUtils.rejectIfEmpty(errors, "address.street", "TODO", "Strasse fehlt");
		ValidationUtils.rejectIfEmpty(errors, "address.streetNr", "TODO", "Strassen-Nr fehlt");
		ValidationUtils.rejectIfEmpty(errors, "address.zip", "TODO", "PLZ fehlt");
		ValidationUtils.rejectIfEmpty(errors, "numSeats", "TODO", "Anzahl Plätze fehlt");
		ValidationUtils.rejectIfEmpty(errors, "email", "TODO", "Email fehlt");

		if (StringUtils.isNotEmpty(participant.getEmail())) {
			if (!participant.getEmail().contains("@")) {
				errors.rejectValue("email", "TODO", "Ungueltige EMail");
			}
		}
	}
}
