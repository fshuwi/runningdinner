package org.runningdinner.event.listener;

import org.runningdinner.events.NewRunningDinnerEvent;
import org.runningdinner.model.RunningDinner;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class NewRunningDinnerListener implements ApplicationListener<NewRunningDinnerEvent> {

	@Override
	public void onApplicationEvent(NewRunningDinnerEvent event) {
		RunningDinner newRunningDinner = event.getNewRunningDinner();
		String email = newRunningDinner.getEmail();
		String uuid = newRunningDinner.getUuid(); // TODO: Admin-link...
		// SADF
		System.out.println("Received Event for sendining email to " + email + " with UUID " + uuid);
	}
}
