package org.runningdinner.model;

import java.io.Serializable;

public class ChangeTeamHost implements Serializable {

	private static final long serialVersionUID = 1L;

	private String teamKey;

	private String hostingParticipantKey;

	private String comment;

	private String modificationParticipantKey;
	
	private boolean sendMailToMe;

	public ChangeTeamHost(String teamKey, String hostingParticipantKey, String comment, String modificationParticipantKey, boolean sendMailToMe) {
		this.teamKey = teamKey;
		this.hostingParticipantKey = hostingParticipantKey;
		this.comment = comment;
		this.modificationParticipantKey = modificationParticipantKey;
		this.sendMailToMe = sendMailToMe;
	}

	public ChangeTeamHost() {
	}

	public String getTeamKey() {
		return teamKey;
	}

	public void setTeamKey(String teamKey) {
		this.teamKey = teamKey;
	}

	public String getHostingParticipantKey() {
		return hostingParticipantKey;
	}

	public void setHostingParticipantKey(String hostingParticipantKey) {
		this.hostingParticipantKey = hostingParticipantKey;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getModificationParticipantKey() {
		return modificationParticipantKey;
	}

	public void setModificationParticipantKey(String modificationParticipantKey) {
		this.modificationParticipantKey = modificationParticipantKey;
	}
	
	public boolean isSendMailToMe() {
		return sendMailToMe;
	}

	public void setSendMailToMe(boolean sendMailToMe) {
		this.sendMailToMe = sendMailToMe;
	}

	@Override
	public String toString() {
		return "teamKey=" + teamKey + ", hostingParticipantKey=" + hostingParticipantKey + ", comment=" + comment
				+ ", modificationParticipantKey=" + modificationParticipantKey;
	}

}
