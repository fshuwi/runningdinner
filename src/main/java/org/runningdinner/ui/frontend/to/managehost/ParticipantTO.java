package org.runningdinner.ui.frontend.to.managehost;

import java.io.Serializable;

import org.runningdinner.core.Participant;

public class ParticipantTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String naturalKey;

	private boolean host;

	public ParticipantTO(final Participant participant) {
		this.name = participant.getName().getFullnameFirstnameFirst();
		this.naturalKey = participant.getNaturalKey();
		this.host = participant.isHost();
	}

	public ParticipantTO() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return name;
	}

}
