package org.runningdinner.jobs;

import java.io.File;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Naive implementation of a job that scans the temporary upload directory and deletes all files that exceed a certain age.
 * 
 * @author Clemens Stich
 * 
 */
public class DeleteTempUploadFilesJob {

	/**
	 * Where to scan
	 */
	@Value("${upload.application.tmpdir}")
	private String tmpUploadDirectory;

	/**
	 * How old is a file allowed to be (if its older as the limit (from now) then delete it)
	 */
	protected long maxLifeTimeMillis = 900000; // Default

	private static Logger LOGGER = LoggerFactory.getLogger(DeleteTempUploadFilesJob.class);

	// Perform Job Each 5 minutes
	@Scheduled(fixedRate = 60 * 1000 * 5)
	public void deleteOldFiles() {
		try {
			LOGGER.info("Scan {} for old temporary upload files with maxLifeTimeMillis = {}", tmpUploadDirectory, maxLifeTimeMillis);

			if (maxLifeTimeMillis < 0) {
				LOGGER.warn("Either maxLifeTimeLimit ({}) is negative or temporary upload directory ({}) is empty. Nothing will be done.",
						maxLifeTimeMillis, tmpUploadDirectory);
				return;
			}

			File uploadDirectory = new File(tmpUploadDirectory);
			DateTime now = DateTime.now();

			File[] children = uploadDirectory.listFiles();
			if (children != null && children.length > 0) {
				LOGGER.info("Found {} files in directory", children.length);
				for (File child : children) {
					if (checkForDelete(child, now)) {

						LOGGER.info("Trying to delete {} with modification date {}", child,
								new DateTime(child.lastModified()).toString("dd.MM.yyyy HH:mm:ss"));

						try {
							if (child.delete()) {
								LOGGER.info("{} successfully deleted", child);
							}
							else {
								LOGGER.error("{} could not be deleted", child);
							}
						}
						catch (Exception ex) {
							// Inner Catch block for deleting other files.. otherwise the first unsuccessful file would always "block" the
							// remaining files for being deleted
							LOGGER.error("Fatal error while trying to delete {}", child, ex);
						}
					}
				}
			}
			else {
				LOGGER.info("Found no files in directory");
			}
		}
		catch (Exception ex) {
			LOGGER.error("Fatal error while scanning {} for old uploaded files with maxLifeTimeMillis = {}", tmpUploadDirectory,
					maxLifeTimeMillis, ex);
		}
	}

	protected boolean checkForDelete(final File file, final DateTime now) {
		long lastModifiedMillis = file.lastModified(); // For these files modificationDate should equal to creationDate

		DateTime lastModifiedDate = new DateTime(lastModifiedMillis);
		DateTime lifeTimeLimit = now.minus(maxLifeTimeMillis);

		if (lastModifiedDate.isBefore(lifeTimeLimit)) {
			return true;
		}
		return false;
	}

	public String getTmpUploadDirectory() {
		return tmpUploadDirectory;
	}

	public void setTmpUploadDirectory(String tmpUploadDirectory) {
		this.tmpUploadDirectory = tmpUploadDirectory;
	}

	public long getMaxLifeTimeMillis() {
		return maxLifeTimeMillis;
	}

	public void setMaxLifeTimeMillis(long maxLifeTimeMillis) {
		this.maxLifeTimeMillis = maxLifeTimeMillis;
	}

}
