package org.runningdinner.ui.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * As Spring's exception handler is not capable of dealing with HTTP error status codes we provide our own controller to do so.<br>
 * The status codes are mapped from web.xml to this view's request methods.
 * 
 * @author Clemens Stich
 * 
 */
@Controller
public class StatusCodeErrorController {

	protected ErrorViewHelper errorViewHelper;

	@RequestMapping(value = "error404", method = RequestMethod.GET)
	public ModelAndView error404() {
		ModelAndView result = new ModelAndView(getFullViewName("error404"));
		errorViewHelper.enrichModelAndView(result);
		return result;
	}

	@RequestMapping(value = "error500", method = RequestMethod.GET)
	public ModelAndView error500() {
		ModelAndView result = new ModelAndView(getFullViewName("errorDefault"));
		errorViewHelper.enrichModelAndView(result);
		return result;
	}

	protected String getFullViewName(final String viewName) {
		return "error/" + viewName;
	}

	@Autowired
	public void setErrorViewHelper(ErrorViewHelper errorViewHelper) {
		this.errorViewHelper = errorViewHelper;
	}

}
