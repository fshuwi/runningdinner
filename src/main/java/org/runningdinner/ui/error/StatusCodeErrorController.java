package org.runningdinner.ui.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StatusCodeErrorController {

	protected ErrorViewHelper errorViewHelper;

	@RequestMapping(value = "error404", method = RequestMethod.GET)
	public ModelAndView adminOverview() {
		ModelAndView result = new ModelAndView(getFullViewName("error404"));
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
