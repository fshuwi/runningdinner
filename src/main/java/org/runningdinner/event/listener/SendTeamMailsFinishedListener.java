package org.runningdinner.event.listener;

import java.util.Map;

import org.runningdinner.events.SendTeamMailsFinishedEvent;
import org.runningdinner.model.TeamMailReport;
import org.runningdinner.service.CommunicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SendTeamMailsFinishedListener implements ApplicationListener<SendTeamMailsFinishedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendTeamMailsFinishedListener.class);

	protected CommunicationService communicationService;

	@Override
	public void onApplicationEvent(SendTeamMailsFinishedEvent event) {
		TeamMailReport teamMailReport = event.getTeamMailReport();
		Map<String, Boolean> sendingResults = event.getSendingResults();

		LOGGER.info("Received team arrangement mails finished event {}", teamMailReport);

		teamMailReport.applySendingFinished(sendingResults);

		communicationService.updateMailReport(teamMailReport);
	}

	@Autowired
	public void setCommunicationService(CommunicationService communicationService) {
		this.communicationService = communicationService;
	}

}
