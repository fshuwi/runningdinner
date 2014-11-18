package org.runningdinner.exceptions;

public class GeocodingException extends Exception {

	private static final long serialVersionUID = -2249502680099549784L;

	public GeocodingException() {
		super();
	}

	public GeocodingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public GeocodingException(String arg0) {
		super(arg0);
	}

	public GeocodingException(Throwable arg0) {
		super(arg0);
	}
}
