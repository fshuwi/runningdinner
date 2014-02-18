package org.runningdinner.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerCalculatorTest;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.service.impl.SerializeToDirectoryHandler;
import org.runningdinner.test.util.TestUtil;

public class TestSerializeToDirHandler {

	private SerializeToDirectoryHandler handler = new SerializeToDirectoryHandler();
	private String tmpUploadDirectory = "/uploadDirTmp";

	@Before
	public void setUp() throws URISyntaxException {
		File file = TestUtil.getClasspathResourceAsFile(tmpUploadDirectory);
		handler.setTmpUploadDirectory(file.getAbsolutePath());
	}

	@Test
	public void testSerialization() throws IOException, URISyntaxException {
		assertEquals(0, getNumChildrenOfTmpUploadDir());
		String location = performSerialization();
		System.out.println(location);
		assertEquals(1, getNumChildrenOfTmpUploadDir());
		assertEquals("Expected location to have string-part with A_", true, location.indexOf("A_") >= 0);
	}

	protected String performSerialization() throws IOException {
		// Generate 18 test participants
		List<Participant> participants = RunningDinnerCalculatorTest.generateParticipants(18, 0);
		String location = handler.pushToTempLocation(participants, "A");
		return location;
	}

	@Test
	public void testDeSerialization() throws IOException, URISyntaxException, ConversionException {

		String location = performSerialization();

		List<Participant> participants = handler.popFromTempLocation(location);
		assertEquals(18, participants.size());
		for (int i = 0; i < participants.size(); i++) {
			assertEquals(i + 1, participants.get(i).getParticipantNumber());
		}
	}

	protected int getNumChildrenOfTmpUploadDir() throws URISyntaxException {
		File file = TestUtil.getClasspathResourceAsFile(tmpUploadDirectory);
		File[] children = file.listFiles();
		if (children == null || children.length == 0) {
			return 0;
		}
		return children.length;
	}

	@After
	public void tearDown() throws URISyntaxException {

		File file = TestUtil.getClasspathResourceAsFile(tmpUploadDirectory);

		File[] children = file.listFiles();
		if (children != null && children.length > 0) {
			for (File child : children) {
				child.delete();
			}
		}
	}
}
