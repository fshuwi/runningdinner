package org.runningdinner.ui.json;

import org.apache.commons.lang.StringUtils;

/**
 * Indicates mainly the response status of a (JSON) request, which can be either successful (true) or unsuccessful (false).<br>
 * In case of an unsuccessful response the errorMessage contain some more detailed information.
 * 
 * @author i01002492
 * 
 */
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
