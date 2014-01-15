package org.runningdinner.service.impl;

import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.service.UuidGenerator;

/**
 * Default implementation of {@link UuidGenerator} which uses Java's UUID class.
 * 
 * @author i01002492
 * 
 */
public class DefaultUuidGenerator implements UuidGenerator {

	@Override
	public String generateNewUUID() {
		UUID generatedUUID = UUID.randomUUID();
		return generatedUUID.toString();
	}

	@Override
	public boolean isValid(final String uuid) {
		if (StringUtils.isEmpty(uuid)) {
			return false;
		}

		if (uuid.length() > (32 + 4)) { // UUID is composed of 32 digits and 4 separator chars
			return false;
		}

		return Pattern.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}", uuid);
	}

}
