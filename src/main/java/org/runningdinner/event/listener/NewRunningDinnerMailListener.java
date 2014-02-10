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
public class NewRunningDinnerMailListener extends MailQueueAwareBaseListener implements ApplicationListener<NewRunningDinnerEvent> {

	private MailQueue mailQueue;

	private static final Logger LOGGER = LoggerFactory.getLogger(NewRunningDinnerMailListener.class);

	@Override
	public void onApplicationEvent(NewRunningDinnerEvent event) {
		RunningDinner newRunningDinner = event.getNewRunningDinner();
		LOGGER.info("Received event for new running dinner {}", newRunningDinner);
		putEventToQueue(event);
	}

	@Autowired
	public void setMailQueue(MailQueue mailQueue) {
		this.mailQueue = mailQueue;
	}

	@Override
	protected MailQueue getMailQueue() {
		return mailQueue;
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

}
