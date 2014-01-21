package org.runningdinner.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.runningdinner.core.GenderAspect;
import org.runningdinner.core.NoPossibleRunningDinnerException;
import org.runningdinner.core.Participant;
import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.core.RunningDinnerCalculatorTest;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.core.Team;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.RunningDinnerInfo;
import org.runningdinner.service.impl.RunningDinnerServiceImpl;
import org.runningdinner.ui.dto.CreateWizardModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-context.xml" })
@ActiveProfiles("junit")
// Clear database after each test method is run... this is quite of an overhead, but sufficient for the samll test cases
// @Transactional for each test-method sucks
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TestRunningDinnerService {

	private static final String MY_TEST_UUID = "mydinner";
	private static final int NUM_PARTICIPANTS = 16;

	@Autowired
	private RunningDinnerServiceImpl runningDinnerService;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Test
	public void testCreateRunningDinner() {
		String newUuid = MY_TEST_UUID;
		Date now = new Date();
		RunningDinnerInfo info = createRunningDinnerInfo("title", now, "email@email.de", "Freiburg");

		RunningDinnerConfig runningDinnerConfig = RunningDinnerConfig.newConfigurer().build();

		List<Participant> participants = generateParticipants();

		RunningDinner result = runningDinnerService.createRunningDinner(info, runningDinnerConfig, participants, newUuid);
		assertEquals(newUuid, result.getUuid());
	}

	@Test
	public void testGetRunningDinnerByUuid() {
		testCreateRunningDinner();

		entityManagerFactory.getCache().evictAll();

		RunningDinner result = runningDinnerService.loadDinnerWithBasicDetails(MY_TEST_UUID);
		assertEquals("title", result.getTitle());
		assertEquals("email@email.de", result.getEmail());
		assertEquals(2, result.getConfiguration().getTeamSize());
		assertEquals(GenderAspect.IGNORE_GENDER, result.getConfiguration().getGenderAspects());
		assertEquals(true, result.getConfiguration().isForceEqualDistributedCapacityTeams());

		assertEquals(3, result.getConfiguration().getMealClasses().size());
	}

	@Test
	public void testGetRunningDinnerByUuidWithParticipants() {
		testCreateRunningDinner();

		entityManagerFactory.getCache().evictAll();

		RunningDinner result = runningDinnerService.loadDinnerWithParticipants(MY_TEST_UUID);
		assertEquals("title", result.getTitle());
		assertEquals("email@email.de", result.getEmail());
		assertEquals(2, result.getConfiguration().getTeamSize());

		List<Participant> participants = result.getParticipants();
		checkParticipants(participants);
	}

	@Test
	public void testGetParticipantsFromDinner() {
		testCreateRunningDinner();
		entityManagerFactory.getCache().evictAll();
		List<Participant> participants = runningDinnerService.loadAllParticipantsOfDinner(MY_TEST_UUID);
		checkParticipants(participants);
	}

	private void checkParticipants(List<Participant> participants) {
		assertEquals(NUM_PARTICIPANTS, participants.size());
		int cnt = 1;
		for (Participant p : participants) {
			assertEquals(cnt++, p.getParticipantNumber());
			assertEquals(12345, p.getAddress().getZip());
			assertEquals("MyStreet", p.getAddress().getStreet());
		}
	}

	@Test
	public void testGetNumberOfGeneratedTeamsWithNoTeams() {
		testCreateRunningDinner();
		entityManagerFactory.getCache().evictAll();
		int number = runningDinnerService.loadNumberOfTeamsForDinner(MY_TEST_UUID);
		assertEquals(0, number);
	}

	@Test
	public void testPersistGeneratedTeams() throws NoPossibleRunningDinnerException {
		testCreateRunningDinner();
		entityManagerFactory.getCache().evictAll();
		runningDinnerService.createTeamAndVisitationPlans(MY_TEST_UUID);

		entityManagerFactory.getCache().evictAll();

		// With 16 participants just 6 regular teams (with 2 members for each team) can be built up (->RunningDinnerCalculator)
		// 4 participants cannot be assigned to regular teams then
		assertEquals(6, runningDinnerService.loadNumberOfTeamsForDinner(MY_TEST_UUID));
	}

	@Test
	public void testGetTeamsForDinner() throws NoPossibleRunningDinnerException {
		testPersistGeneratedTeams();

		entityManagerFactory.getCache().evictAll();

		List<Team> teams = runningDinnerService.loadRegularTeamsFromDinner(MY_TEST_UUID);
		List<Participant> notAssignedParticipants = runningDinnerService.loadNotAssignableParticipantsOfDinner(MY_TEST_UUID);
		assertEquals(6, teams.size());
		assertEquals(4, notAssignedParticipants.size());

		// Check just order and that associations are resolved:
		int cnt = 1;
		for (Team team : teams) {
			assertEquals(cnt++, team.getTeamNumber());
			assertNotNull(team.getMealClass());
			assertEquals(2, team.getTeamMembers().size());
			// Check both team members for having successfully retrieved some persistent values:
			Iterator<Participant> tmpIter = team.getTeamMembers().iterator();
			assertEquals("MyStreet", tmpIter.next().getAddress().getStreet());
			assertEquals("MyStreet", tmpIter.next().getAddress().getStreet());

		}

		entityManagerFactory.getCache().evictAll();
	}

	@Test
	public void testGetVisitationPlans() throws NoPossibleRunningDinnerException {
		testPersistGeneratedTeams();

		entityManagerFactory.getCache().evictAll();

		List<Team> teamsWithArrangements = runningDinnerService.loadRegularTeamsWithVisitationPlanFromDinner(MY_TEST_UUID);
		for (Team team : teamsWithArrangements) {
			assertEquals(2, team.getVisitationPlan().getGuestTeams().size());
			assertEquals(2, team.getVisitationPlan().getHostTeams().size());
		}

	}

	private List<Participant> generateParticipants() {
		// Generate 20 dummy participants...
		List<Participant> generatedParticipants = RunningDinnerCalculatorTest.generateParticipants(NUM_PARTICIPANTS, 0);
		// ... and add Adresses:
		for (Participant generatedParticipant : generatedParticipants) {
			generatedParticipant.setAddress(ParticipantAddress.parseFromString("MyStreet 1\n12345 MyCity"));
		}

		return generatedParticipants;
	}

	private RunningDinnerInfo createRunningDinnerInfo(String title, Date date, String email, String city) {
		// Utilize that CreateWizardModel also implements RunningDinnerInfo:
		CreateWizardModel result = CreateWizardModel.newModelWithDefaults();
		result.setTitle(title);
		result.setCity(city);
		result.setEmail(email);
		result.setDate(date);
		return result;
	}

}
