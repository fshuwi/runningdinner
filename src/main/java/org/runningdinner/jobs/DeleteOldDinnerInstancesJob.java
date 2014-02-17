package org.runningdinner.jobs;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.RunningDinnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Naive implementation of a job that scans the database for dinner instances that shall be deleted (because they are too old).<br>
 * TODo: Currently we use Spring's scheduling support... it would be reasonable to switch to Quartz in clustered environments as this job
 * should be only run once per application.
 * 
 * @author Clemens Stich
 * 
 */
public class DeleteOldDinnerInstancesJob {

	private TimeUnit timeUnit;

	private int maxLifeTime;

	private RunningDinnerService runningDinnerService;

	private static Logger LOGGER = LoggerFactory.getLogger(DeleteOldDinnerInstancesJob.class);

	// Perform job each 2 hours
	@Scheduled(fixedRate = 60 * 1000 * 60 * 2)
	public void deleteOldDinnerInstances() {

		LOGGER.info("Check database for dinner instances with maxLifeTime = {} and timeUnit = {}", maxLifeTime, timeUnit);
		if (maxLifeTime < 0) {
			LOGGER.warn("maxLifeTime ({}) is negative. Nothing will be done.", maxLifeTime);
		}

		Date creationDateLimit = calculateCreationDateLimit();

		List<RunningDinner> dinners = null;
		try {
			dinners = runningDinnerService.findDinnersWithEarlierCreationDate(creationDateLimit);
			LOGGER.info("Found {} dinners with creation date older as {}", dinners.size(),
					CoreUtil.getFormattedTime(creationDateLimit, CoreUtil.getDefaultDateFormat(), "Unknown"));
		}
		catch (Exception ex) {
			LOGGER.error("Failed to retrieve dinners to delete", ex);
			return;
		}

		for (RunningDinner dinner : dinners) {
			LOGGER.info("Trying to delete dinner {}", dinner.getUuid());
			try {
				runningDinnerService.deleteCompleteDinner(dinner);
				LOGGER.info("Dinner {} successfully deleted", dinner.getUuid());
			}
			catch (Exception ex) {
				LOGGER.error("Failed to delete dinner {}", dinner.getUuid(), ex);
			}
		}
	}

	private Date calculateCreationDateLimit() {
		DateTime now = DateTime.now();
		long lifeTimeMillis = TimeUnit.MILLISECONDS.convert(maxLifeTime, timeUnit);

		DateTime creationDateLimit = now.minus(lifeTimeMillis);
		return creationDateLimit.toDate();
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public void setTimeUnitAsString(final String timeUnitStr) {
		this.timeUnit = TimeUnit.valueOf(timeUnitStr);
	}

	public int getMaxLifeTime() {
		return maxLifeTime;
	}

	public void setMaxLifeTime(int maxLifeTime) {
		this.maxLifeTime = maxLifeTime;
	}

	public void setRunningDinnerService(RunningDinnerService runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

}
