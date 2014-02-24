package org.runningdinner.event.listener;

import java.util.Map;

import org.runningdinner.events.SendDinnerRouteMailsFinishedEvent;
import org.runningdinner.model.DinnerRouteMailReport;
import org.runningdinner.service.RunningDinnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SendDinnerRouteMailsFinishedListener implements ApplicationListener<SendDinnerRouteMailsFinishedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendDinnerRouteMailsFinishedListener.class);

	protected RunningDinnerService runningDinnerService;

	@Override
	public void onApplicationEvent(SendDinnerRouteMailsFinishedEvent event) {
		DinnerRouteMailReport dinnerRouteMailReport = event.getDinnerRouteMailReport();
		Map<String, Boolean> sendingResults = event.getSendingResults();

		LOGGER.info("Received dinner route mails finished event {}", dinnerRouteMailReport);

		dinnerRouteMailReport.applySendingFinished(sendingResults);

		runningDinnerService.updateMailReport(dinnerRouteMailReport);
	}

	@Autowired
	public void setRunningDinnerService(RunningDinnerService runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

}
