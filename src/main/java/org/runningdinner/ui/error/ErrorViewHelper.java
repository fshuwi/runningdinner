package org.runningdinner.ui.error;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

/**
 * Used in Exception-Handlers for adding additional information to the view
 * 
 * @author Clemens Stich
 * 
 */
@Component
public class ErrorViewHelper {

	protected boolean showException = true; // TODO => Property

	@Value("${contact.mail}")
	protected String contactMail;

	@Value("${host.context.url}")
	protected String startUrl;

	public void enrichModelAndView(final ModelAndView mav) {
		mav.addObject("timestamp", new Date());
		mav.addObject("startUrl", startUrl);
		mav.addObject("contactMail", contactMail);
		mav.addObject("showException", showException);
	}

	/**
	 * Display Exception stacktrace in error view?
	 * 
	 * @return
	 */
	public boolean isShowException() {
		return showException;
	}

	public void setShowException(boolean showException) {
		this.showException = showException;
	}

	/**
	 * Contact mail for problems
	 * 
	 * @return
	 */
	public String getContactMail() {
		return contactMail;
	}

	public void setContactMail(String contactMail) {
		this.contactMail = contactMail;
	}

	/**
	 * Landing page of application
	 * 
	 * @return
	 */
	public String getStartUrl() {
		return startUrl;
	}

	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}

}
