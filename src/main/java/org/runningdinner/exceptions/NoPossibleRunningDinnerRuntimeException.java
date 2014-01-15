package org.runningdinner.exceptions;

import org.runningdinner.core.NoPossibleRunningDinnerException;

public class NoPossibleRunningDinnerRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -6262298967766257149L;

	private NoPossibleRunningDinnerException wrappedException;

	public NoPossibleRunningDinnerRuntimeException(NoPossibleRunningDinnerException wrappedException) {
		this.wrappedException = wrappedException;
	}

	public NoPossibleRunningDinnerException getWrappedException() {
		return wrappedException;
	}

}
