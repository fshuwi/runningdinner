package org.runningdinner.ui.validator;

import org.runningdinner.service.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminValidator {

	@Autowired
	private UuidGenerator uuidGenerator;

	public void validateUuid(final String uuid) {
		if (!uuidGenerator.isValid(uuid)) {
			// TODO Other exception!
			throw new RuntimeException("Invalid UUID passed!");
		}
	}
}
