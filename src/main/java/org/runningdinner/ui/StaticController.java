package org.runningdinner.ui;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Simple controller that just serves "static" (not application relevant) views
 * 
 * @author Clemens Stich
 * 
 */
@Controller
public class StaticController {

	protected MessageSource messages;

	@RequestMapping(value = RequestMappings.PRIVACY, method = RequestMethod.GET)
	public String showPrivacy(Model model, Locale locale) {
		addTitle(model, "label.privacy", locale);
		return getFullViewName("privacy");
	}

	@RequestMapping(value = RequestMappings.IMPRESSUM, method = RequestMethod.GET)
	public String showImpressum(Model model, Locale locale) {
		addTitle(model, "label.impressum", locale);
		return getFullViewName("impressum");
	}

	@RequestMapping(value = RequestMappings.ABOUT, method = RequestMethod.GET)
	public String showAbout(Model model, Locale locale) {
		addTitle(model, "label.about", locale);
		return getFullViewName("about");
	}

	protected String getFullViewName(final String viewName) {
		return "static/" + viewName;
	}

	protected void addTitle(Model model, String textKey, Locale locale) {
		String title = messages.getMessage(textKey, null, locale);
		if (StringUtils.isEmpty(title)) {
			title = "Untitled";
		}
		model.addAttribute("title", title);
	}

	@Autowired
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}

}
