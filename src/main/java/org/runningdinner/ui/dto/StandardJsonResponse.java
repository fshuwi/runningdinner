package org.runningdinner.ui.dto;

import org.apache.commons.lang.StringUtils;

public class StandardJsonResponse {

	protected boolean success;

	protected String errorMessage;

	public static StandardJsonResponse createSuccessResponse() {
		StandardJsonResponse result = new StandardJsonResponse();
		result.setSuccess(true);
		result.errorMessage = StringUtils.EMPTY;
		return result;
	}

	public static StandardJsonResponse createErrorResponse(final String errorMessage) {
		StandardJsonResponse result = new StandardJsonResponse();
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
