package org.runningdinner.ui.json;

/**
 * Wraps up the attributes of a team-member (participant) that is used for displaying the results in a JSON-AJaX reqest
 * 
 * @author i01002492
 * 
 */
public class TeamMemberWrapper {

	protected String fullname;
	protected String naturalKey;
	protected boolean host;

	public TeamMemberWrapper() {
	}

	public TeamMemberWrapper(String fullname, String naturalKey, boolean host) {
		this.fullname = fullname;
		this.naturalKey = naturalKey;
		this.host = host;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getNaturalKey() {
		return naturalKey;
	}

	public void setNaturalKey(String naturalKey) {
		this.naturalKey = naturalKey;
	}

	public boolean isHost() {
		return host;
	}

	public void setHost(boolean host) {
		this.host = host;
	}

	@Override
	public String toString() {
		return fullname;
	}
}