package org.runningdinner.ui;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.runningdinner.ui.dto.SimpleStatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Simple controller that just serves "static" (not application relevant) views
 * 
 * @author Clemens Stich
 * 
 */
@Controller
public class StaticController {

	protected MessageSource messages;

	private static Logger LOGGER = LoggerFactory.getLogger(StaticController.class);

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

	@RequestMapping(value = "/simpleflash", method = RequestMethod.GET)
	public String testGetSimpleFlash(Model model, final RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("simpleMessage", "Hello World!");
		return "redirect:/simpleflash-redirect";
	}

	@RequestMapping(value = "/simpleflash-redirect", method = RequestMethod.GET)
	public String testRedirectedSimpleFlash(HttpServletRequest request, Model model, final RedirectAttributes redirectAttributes) {
		// *** Test for OPENSHIFT *** //
		Map<String, ?> map = RequestContextUtils.getInputFlashMap(request);
		LOGGER.warn("Receive flash map with {} elements", (map != null ? map.size() : 0));
		if (map != null) {
			Object simpleMessage = map.get("simpleMessage");
			LOGGER.warn("Received statusMessage attribute {}", simpleMessage);
		}
		// *** END Test for Openshift *** //
		return getFullViewName("redirectview_simple");
	}

	@RequestMapping(value = "/simpleflashform", method = RequestMethod.GET)
	public String testGetSimpleFlashForm(Model model) {
		return getFullViewName("simpleflashform");
	}

	@RequestMapping(value = "/simpleflashform", method = RequestMethod.POST)
	public String testPostSimpleFlashForm(Model model, final RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("simpleMessage", "Hello World!");
		return "redirect:/simpleflash-redirect";
	}

	@RequestMapping(value = "/statusflashform", method = RequestMethod.GET)
	public String testGetStatusFlashForm(Model model) {
		return getFullViewName("simpleflashform");
	}

	@RequestMapping(value = "/statusflashform", method = RequestMethod.POST)
	public String testPostStatusFlashForm(Model model, final RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("statusMessage", new SimpleStatusMessage("danger", "My Status Message"));
		return "redirect:/statusflash-redirect";
	}

	@RequestMapping(value = "/statusflash-redirect", method = RequestMethod.GET)
	public String testRedirectedStatusFlash(HttpServletRequest request, Model model, final RedirectAttributes redirectAttributes) {
		// *** Test for OPENSHIFT *** //
		Map<String, ?> map = RequestContextUtils.getInputFlashMap(request);
		LOGGER.warn("Receive flash map with {} elements", (map != null ? map.size() : 0));
		if (map != null) {
			Object statusMessage = map.get("statusMessage");
			LOGGER.warn("Received statusMessage attribute {}", statusMessage);
		}
		// *** END Test for Openshift *** //
		return getFullViewName("redirectview_status");
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
