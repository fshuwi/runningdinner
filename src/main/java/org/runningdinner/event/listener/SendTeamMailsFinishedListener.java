package org.runningdinner.event.listener;

import java.util.Map;

import org.runningdinner.events.SendTeamMailsFinishedEvent;
import org.runningdinner.model.TeamMailReport;
import org.runningdinner.service.RunningDinnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SendTeamMailsFinishedListener implements ApplicationListener<SendTeamMailsFinishedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendTeamMailsFinishedListener.class);

	protected RunningDinnerService runningDinnerService;

	@Override
	public void onApplicationEvent(SendTeamMailsFinishedEvent event) {
		TeamMailReport teamMailReport = event.getTeamMailReport();
		Map<String, Boolean> sendingResults = event.getSendingResults();

		LOGGER.info("Received team arrangement mails finished event {}", teamMailReport);

		teamMailReport.applySendingFinished(sendingResults);

		runningDinnerService.updateMailReport(teamMailReport);
	}

	@Autowired
	public void setRunningDinnerService(RunningDinnerService runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

}
