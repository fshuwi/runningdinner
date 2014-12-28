package org.runningdinner.service.geocoder.impl;

import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.model.GeocodingResult;
import org.runningdinner.service.GeocoderService;

import com.google.common.base.Optional;

/**
 * This is needed for spring to avoid proxy bean creation errors
 * @author Clemens
 *
 */
public interface GeocoderServiceDb extends GeocoderService {
	
	void saveGeocodingForAddress(final ParticipantAddress address, final GeocodingResult geocodingResult);
	
	Optional<GeocodingResult> findGeocodingForAddress(final ParticipantAddress address); 
}
