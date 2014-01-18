package org.runningdinner.event.listener;

import org.runningdinner.events.NewRunningDinnerEvent;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.email.MailQueue;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class NewRunningDinnerListener implements ApplicationListener<NewRunningDinnerEvent> {

	private MailQueue mailQueue;

	@Override
	public void onApplicationEvent(NewRunningDinnerEvent event) {
		// try {
		// mailQueue.putToQueue(event);
		// }
		// catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		RunningDinner newRunningDinner = event.getNewRunningDinner();
		String email = newRunningDinner.getEmail();
		String uuid = newRunningDinner.getUuid(); // TODO: Admin-link...
		System.out.println("Received Event for sendining email to " + email + " with UUID " + uuid);
	}
}
