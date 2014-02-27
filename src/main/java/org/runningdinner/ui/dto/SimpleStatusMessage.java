package org.runningdinner.ui.dto;

import java.io.Serializable;

/**
 * Used for displaying the result of a request (e.g. in page header)
 * 
 * @author Clemens Stich
 * 
 */
public class SimpleStatusMessage implements Serializable {

	private static final long serialVersionUID = 825053797057214647L;

	protected String status;
	protected String message;

	public static final String ERROR_STATUS = "danger";
	public static final String INFO_STATUS = "info";
	public static final String WARN_STATUS = "warning";
	public static final String SUCCESS_STATUS = "success";

	public SimpleStatusMessage() {
	}

	public SimpleStatusMessage(String status, String message) {
		this.status = status;
		this.message = message;
	}

	/**
	 * The status as string constant (used for display)
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * The message to display
	 * 
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "SimpleStatusMessage [status=" + status + ", message=" + message + "]";
	}

}
