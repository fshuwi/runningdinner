package org.runningdinner.service.geocoder.impl;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.exceptions.GeocodingException;
import org.runningdinner.model.GeocodingResult;
import org.runningdinner.service.GeocoderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import com.google.common.base.Optional;

/**
 * See https://developers.google.com/maps/documentation/geocoding/ for API information.
 * 
 * @author Clemens
 *
 */
public class GeocoderServiceGoogleImpl implements GeocoderService {

	protected static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";

	protected RestTemplate restTemplate = new RestTemplate();

	private static Logger LOGGER = LoggerFactory.getLogger(GeocoderServiceGoogleImpl.class);

	public List<GeocodingResult> geocodeAddress(final ParticipantAddress address, final Locale locale) throws GeocodingException {

		String queryString = getQueryString(address);

		StringBuilder requestUrl = new StringBuilder();
		requestUrl.append(BASE_URL).append(queryString).append("&region=").append(mapLocaleToRegionTLD(locale)).append("&language=").append(
				locale.getLanguage()).append("&sensor=false");

		GoogleGeocderResponse response = executeRequest(requestUrl.toString());

		if (StringUtils.equalsIgnoreCase(response.getStatus(), "OK")) {
			return convertResponse(response);
		}
		else if (StringUtils.equalsIgnoreCase(response.getStatus(), "ZERO_RESULTS")) {
			return Collections.emptyList();
		}
		else {
			throw new GeocodingException("Google Geocoding API returned status " + response.getStatus() + " for request " + requestUrl);
		}
	}
	
	@Override
	public Map<ParticipantAddress, List<GeocodingResult>> geocodeAddresses(Set<ParticipantAddress> addresses, Locale locale)
			throws GeocodingException {
		// TODO Auto-generated method stub
		return null;
	}

	private GoogleGeocderResponse executeRequest(final String requestUrl) throws GeocodingException {
		try {
			return restTemplate.getForObject(new URI(requestUrl), GoogleGeocderResponse.class);
		}
		catch (RestClientException | URISyntaxException e) {
			throw new GeocodingException("Technical Exception when performing Google API call to URL " + requestUrl, e);
		}
	}

	private String getQueryString(ParticipantAddress address) {
		int zip = address.getZip();
		String cityName = address.getCityName();
		String street = address.getStreet();
		String streetNr = address.getStreetNr();

		String result = streetNr + " " + street + ", " + zip;
		if (StringUtils.isNotEmpty(cityName)) {
			result += " " + cityName;
		}

		try {
			result = UriUtils.encodeQueryParam(result, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return result;
	}

	protected List<GeocodingResult> convertResponse(final GoogleGeocderResponse response) {

		List<GeocodingResult> result = new ArrayList<GeocodingResult>();

		List<GoogleGeocoderResult> responseResults = response.getResults();
		for (GoogleGeocoderResult responseResult : responseResults) {
			Optional<GeocodingResult> geocodingResult = convertSingleResult(responseResult);
			if (geocodingResult.isPresent()) {
				result.add(geocodingResult.get());
			}
		}

		return result;
	}

	protected Optional<GeocodingResult> convertSingleResult(final GoogleGeocoderResult googleGeocodingResult) {
		if (googleGeocodingResult == null || googleGeocodingResult.getGeometry() == null) {
			// Should actually never happen
			LOGGER.warn("GoogleGeocoderResult contained unexpected null values. Object will be ignored!");
			return Optional.absent();
		}

		Geometry geometry = googleGeocodingResult.getGeometry();
		List<String> types = googleGeocodingResult.getTypes();
		Location location = geometry.getLocation();
		String locationType = geometry.getLocation_type();

		double lat = location.getLat();
		double lng = location.getLng();
		boolean exact = true;

		if (!StringUtils.equalsIgnoreCase(locationType, "ROOFTOP")) {
			exact = false;
		}
		if (types == null || !types.contains("street_address")) {
			exact = false;
		}

		GeocodingResult result = new GeocodingResult();
		result.setLat(lat);
		result.setLng(lng);
		result.setExact(exact);
		return Optional.of(result);
	}

	protected String mapLocaleToRegionTLD(Locale locale) {
		// This is not correct for all locales, as great britain would e.g. need "uk" instead of "en".
		// For german this is however currently not relevant:
		return locale.getLanguage();
	}

}
