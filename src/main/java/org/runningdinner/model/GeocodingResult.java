package org.runningdinner.model;

public class GeocodingResult {

	protected double lat;

	protected double lng;

	protected boolean exact;

	protected String formattedAddress;

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public boolean isExact() {
		return exact;
	}

	public void setExact(boolean exact) {
		this.exact = exact;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	@Override
	public String toString() {
		return "(" + lat + ", " + lng + ", " + exact + ")";
	}

}
