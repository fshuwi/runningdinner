package org.runningdinner.ui.dto;

import org.apache.commons.lang.StringUtils;

public class TeamHostsChangeResponse {

	private boolean success;

	private String errorMessage;

	public static TeamHostsChangeResponse createSuccessResponse() {
		TeamHostsChangeResponse result = new TeamHostsChangeResponse();
		result.setSuccess(true);
		result.errorMessage = StringUtils.EMPTY;
		return result;
	}

	public static TeamHostsChangeResponse createErrorResponse(final String errorMessage) {
		TeamHostsChangeResponse result = new TeamHostsChangeResponse();
		result.setSuccess(false);
		result.errorMessage = errorMessage;
		return result;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
