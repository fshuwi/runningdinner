package org.runningdinner.event.listener;

import org.runningdinner.service.email.MailQueue;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEvent;

public abstract class MailQueueAwareBaseListener {

	protected abstract MailQueue getMailQueue();

	protected abstract Logger getLogger();

	public void putEventToQueue(final ApplicationEvent applicationEvent) {
		final MailQueue injectedMailQueue = getMailQueue();

		try {
			if (injectedMailQueue.putToQueue(applicationEvent)) {
				getLogger().info("Put event successfully to mailqueue");
			}
			else {
				getLogger().error("Could not put event to mailqueue due to the queue is already closed");
			}
		}
		catch (InterruptedException e) {
			getLogger().error("Could not put event to mailqueue because the activity was interrupted", e);
			Thread.currentThread().interrupt();
		}
	}
}
