package org.runningdinner.event.listener;

import java.util.Map;

import org.runningdinner.events.SendDinnerRouteMailsFinishedEvent;
import org.runningdinner.model.DinnerRouteMailReport;
import org.runningdinner.service.CommunicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SendDinnerRouteMailsFinishedListener implements ApplicationListener<SendDinnerRouteMailsFinishedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendDinnerRouteMailsFinishedListener.class);

	protected CommunicationService communicationService;

	@Override
	public void onApplicationEvent(SendDinnerRouteMailsFinishedEvent event) {
		DinnerRouteMailReport dinnerRouteMailReport = event.getDinnerRouteMailReport();
		Map<String, Boolean> sendingResults = event.getSendingResults();

		LOGGER.info("Received dinner route mails finished event {}", dinnerRouteMailReport);

		dinnerRouteMailReport.applySendingFinished(sendingResults);

		communicationService.updateMailReport(dinnerRouteMailReport);
	}

	@Autowired
	public void setCommunicationService(CommunicationService communicationService) {
		this.communicationService = communicationService;
	}

}
