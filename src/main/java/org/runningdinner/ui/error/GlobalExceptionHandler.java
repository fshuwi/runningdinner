package org.runningdinner.ui.error;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * Responsible for dealing with all types of (runtime-)exceptions that are thrown in application.<br>
 * This the central point where all these exceptions get logged and where an appropriate error view is displayed.
 * 
 * @author Clemens Stich
 * 
 */
public class GlobalExceptionHandler extends SimpleMappingExceptionResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	protected ErrorViewHelper errorViewHelper;

	public GlobalExceptionHandler() {
		// Enable logging by providing the name of the logger to use
		setWarnLogCategory(GlobalExceptionHandler.class.getName());
	}

	@Override
	public String buildLogMessage(Exception e, HttpServletRequest req) {
		return "Fatal Error: " + e.getLocalizedMessage();
	}

	@Override
	protected void logException(Exception e, HttpServletRequest req) {
		// Don't use JCL, but SFL4J Logging:
		LOGGER.error(buildLogMessage(e, req), e);
	}

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
		// Call super method to get the ModelAndView (all needed information is already provided from there)
		ModelAndView mav = super.doResolveException(request, response, handler, exception);
		// Add additional information
		errorViewHelper.enrichModelAndView(mav);
		return mav;
	}

	@Autowired
	public void setErrorViewHelper(ErrorViewHelper errorViewHelper) {
		this.errorViewHelper = errorViewHelper;
	}

}
