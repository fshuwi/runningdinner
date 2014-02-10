package org.runningdinner.exceptions;

/**
 * Thrown if there is tried to load an (admin area) page with a UUID that is not valid
 * 
 * @author Clemens Stich
 * 
 */
public class InvalidUuidException extends RuntimeException {

	private static final long serialVersionUID = 1246102332165640703L;

	public InvalidUuidException() {
		super();
	}

	public InvalidUuidException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidUuidException(String arg0) {
		super(arg0);
	}

	public InvalidUuidException(Throwable arg0) {
		super(arg0);
	}

}
