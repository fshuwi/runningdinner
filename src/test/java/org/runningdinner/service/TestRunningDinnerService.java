package org.runningdinner.service;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.runningdinner.core.GenderAspect;
import org.runningdinner.core.Participant;
import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.core.RunningDinnerCalculatorTest;
import org.runningdinner.core.RunningDinnerConfig;
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

	@Autowired
	private RunningDinnerServiceImpl runningDinnerService;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Test
	public void testCreateRunningDinner() {
		String newUuid = "mydinner";
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

		RunningDinner result = runningDinnerService.findRunningDinner("mydinner");
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

		RunningDinner result = runningDinnerService.findRunningDinnerWithParticipants("mydinner");
		assertEquals("title", result.getTitle());
		assertEquals("email@email.de", result.getEmail());
		assertEquals(2, result.getConfiguration().getTeamSize());

		List<Participant> participants = result.getParticipants();
		assertEquals(20, participants.size());

		int cnt = 1;
		for (Participant p : participants) {
			assertEquals(cnt++, p.getParticipantNumber());
			assertEquals(12345, p.getAddress().getZip());
			assertEquals("MyStreet", p.getAddress().getStreet());
		}
	}

	private List<Participant> generateParticipants() {
		// Generate 20 dummy participants...
		List<Participant> generatedParticipants = RunningDinnerCalculatorTest.generateParticipants(20, 0);
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
