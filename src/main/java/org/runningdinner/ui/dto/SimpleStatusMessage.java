package org.runningdinner.ui.dto;

public class SimpleStatusMessage {

	protected String status;
	protected String message;

	public static final String ERROR_STATUS = "danger";
	public static final String INFO_STATUS = "info";
	public static final String SUCCESS_STATUS = "success";

	public SimpleStatusMessage() {
	}

	public SimpleStatusMessage(String status, String message) {
		this.status = status;
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

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
