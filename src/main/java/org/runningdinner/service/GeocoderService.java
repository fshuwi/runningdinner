package org.runningdinner.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.exceptions.GeocodingException;
import org.runningdinner.model.GeocodingResult;

public interface GeocoderService {

	List<GeocodingResult> geocodeAddress(final ParticipantAddress address, final Locale locale) throws GeocodingException;
	
	Map<ParticipantAddress,List<GeocodingResult>> geocodeAddresses(final Set<ParticipantAddress> addresses, final Locale locale) throws GeocodingException;
}
