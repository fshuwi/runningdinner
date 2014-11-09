package org.runningdinner.service;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.RunningDinnerInfo;
import org.runningdinner.model.RunningDinnerPreferences;
import org.runningdinner.test.util.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TestRunningDinnerPreferences {

	@Autowired
	private RunningDinnerService runningDinnerService;

	@Test
	public void testEmptyPreferences() {
		String dinnerUuid = "uuid";

		RunningDinner dinner = TestUtil.createDefaultRunningDinner(runningDinnerService, dinnerUuid);

		RunningDinnerPreferences preferences = runningDinnerService.loadPreferences(dinner);
		assertEquals(0, preferences.getAllPreferences().size());
	}

	@Test
	public void testAddAndFindPreference() {
		String uuid = "uuid";
		RunningDinner runningDinner = TestUtil.createDefaultRunningDinner(runningDinnerService, uuid);

		// Load preferences
		RunningDinnerPreferences preferences = runningDinnerService.loadPreferences(runningDinner);
		assertEquals(0, preferences.getAllPreferences().size());

		preferences.addPreference("name", "value");

		RunningDinnerPreferences reloadedPreferences = runningDinnerService.loadPreferences(runningDinner);

		assertEquals(1, reloadedPreferences.getAllPreferences().size());
		assertEquals("value", preferences.getValue("name"));

		reloadedPreferences.addPreference("new", "newval");
		assertEquals("value", reloadedPreferences.getValue("name"));
		assertEquals("newval", reloadedPreferences.getValue("new"));
		reloadedPreferences = runningDinnerService.loadPreferences(runningDinner);
		assertEquals("value", reloadedPreferences.getValue("name"));
		assertEquals("newval", reloadedPreferences.getValue("new"));
	}

	@Test
	public void testOverwritePreference() {
		String uuid = "uuid";
		RunningDinner runningDinner = TestUtil.createDefaultRunningDinner(runningDinnerService, uuid);

		// Load preferences
		RunningDinnerPreferences preferences = runningDinnerService.loadPreferences(runningDinner);
		preferences.addPreference("name", "value");
		assertEquals("value", preferences.getValue("name"));

		// Modify preference
		preferences.addPreference("name", "update1");
		assertEquals("update1", preferences.getValue("name"));
		// Reload and check again
		preferences = runningDinnerService.loadPreferences(runningDinner);
		assertEquals("update1", preferences.getValue("name"));
	}

	@Test
	public void testSeveralDinnerPreferences() {
		String uuid1 = "uuid1";
		String uuid2 = "uuid2";
		RunningDinner dinner1 = TestUtil.createDefaultRunningDinner(runningDinnerService, uuid1);
		RunningDinner dinner2 = TestUtil.createDefaultRunningDinner(runningDinnerService, uuid2);

		RunningDinnerPreferences pref1 = runningDinnerService.loadPreferences(dinner1);
		RunningDinnerPreferences pref2 = runningDinnerService.loadPreferences(dinner2);

		pref1.addPreference("name", "dinner1");
		pref2.addPreference("name", "dinner2");
		assertEquals("dinner1", pref1.getValue("name"));
		assertEquals("dinner2", pref2.getValue("name"));

		// Reload
		pref1 = runningDinnerService.loadPreferences(dinner1);
		pref2 = runningDinnerService.loadPreferences(dinner2);
		assertEquals("dinner1", pref1.getValue("name"));
		assertEquals("dinner2", pref2.getValue("name"));
	}
}
