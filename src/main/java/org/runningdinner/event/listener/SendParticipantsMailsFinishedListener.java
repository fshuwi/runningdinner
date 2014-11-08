package org.runningdinner.event.listener;

import java.util.Map;

import org.runningdinner.events.SendParticipantMailsFinishedEvent;
import org.runningdinner.model.ParticipantMailReport;
import org.runningdinner.service.CommunicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SendParticipantsMailsFinishedListener implements ApplicationListener<SendParticipantMailsFinishedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendParticipantsMailsFinishedListener.class);

	protected CommunicationService communicationService;

	@Override
	public void onApplicationEvent(SendParticipantMailsFinishedEvent event) {
		ParticipantMailReport mailReport = event.getParticipantMailReport();
		Map<String, Boolean> sendingResults = event.getSendingResults();

		LOGGER.info("Received participant mails finished event {}", mailReport);

		mailReport.applySendingFinished(sendingResults);

		communicationService.updateMailReport(mailReport);
	}

	@Autowired
	public void setCommunicationService(CommunicationService communicationService) {
		this.communicationService = communicationService;
	}
}
