package org.runningdinner.ui;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.service.impl.RunningDinnerServiceImpl;
import org.springframework.context.MessageSource;

/**
 * Contains some helper methods that are common for all controller classes
 * 
 * @author i01002492
 * 
 */
public abstract class AbstractBaseController {

	/**
	 * Put passed participants into request-attribute, don't use session as this list might be quite long.<br>
	 * Furthermore all not assignable participants (based upon the passed dinner-configuration) are calculated and according status
	 * attributes are set.<br>
	 * TODO: Use paging in future
	 * 
	 * @param request
	 * @param participants
	 * @param runningDinnerConfig
	 * @param locale
	 */
	protected void setParticipantListViewAttributes(HttpServletRequest request, List<Participant> participants,
			RunningDinnerConfig runningDinnerConfig, Locale locale) {

		// All participants for display in table:
		request.setAttribute("participants", participants);

		// Check whether all participants can be used for creating the dinner or not:
		List<Participant> notAssignableParticipants = getRunningDinnerService().calculateNotAssignableParticipants(runningDinnerConfig,
				participants);
		request.setAttribute("notAssignableParticipants", notAssignableParticipants);

		if (notAssignableParticipants.size() == 0) {
			// Every participant can be assigned into a team
			request.setAttribute("participantStatus", "success");
			request.setAttribute("participantStatusMessage",
					getMessageSource().getMessage("text.participant.preview.success", null, locale));
		}
		else if (notAssignableParticipants.size() == participants.size()) {
			// Too few participants for assigning them into valid team combinations
			request.setAttribute("participantStatus", "danger");
			request.setAttribute("participantStatusMessage", getMessageSource().getMessage("text.participant.preview.error", null, locale));
		}
		else {
			// Not every participant can successfuly be assigned into a team
			request.setAttribute("participantStatus", "warning");
			request.setAttribute("participantStatusMessage",
					getMessageSource().getMessage("text.participant.preview.warning", null, locale));
		}

	}

	protected abstract MessageSource getMessageSource();

	protected abstract RunningDinnerServiceImpl getRunningDinnerService();
}
