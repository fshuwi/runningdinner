package org.runningdinner.ui.rest;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorTO {

	private List<FieldErrorTO> fieldErrors = new ArrayList<>();

	public ValidationErrorTO() {
	}

	public void addFieldError(String path, String message) {
		FieldErrorTO error = new FieldErrorTO(path, message);
		fieldErrors.add(error);
	}

	public List<FieldErrorTO> getFieldErrors() {
		return fieldErrors;
	}

	public void setFieldErrors(List<FieldErrorTO> fieldErrors) {
		this.fieldErrors = fieldErrors;
	}

}
