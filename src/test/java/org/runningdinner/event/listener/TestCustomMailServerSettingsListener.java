package org.runningdinner.event.listener;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.runningdinner.core.Team;
import org.runningdinner.events.SendDinnerRoutesEvent;
import org.runningdinner.model.DinnerRouteMailReport;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.RunningDinnerPreferences;
import org.runningdinner.service.RunningDinnerService;
import org.runningdinner.service.email.DinnerRouteMessageFormatter;
import org.runningdinner.service.email.MailServerSettings;
import org.runningdinner.test.util.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { TestUtil.APP_CONTEXT, TestUtil.MAIL_CONTEXT })
@ActiveProfiles("junit")
// Clear database after each test method is run... this is quite of an overhead, but sufficient for the samll test cases
// @Transactional for each test-method sucks
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TestCustomMailServerSettingsListener {

	private CustomMailServerSettingsListener listener;

	@Autowired
	private RunningDinnerService runningDinnerService;

	@Autowired
	private MessageSource messageSource;

	RunningDinner dinner;

	@Test
	public void testUseCustomMailServerSettings() {

		// Don't know why, but I must manually create event listener for testing... autowiring doesn't work!
		this.listener = new CustomMailServerSettingsListener();
		this.listener.setRunningDinnerService(runningDinnerService);

		dinner = TestUtil.createDefaultRunningDinner(runningDinnerService, "uuid");

		RunningDinnerPreferences preferences = runningDinnerService.loadPreferences(dinner);
		assertEquals(false, preferences.getBooleanValue(RunningDinnerPreferences.USE_CUSTOM_MAILSERVER).isPresent());

		MailServerSettings customMailServerSettings = TestUtil.generateTestableMailServerSettings();
		fireEvent(customMailServerSettings);

		preferences = runningDinnerService.loadPreferences(dinner);
		assertEquals(true, preferences.getBooleanValue(RunningDinnerPreferences.USE_CUSTOM_MAILSERVER).isPresent());
		assertEquals(true, preferences.getBooleanValue(RunningDinnerPreferences.USE_CUSTOM_MAILSERVER).get().booleanValue());

		// Disable custom mail server settings again:
		fireEvent(null);
		preferences = runningDinnerService.loadPreferences(dinner);
		assertEquals(true, preferences.getBooleanValue(RunningDinnerPreferences.USE_CUSTOM_MAILSERVER).isPresent());
		assertEquals(false, preferences.getBooleanValue(RunningDinnerPreferences.USE_CUSTOM_MAILSERVER).get().booleanValue());
	}

	void fireEvent(MailServerSettings customMailServerSettings) {
		// Prepare test data
		DinnerRouteMailReport dinnerRouteMailReport = new DinnerRouteMailReport(dinner);
		DinnerRouteMessageFormatter dinnerRouteMessageFormatter = new DinnerRouteMessageFormatter(messageSource, Locale.GERMAN);
		SendDinnerRoutesEvent event = new SendDinnerRoutesEvent(this, dinner, Collections.<Team> emptyList(), dinnerRouteMessageFormatter,
				dinnerRouteMailReport, customMailServerSettings);

		listener.onApplicationEvent(event);
	}

}
