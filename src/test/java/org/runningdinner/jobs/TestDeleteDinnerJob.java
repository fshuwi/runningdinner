package org.runningdinner.jobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.runningdinner.core.NoPossibleRunningDinnerException;
import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.exceptions.DinnerNotFoundException;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.RunningDinnerInfo;
import org.runningdinner.service.RunningDinnerService;
import org.runningdinner.test.util.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-context.xml", "classpath:spring/mail-context.xml" })
@ActiveProfiles("junit")
// Clear database after each test method is run... this is quite of an overhead, but sufficient for the samll test cases
// @Transactional for each test-method sucks
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TestDeleteDinnerJob {

	private String MY_TEST_UUID = "test";

	// 1,5 s for testing
	// Code should be fast enough to perform all tests before dinner is deleted
	private long MAX_LIFETIME_MILLIS = 1500;

	@Autowired
	private RunningDinnerService runningDinnerService;

	private DeleteOldDinnerInstancesJob deleteDinnerJob;

	// Used for fine-granular db table checking:
	@Autowired
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	@Before
	public void setUp() throws Exception {
		// Construct own job instance by hand (the scheduled job spring-instance doesn't perform any work -> config_junit.properties)
		// We want just to test functionality and not spring's scheduling:
		deleteDinnerJob = new DeleteOldDinnerInstancesJob();
		deleteDinnerJob.setMaxLifeTime(MAX_LIFETIME_MILLIS);
		deleteDinnerJob.setTimeUnit(TimeUnit.MILLISECONDS);
		deleteDinnerJob.setRunningDinnerService(runningDinnerService);

		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	public void testDinnerDeletionJob() throws NoPossibleRunningDinnerException, InterruptedException {

		try {
			runningDinnerService.loadDinnerWithBasicDetails(MY_TEST_UUID);
			fail("Expected DinnerNotFoundException to be thrown");
		}
		catch (DinnerNotFoundException ex) {
			assertTrue(true);
		}

		RunningDinner dinner = createDinnerAndVisitationPlans();

		// This call should not delete anything as (hopefully :-)) 1,5 seconds have not been passed till now. Quite rough testing ;-)
		deleteDinnerJob.deleteOldDinnerInstances();

		assertEquals(dinner, runningDinnerService.loadDinnerWithBasicDetails(MY_TEST_UUID));

		// As we performed some stuff since creation, it is for sure enough to exactly sleep the life time period
		Thread.sleep(MAX_LIFETIME_MILLIS);

		// Re-run job => now dinner should be deleted
		deleteDinnerJob.deleteOldDinnerInstances();
		try {
			runningDinnerService.loadDinnerWithBasicDetails(MY_TEST_UUID);
			fail("Dinner should have been deleted by job");
		}
		catch (DinnerNotFoundException ex) {
			assertTrue(true);
		}

		// Perform further tests for checking that really all db-entites are deleted
		// Therefore we also automatically check the correct working of the delete method of runningDinnerService:
		checkDbTables();
	}

	private void checkDbTables() {
		assertEquals(0, getNumRowsInTable("MealClass"));
		assertEquals(0, getNumRowsInTable("RunningDinner"));
		assertEquals(0, getNumRowsInTable("Team"));
		assertEquals(0, getNumRowsInTable("Participant"));

		// Check Visitation-Plan Join-Tables:
		assertEquals(0, getNumRowsInTable("HostTeamMapping"));
		assertEquals(0, getNumRowsInTable("GuestTeamMapping"));
	}

	private int getNumRowsInTable(final String tablename) {
		return jdbcTemplate.queryForInt("SELECT distinct count(*) FROM " + tablename);
	}

	private RunningDinner createDinnerAndVisitationPlans() throws NoPossibleRunningDinnerException {
		Date now = new Date();
		RunningDinnerInfo info = TestUtil.createRunningDinnerInfo("title", now, "email@email.de", "Freiburg");
		RunningDinnerConfig runningDinnerConfig = RunningDinnerConfig.newConfigurer().build();

		List<Participant> participants = TestUtil.generateParticipants(18 + 1);

		RunningDinner dinner = runningDinnerService.createRunningDinner(info, runningDinnerConfig, participants, MY_TEST_UUID);
		runningDinnerService.createTeamAndVisitationPlans(MY_TEST_UUID);

		assertEquals(9, runningDinnerService.loadRegularTeamsFromDinner(MY_TEST_UUID).size());
		assertEquals(1, runningDinnerService.loadNotAssignableParticipantsOfDinner(MY_TEST_UUID).size());

		return dinner;
	}

}
