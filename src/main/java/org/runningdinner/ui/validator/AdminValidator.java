package org.runningdinner.ui.validator;

import java.util.Collection;

import org.runningdinner.core.model.AbstractEntity;
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

	public void validateNaturalKeys(Collection<String> naturalKeys) {
		for (String naturalKey : naturalKeys) {
			if (!AbstractEntity.validateNaturalKey(naturalKey)) {
				throw new IllegalArgumentException("Invalud natural key passed!");
			}
		}
	}
}
