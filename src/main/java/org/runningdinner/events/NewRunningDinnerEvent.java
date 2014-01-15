package org.runningdinner.events;

import org.runningdinner.model.RunningDinner;
import org.springframework.context.ApplicationEvent;

public class NewRunningDinnerEvent extends ApplicationEvent {

	private static final long serialVersionUID = 2362167628097318302L;

	private RunningDinner newRunningDinner;

	public NewRunningDinnerEvent(final Object source, final RunningDinner newRunningDinner) {
		super(source);
		this.newRunningDinner = newRunningDinner;
	}

	public RunningDinner getNewRunningDinner() {
		return newRunningDinner;
	}

}
