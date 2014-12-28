package org.runningdinner.service.geocoder.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.exceptions.GeocodingException;
import org.runningdinner.model.GeocodingResult;
import org.runningdinner.service.GeocoderService;

public abstract class AbstractGeocoderService implements GeocoderService {

	@Override
	public Map<ParticipantAddress, List<GeocodingResult>> geocodeAddresses(Set<ParticipantAddress> addresses, Locale locale)
			throws GeocodingException {
		
		Map<ParticipantAddress, List<GeocodingResult>> result = new HashMap<>();

		for (ParticipantAddress address : addresses) {
			List<GeocodingResult> geocodingResult = geocodeAddress(address, locale);
			result.put(address, geocodingResult);
		}
		return result;
	}

}
