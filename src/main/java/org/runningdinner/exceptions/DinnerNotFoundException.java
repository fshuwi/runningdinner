package org.runningdinner.exceptions;

/**
 * Thrown if a user tries to load the admin area of a running dinner which does not exist (any more)
 * 
 * @author Clemens Stich
 * 
 */
public class DinnerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1347609946273869803L;

	public DinnerNotFoundException() {
		super();
	}

	public DinnerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public DinnerNotFoundException(String message) {
		super(message);
	}

	public DinnerNotFoundException(Throwable cause) {
		super(cause);
	}

}
