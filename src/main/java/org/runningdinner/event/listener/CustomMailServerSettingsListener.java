package org.runningdinner.event.listener;

import org.runningdinner.events.BaseMailEvent;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.RunningDinnerPreference;
import org.runningdinner.model.RunningDinnerPreferences;
import org.runningdinner.repository.jpa.RunningDinnerRepositoryJpa;
import org.runningdinner.service.RunningDinnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Optional;

/**
 * Called whenever an event is fired regarding sending of mails
 * 
 * @author Clemens
 *
 */
@Component
public class CustomMailServerSettingsListener implements ApplicationListener<BaseMailEvent> {

	private RunningDinnerService runningDinnerService;

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void onApplicationEvent(BaseMailEvent event) {

		RunningDinner runningDinner = event.getRunningDinner();
		RunningDinnerPreferences preferences = runningDinnerService.loadPreferences(runningDinner);

		Optional<Boolean> useCustomMailServer = preferences.getBooleanValue(RunningDinnerPreferences.USE_CUSTOM_MAILSERVER);

		if (event.getCustomMailServerSettings().isPresent()) {
			// Change running dinner settings preference to use custom mail server

			if (useCustomMailServer.isPresent() && useCustomMailServer.get().booleanValue() == true) {
				// Performance: Don't update database if not necessary:
				return;
			}
			
			preferences.addPreference(RunningDinnerPreferences.USE_CUSTOM_MAILSERVER, String.valueOf(true));
		}
		else { // Change running dinner settings preference to use built-in mail server

			if (!useCustomMailServer.isPresent() || useCustomMailServer.get().booleanValue() == false) {
				// Performance: Don't update database if not necessary:
				return;
			}
			
			preferences.addPreference(RunningDinnerPreferences.USE_CUSTOM_MAILSERVER, String.valueOf(false));
		}
	}

	@Autowired
	public void setRunningDinnerService(RunningDinnerService runningDinnerService) {
		this.runningDinnerService = runningDinnerService;
	}

}
