package org.runningdinner.jobs;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.runningdinner.test.util.TestUtil;

public class TestDeleteTempUploadedFilesJob {

	private DeleteTempUploadFilesJob deleteFilesJob;

	// 0,6 s for testing
	private long MAX_LIFETIME_MILLIS = 600;

	private String tmpUploadDirectoryPath = "src/test/resources/uploadDirTmp";
	private File tmpUploadDirectory;

	@Before
	public void setUp() throws Exception {
		// Construct own job instance by hand (the scheduled job spring-instance doesn't perform any work -> config_junit.properties)
		// We want just to test functionality and not spring's scheduling:

		tmpUploadDirectory = new File(tmpUploadDirectoryPath);
		assertEquals(0, TestUtil.getNumChildren(tmpUploadDirectory));

		deleteFilesJob = new DeleteTempUploadFilesJob();
		deleteFilesJob.setTmpUploadDirectory(tmpUploadDirectory.getAbsolutePath());
		deleteFilesJob.setMaxLifeTimeMillis(MAX_LIFETIME_MILLIS);
	}

	@Test
	public void testFileDeletion() throws IOException, InterruptedException {

		File newFile1 = new File(tmpUploadDirectory.getAbsolutePath() + File.separator + "createdFile1.txt");
		File newFile2 = new File(tmpUploadDirectory.getAbsolutePath() + File.separator + "createdFile2.txt");
		newFile1.deleteOnExit();
		newFile2.deleteOnExit();

		// Create one file:
		assertEquals(true, newFile1.createNewFile());
		// Nothing should be deleted:
		deleteFilesJob.deleteOldFiles();
		assertEquals(true, newFile1.exists());

		// After lifetime limit is exceed re-run job and ensure that newFile1 is deleted
		Thread.sleep(MAX_LIFETIME_MILLIS + 200); // sleep is on some machines inaccurate.. add little offset to be sure to have enough time
		// ... but create a new file before and ensure that this one is not deleted:
		assertEquals(true, newFile2.createNewFile());
		deleteFilesJob.deleteOldFiles();
		assertEquals(false, newFile1.exists());
		assertEquals(true, newFile2.exists());

		// Exceed lifetime limit again... now both files should not exist any longer:
		Thread.sleep(MAX_LIFETIME_MILLIS + 200);// sleep is on some machines inaccurate.. add little offset to be sure to have enough time
		deleteFilesJob.deleteOldFiles();
		assertEquals(false, newFile1.exists());
		assertEquals(false, newFile2.exists());
		assertEquals(0, TestUtil.getNumChildren(tmpUploadDirectory));
	}

	@After
	public void tearDown() throws URISyntaxException {
		// This is a repetition of job logic... but anyway ensure an empty directory:
		TestUtil.deleteChildFiles(tmpUploadDirectory);
	}
}
