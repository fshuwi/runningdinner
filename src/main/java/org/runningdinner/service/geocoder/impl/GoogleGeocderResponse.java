package org.runningdinner.service.geocoder.impl;

import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleGeocderResponse {

	private String status;

	private List<GoogleGeocoderResult> results;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<GoogleGeocoderResult> getResults() {
		if (results == null) {
			return Collections.emptyList();
		}
		return results;
	}

	public void setResults(List<GoogleGeocoderResult> results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return status;
	}

}
