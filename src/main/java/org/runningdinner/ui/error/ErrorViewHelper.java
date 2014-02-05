package org.runningdinner.ui.error;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

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

	public boolean isShowException() {
		return showException;
	}

	public void setShowException(boolean showException) {
		this.showException = showException;
	}

	public String getContactMail() {
		return contactMail;
	}

	public void setContactMail(String contactMail) {
		this.contactMail = contactMail;
	}

	public String getStartUrl() {
		return startUrl;
	}

	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}

}
