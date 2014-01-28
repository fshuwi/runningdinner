package org.runningdinner.ui.validator;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.MealClass;
import org.runningdinner.core.Participant;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

@Component
public class CommonValidator {

	public void validateMealTimes(Set<MealClass> meals, Errors errors) {
	}

	public void validateParticipant(Participant participant, Errors errors) {
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
