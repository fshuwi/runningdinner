package org.runningdinner.event.listener;

import org.runningdinner.events.SendDinnerRoutesEvent;
import org.runningdinner.service.email.MailQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SendDinnerRoutesListener implements ApplicationListener<SendDinnerRoutesEvent> {

	protected static final Logger LOGGER = LoggerFactory.getLogger(SendDinnerRoutesListener.class);

	protected MailQueue mailQueue;

	@Override
	public void onApplicationEvent(SendDinnerRoutesEvent event) {
		// TODO: Code duplication!

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

	public MailQueue getMailQueue() {
		return mailQueue;
	}

	@Autowired
	public void setMailQueue(MailQueue mailQueue) {
		this.mailQueue = mailQueue;
	}

}
