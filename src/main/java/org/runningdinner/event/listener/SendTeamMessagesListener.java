package org.runningdinner.event.listener;

import org.runningdinner.events.SendTeamArrangementsEvent;
import org.runningdinner.service.email.MailQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SendTeamMessagesListener extends MailQueueAwareBaseListener implements ApplicationListener<SendTeamArrangementsEvent> {

	protected MailQueue mailQueue;

	private static final Logger LOGGER = LoggerFactory.getLogger(SendTeamMessagesListener.class);

	@Override
	public void onApplicationEvent(SendTeamArrangementsEvent event) {
		putEventToQueue(event);
	}

	@Override
	protected MailQueue getMailQueue() {
		return mailQueue;
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

	@Autowired
	public void setMailQueue(MailQueue mailQueue) {
		this.mailQueue = mailQueue;
	}

}
