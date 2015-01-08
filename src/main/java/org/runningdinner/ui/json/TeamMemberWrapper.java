package org.runningdinner.ui.json;

import org.runningdinner.core.ParticipantAddress;

/**
 * Wraps up the attributes of a team-member (participant) that is used for displaying the results in a JSON-AJAX request
 * 
 * @author i01002492
 * 
 */
public class TeamMemberWrapper {

	protected String fullname;
	protected String naturalKey;
	protected boolean host;

	protected int zip;
	
	protected String editLink;

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

	public String getEditLink() {
		return editLink;
	}

	public void setEditLink(String editLink) {
		this.editLink = editLink;
	}
	
	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}

	@Override
	public String toString() {
		return fullname;
	}

}