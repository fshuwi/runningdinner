package org.runningdinner.service;

import java.util.List;
import java.util.Locale;

import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.exceptions.GeocodingException;
import org.runningdinner.model.GeocodingResult;

public interface GeocoderService {

	List<GeocodingResult> geocodeAddress(final ParticipantAddress address, final Locale locale) throws GeocodingException;
}
