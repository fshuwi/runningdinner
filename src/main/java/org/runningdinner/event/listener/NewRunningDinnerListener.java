package org.runningdinner.event.listener;

import org.runningdinner.events.NewRunningDinnerEvent;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.email.MailQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class NewRunningDinnerListener implements ApplicationListener<NewRunningDinnerEvent> {

	private MailQueue mailQueue;

	private static final Logger LOGGER = LoggerFactory.getLogger(NewRunningDinnerListener.class);

	@Override
	public void onApplicationEvent(NewRunningDinnerEvent event) {
		RunningDinner newRunningDinner = event.getNewRunningDinner();
		LOGGER.info("Received event for new running dinner {}", newRunningDinner);

		try {
			if (mailQueue.putToQueue(event)) {
				LOGGER.info("Put event successfully to mailqueue");
			}
			else {
				LOGGER.error("Could not put event to mailqueue due to the queue is already closed");
			}
		}
		catch (InterruptedException e) {
			LOGGER.error("Could not put event to mailqueue because the activity was interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	@Autowired
	public void setMailQueue(MailQueue mailQueue) {
		this.mailQueue = mailQueue;
	}

}
