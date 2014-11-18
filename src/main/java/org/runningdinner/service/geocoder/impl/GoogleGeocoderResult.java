package org.runningdinner.service.geocoder.impl;

import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleGeocoderResult {
	
	private String formatted_address;
	
	private Geometry geometry;
	
	private List<String> types;

	public String getFormatted_address() {
		return formatted_address;
	}

	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public List<String> getTypes() {
		if (types==null) {
			return Collections.emptyList();
		}
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}
	
	
}
