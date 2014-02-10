package org.runningdinner.event.listener;

import org.runningdinner.events.NewRunningDinnerEvent;
import org.runningdinner.model.RunningDinner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Primitive mechanism for tracking the usage of running dinners: This class uses a special configured logger to track each generated
 * running dinner. Should however be sufficient for now in a first go.
 * 
 * @author Clemens Stich
 * 
 */
@Component
public class TrackRunningDinnerListener implements ApplicationListener<NewRunningDinnerEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TrackRunningDinnerListener.class);

	@Override
	public void onApplicationEvent(NewRunningDinnerEvent event) {
		RunningDinner newRunningDinner = event.getNewRunningDinner();
		LOGGER.info("Created {} for email {}", newRunningDinner.getUuid(), newRunningDinner.getEmail());
	}

}
