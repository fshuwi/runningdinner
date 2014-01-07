package org.runningdinner.service.impl;

import java.util.UUID;

import org.runningdinner.service.UuidGenerator;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link UuidGenerator} which uses Java's UUID class.
 * 
 * @author i01002492
 * 
 */
@Component
public class DefaultUuidGenerator implements UuidGenerator {

	@Override
	public String generateNewUUID() {
		UUID generatedUUID = UUID.randomUUID();
		return generatedUUID.toString();
	}

}
