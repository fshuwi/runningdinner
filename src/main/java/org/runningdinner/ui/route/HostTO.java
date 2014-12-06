package org.runningdinner.ui.route;

import java.util.List;

import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.model.GeocodingResult;

public class HostTO {

	protected String name;
	
	protected boolean onlyLastname;
	
	protected ParticipantAddress address;
	
	protected List<GeocodingResult> geocodes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOnlyLastname() {
		return onlyLastname;
	}

	public void setOnlyLastname(boolean onlyLastname) {
		this.onlyLastname = onlyLastname;
	}

	public ParticipantAddress getAddress() {
		return address;
	}

	public void setAddress(ParticipantAddress address) {
		this.address = address;
	}

	public List<GeocodingResult> getGeocodes() {
		return geocodes;
	}

	public void setGeocodes(List<GeocodingResult> geocodes) {
		this.geocodes = geocodes;
	}
	
}
