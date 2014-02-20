package org.runningdinner.test.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.runningdinner.core.Participant;
import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.core.RunningDinnerCalculatorTest;
import org.runningdinner.model.RunningDinnerInfo;
import org.runningdinner.ui.dto.CreateWizardModel;

public class TestUtil {

	public static final String SPRING_CONFIGURATION = "classpath:spring/app-context.xml, classpath:spring/mail-context.xml";

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
