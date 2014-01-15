package org.runningdinner.ui;

import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.impl.RunningDinnerServiceImpl;
import org.runningdinner.ui.validator.AdminValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminController {

	public static final String ADMIN_URL_UUID_MARKER = "{uuid}";
	public static final String ADMIN_URL_PATTERN = "/event/" + ADMIN_URL_UUID_MARKER + "/admin";

	private MessageSource messages;
	private RunningDinnerServiceImpl runningDinnerService;
	private AdminValidator adminValidator;

	@RequestMapping(value = ADMIN_URL_PATTERN, method = RequestMethod.GET)
	public String adminOverview(@PathVariable(ADMIN_URL_UUID_MARKER) String uuid, Model model) {
		adminValidator.validateUuid(uuid);

		RunningDinner foundRunningDinner = runningDinnerService.findRunningDinner(uuid);
		model.addAttribute("runningDinner", foundRunningDinner);

		return getFullViewName("start");
	}

	protected String getFullViewName(final String viewName) {
		return "admin/" + viewName;
	}

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messages = messageSource;
	}

	@Autowired
	public void setRunningDinnerService(RunningDinnerServiceImpl runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

	@Autowired
	public void setAdminValidator(AdminValidator adminValidator) {
		this.adminValidator = adminValidator;
	}

}
