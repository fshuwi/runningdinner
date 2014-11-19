package org.runningdinner.jobs;

import java.util.Date;
import java.util.List;

import org.runningdinner.core.util.CoreUtil;
import org.runningdinner.model.BaseMailReport;
import org.runningdinner.service.CommunicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * This job scans the database for pending mail reports and deletes any found reports. The working of this job is quite heuristic as it
 * assumes that any mail sending job can not exceed the passed maxLifeTime. If so, such a report is marked as pending and deleted.
 * 
 * @author Clemens Stich
 * 
 */
public class DeletePendingMailReportsJob extends AbstractDeleteDbInstancesJob {

	private static Logger LOGGER = LoggerFactory.getLogger(DeletePendingMailReportsJob.class);
	
	protected CommunicationService communicationService;

	// Perform job each 3 hours
	@Scheduled(fixedRate = 60 * 1000 * 60 * 3)
	public void deleteOldDinnerInstances() {

		LOGGER.info("Check database for pending mail report instances with maxLifeTime = {} and timeUnit = {}", getMaxLifeTime(),
				getTimeUnit());
		if (getMaxLifeTime() < 0) {
			LOGGER.warn("maxLifeTime ({}) is negative. Nothing will be done.", getMaxLifeTime());
			return;
		}

		Date maxLifeTimeLimit = calculateMaxLifeTimeDateLimit();

		List<BaseMailReport> pendingReports = null;
		try {
			pendingReports = communicationService.findPendingMailReports(maxLifeTimeLimit);
		}
		catch (Exception ex) {
			LOGGER.error("Failed to retrieve pending reports to delete", ex);
			return;
		}

		if (CoreUtil.isEmpty(pendingReports)) {
			LOGGER.info("No pending mail reports found to be deleted");
			return;
		}

		for (BaseMailReport pendingReport : pendingReports) {
			LOGGER.info("Trying to delete pending report {}", pendingReport);
			try {
				communicationService.deleteMailReport(pendingReport);
				LOGGER.info("Pending Report {} successfully deleted", pendingReport);
			}
			catch (Exception ex) {
				LOGGER.error("Failed to delete pending report {}", pendingReport, ex);
			}
		}
	}
	
	public CommunicationService getCommunicationService() {
		return communicationService;
	}

	public void setCommunicationService(CommunicationService communicationService) {
		this.communicationService = communicationService;
	}

}
