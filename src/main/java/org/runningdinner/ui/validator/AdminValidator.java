package org.runningdinner.ui.validator;

import org.runningdinner.exceptions.InvalidUuidException;
import org.runningdinner.service.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
}
