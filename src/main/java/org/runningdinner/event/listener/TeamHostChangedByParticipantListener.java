package org.runningdinner.event.listener;

import org.runningdinner.events.TeamHostChangedByParticipantEvent;
import org.runningdinner.service.email.MailQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class TeamHostChangedByParticipantListener extends MailQueueAwareBaseListener implements
		ApplicationListener<TeamHostChangedByParticipantEvent> {

	protected static final Logger LOGGER = LoggerFactory.getLogger(TeamHostChangedByParticipantListener.class);

	protected MailQueue mailQueue;

	@Override
	public void onApplicationEvent(final TeamHostChangedByParticipantEvent event) {
		putEventToQueue(event);
	}

	@Override
	protected MailQueue getMailQueue() {
		return mailQueue;
	}

	@Autowired
	public void setMailQueue(MailQueue mailQueue) {
		this.mailQueue = mailQueue;
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

}
