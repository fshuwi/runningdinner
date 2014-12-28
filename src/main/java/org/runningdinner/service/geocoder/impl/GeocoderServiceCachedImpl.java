package org.runningdinner.service.geocoder.impl;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.core.util.CoreUtil;
import org.runningdinner.exceptions.GeocodingException;
import org.runningdinner.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Optional;

public class GeocoderServiceCachedImpl extends AbstractGeocoderService {

	private GeocoderServiceDb dbGeocoderService;

	private GeocoderServiceGoogleImpl googleGeocoderService;

	@Override
	public List<GeocodingResult> geocodeAddress(final ParticipantAddress address, final Locale locale) throws GeocodingException {

		Optional<GeocodingResult> cachedResult = dbGeocoderService.findGeocodingForAddress(address);
		if (cachedResult.isPresent()) {
			return Collections.singletonList(cachedResult.get());
		}

		List<GeocodingResult> resultList = googleGeocoderService.geocodeAddress(address, locale);
		if (!CoreUtil.isEmpty(resultList) && resultList.size() == 1) {
			GeocodingResult geocodingResult = resultList.get(0);
			if (geocodingResult.isExact()) {
				dbGeocoderService.saveGeocodingForAddress(address, geocodingResult);
			}
		}

		return resultList;
	}

	@Autowired
	public void setDbGeocoderService(final GeocoderServiceDb dbGeocoderService) {
		this.dbGeocoderService = dbGeocoderService;
	}

	@Autowired
	public void setGoogleGeocoderService(final GeocoderServiceGoogleImpl googleGeocoderService) {
		this.googleGeocoderService = googleGeocoderService;
	}

}
