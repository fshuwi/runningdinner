package org.runningdinner.jobs;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.runningdinner.service.RunningDinnerService;

/**
 * Base class of jobs that perform database operations. Contains some common properties and helper methods.
 * 
 * @author Clemens Stich
 * 
 */
public class AbstractDeleteDbInstancesJob {

	protected TimeUnit timeUnit;

	protected long maxLifeTime;

	protected RunningDinnerService runningDinnerService;

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public void setTimeUnitAsString(final String timeUnitStr) {
		this.timeUnit = TimeUnit.valueOf(timeUnitStr.trim());
	}

	public long getMaxLifeTime() {
		return maxLifeTime;
	}

	public void setMaxLifeTime(long maxLifeTime) {
		this.maxLifeTime = maxLifeTime;
	}

	public void setRunningDinnerService(RunningDinnerService runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

	public RunningDinnerService getRunningDinnerService() {
		return runningDinnerService;
	}

	protected Date calculateMaxLifeTimeDateLimit() {
		DateTime now = DateTime.now();
		long lifeTimeMillis = TimeUnit.MILLISECONDS.convert(maxLifeTime, timeUnit);

		DateTime result = now.minus(lifeTimeMillis);
		return result.toDate();
	}
}
