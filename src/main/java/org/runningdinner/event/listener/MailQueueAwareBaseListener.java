package org.runningdinner.event.listener;

import org.runningdinner.service.email.MailQueue;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEvent;

/**
 * Abstract base class that handles concurrency staff when putting events to the MailQueue.
 * 
 * @author Clemens Stich
 * 
 */
public abstract class MailQueueAwareBaseListener {

	/**
	 * Retrieve the injected queue
	 * 
	 * @return
	 */
	protected abstract MailQueue getMailQueue();

	/**
	 * Retrieve the concrete logger of the inheriting class
	 * 
	 * @return
	 */
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
			// Save interruption state
			Thread.currentThread().interrupt();
		}
	}
}
