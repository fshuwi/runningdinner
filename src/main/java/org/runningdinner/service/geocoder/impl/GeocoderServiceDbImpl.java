package org.runningdinner.service.geocoder.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.exceptions.GeocodingException;
import org.runningdinner.model.GeocodingResult;
import org.runningdinner.repository.jpa.RunningDinnerRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;

public class GeocoderServiceDbImpl extends AbstractGeocoderService implements GeocoderServiceDb {

	private RunningDinnerRepositoryJpa repository;

	@Override
	public List<GeocodingResult> geocodeAddress(final ParticipantAddress address, final Locale locale) throws GeocodingException {

		DbGeocoderResult result = repository.findGeodingByNormalizedAddress(DbGeocoderResult.normalizeAddress(address));
		if (result == null) {
			return Collections.emptyList();
		}

		return Collections.singletonList(toGeocodingResult(result));
	}

	@Transactional
	public void saveGeocodingForAddress(final ParticipantAddress address, final GeocodingResult geocodingResult) {

		final String normalizedAddress = DbGeocoderResult.normalizeAddress(address);

		if (repository.findGeodingByNormalizedAddress(normalizedAddress) != null) {
			return;
		}

		DbGeocoderResult dbGeocoderResult = new DbGeocoderResult(geocodingResult.getLat(), geocodingResult.getLng(),
				geocodingResult.isExact(), address.getZip());
		dbGeocoderResult.setCityName(address.getCityName());
		dbGeocoderResult.setStreet(address.getStreet());
		dbGeocoderResult.setStreetNr(address.getStreetNr());
		dbGeocoderResult.setNormalizedAddressString(normalizedAddress);
		dbGeocoderResult.setFormattedAddressString(geocodingResult.getFormattedAddress());
		dbGeocoderResult.setLastAccess(new Date());

		repository.saveOrMerge(dbGeocoderResult);
	}

	public Optional<GeocodingResult> findGeocodingForAddress(final ParticipantAddress address) {
		final String normalizedAddress = DbGeocoderResult.normalizeAddress(address);
		DbGeocoderResult result = repository.findGeodingByNormalizedAddress(normalizedAddress);
		if (result == null) {
			return Optional.absent();
		}
		return Optional.of(toGeocodingResult(result));
	}

	private GeocodingResult toGeocodingResult(final DbGeocoderResult dbGeocoderResult) {
		GeocodingResult result = new GeocodingResult();
		result.setLat(dbGeocoderResult.getLat());
		result.setLng(dbGeocoderResult.getLng());
		result.setExact(true);
		result.setFormattedAddress(dbGeocoderResult.getFormattedAddressString());
		return result;
	}

	@Autowired
	public void setRepository(RunningDinnerRepositoryJpa repository) {
		this.repository = repository;
	}
}
