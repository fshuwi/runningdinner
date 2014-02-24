package org.runningdinner.test.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.runningdinner.core.NoPossibleRunningDinnerException;
import org.runningdinner.core.Participant;
import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.core.RunningDinnerCalculatorTest;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.RunningDinnerInfo;
import org.runningdinner.service.RunningDinnerService;
import org.runningdinner.ui.dto.CreateWizardModel;

public class TestUtil {

	public static final String APP_CONTEXT = "classpath:spring/app-context.xml";
	public static final String MAIL_CONTEXT = "classpath:spring/mail-context.xml";

	public static RunningDinnerInfo createRunningDinnerInfo(String title, Date date, String email, String city) {
		// Utilize that CreateWizardModel also implements RunningDinnerInfo:
		CreateWizardModel result = CreateWizardModel.newModelWithDefaultSettings();
		result.setTitle(title);
		result.setCity(city);
		result.setEmail(email);
		result.setDate(date);
		return result;
	}

	/**
	 * Generates #numParticipants participants
	 * 
	 * @return
	 */
	public static List<Participant> generateParticipants(int numParticipants) {
		// Generate 22 dummy participants...
		List<Participant> generatedParticipants = RunningDinnerCalculatorTest.generateParticipants(numParticipants, 0);
		// ... and add Adresses:
		for (Participant generatedParticipant : generatedParticipants) {
			generatedParticipant.setAddress(ParticipantAddress.parseFromString("MyStreet 1\n12345 MyCity"));
		}

		return generatedParticipants;
	}

	/**
	 * Creates a complete running dinner instance and also arranges also random teams out of the participants
	 * 
	 * @param runningDinnerService
	 * @param uuid
	 * @return
	 * @throws NoPossibleRunningDinnerException
	 */
	public static RunningDinner createExampleDinnerAndVisitationPlans(RunningDinnerService runningDinnerService, String uuid)
			throws NoPossibleRunningDinnerException {
		Date now = new Date();
		RunningDinnerInfo info = TestUtil.createRunningDinnerInfo("title", now, "email@email.de", "Freiburg");
		RunningDinnerConfig runningDinnerConfig = RunningDinnerConfig.newConfigurer().build();

		List<Participant> participants = TestUtil.generateParticipants(18 + 1);

		RunningDinner dinner = runningDinnerService.createRunningDinner(info, runningDinnerConfig, participants, uuid);
		runningDinnerService.createTeamAndVisitationPlans(uuid);

		assertEquals(9, runningDinnerService.loadRegularTeamsFromDinner(uuid).size());
		assertEquals(1, runningDinnerService.loadNotAssignableParticipantsOfDinner(uuid).size());

		return dinner;
	}

	public static File getClasspathResourceAsFile(final String path) throws URISyntaxException {
		URL tmpUrl = TestUtil.class.getResource(path);
		File file = new File(tmpUrl.toURI());
		return file;
	}

	public static void deleteChildFiles(File directory) {
		File[] children = directory.listFiles();
		if (children != null && children.length > 0) {
			for (File child : children) {
				child.delete();
			}
		}
	}

	public static int getNumChildren(File directory) {
		File[] tmp = directory.listFiles();
		if (tmp == null || tmp.length == 0) {
			return 0;
		}
		return tmp.length;
	}

}
