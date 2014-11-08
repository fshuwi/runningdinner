package org.runningdinner.events;

import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.email.MailServerSettings;
import org.springframework.context.ApplicationEvent;

import com.google.common.base.Optional;

public abstract class BaseMailEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	protected Optional<MailServerSettings> customMailServerSettings = Optional.absent();

	protected RunningDinner runningDinner;

	public BaseMailEvent(Object source, RunningDinner dinner, MailServerSettings customMailServerSettings) {
		super(source);
		if (customMailServerSettings != null) {
			this.customMailServerSettings = Optional.of(customMailServerSettings);
		}
		this.runningDinner = dinner;
	}

	public Optional<MailServerSettings> getCustomMailServerSettings() {
		return customMailServerSettings;
	}

	public RunningDinner getRunningDinner() {
		return runningDinner;
	}

}
