package org.runningdinner.ui.frontend.host;

import java.io.Serializable;

import org.runningdinner.ui.json.SingleTeamParticipantChange;

public class ChangeTeamHostTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private SingleTeamParticipantChange changedTeamHost;

	private String comment;

	private boolean sendMailToMe;

	public SingleTeamParticipantChange getChangedTeamHost() {
		return changedTeamHost;
	}

	public void setChangedTeamHost(SingleTeamParticipantChange changedTeamHost) {
		this.changedTeamHost = changedTeamHost;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isSendMailToMe() {
		return sendMailToMe;
	}

	public void setSendMailToMe(boolean sendMailToMe) {
		this.sendMailToMe = sendMailToMe;
	}

}
