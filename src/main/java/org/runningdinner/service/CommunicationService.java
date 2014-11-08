package org.runningdinner.service;

import java.util.Date;
import java.util.List;

import org.runningdinner.model.BaseMailReport;
import org.runningdinner.model.DinnerRouteMailReport;
import org.runningdinner.model.ParticipantMailReport;
import org.runningdinner.model.TeamMailReport;
import org.runningdinner.service.email.DinnerRouteMessageFormatter;
import org.runningdinner.service.email.MailServerSettings;
import org.runningdinner.service.email.ParticipantMessageFormatter;
import org.runningdinner.service.email.TeamArrangementMessageFormatter;

public interface CommunicationService {

	/**
	 * Send messages to the members of all teams identified by the passed teamKeys of a running dinner.<br>
	 * 
	 * @param uuid The parent running dinner
	 * @param teamKeys The natural keys of the teams to which all participants shall be sent messages
	 * @param messageFormatter A setup formatter that is used for replacing some template-variables inside a message
	 * @param customMailServerSettings If set to null => Use the standard running dinner mail-server. Otherwise try to send mails with the provided settings
	 * @return
	 */
	int sendTeamMessages(String uuid, List<String> teamKeys, TeamArrangementMessageFormatter messageFormatter, MailServerSettings customMailServerSettings);

	int sendDinnerRouteMessages(String uuid, List<String> teamKeys, DinnerRouteMessageFormatter dinnerRouteFormatter, MailServerSettings customMailServerSettings);

	int sendParticipantMessages(String uuid, List<String> participantKeys, ParticipantMessageFormatter participantFormatter, MailServerSettings customMailServerSettings);
	
	
	/**
	 * Persists the changes in the passed mail report.
	 * 
	 * @param mailReport
	 * @return
	 */
	BaseMailReport updateMailReport(BaseMailReport mailReport);

	/**
	 * Finds the last mail report about sending team arrangements for the dinner identified by the passed uuid.
	 * 
	 * @param dinnerUuid
	 * @return The found report or null if e.g. there was never sent a team arrangment mail
	 */
	TeamMailReport findLastTeamMailReport(final String dinnerUuid);

	/**
	 * Finds the last mail report about sending dinner-route messages for the dinner identified by the passed uuid.
	 * 
	 * @param dinnerUuid
	 * @return The found report or null if e.g. there was never sent a dinner route mail
	 */
	DinnerRouteMailReport findLastDinnerRouteMailReport(String dinnerUuid);

	/**
	 * Finds the last mail report about sending messages to (all) participants for the dinner identified by the passed uuid.
	 * 
	 * @param dinnerUuid
	 * @return The found report or null if e.g. there was never sent participant mails
	 */
	ParticipantMailReport findLastParticipantMailReport(final String dinnerUuid);

	/**
	 * Finds all mail reports that are pending. Pending means that a report is still in "sending"-state, but it seems to never complete (may
	 * e.g. happen if container is shutdown while there was an active sending mail task).
	 * 
	 * @param sendingStartDateLimit Every report (which is in sending state) that is older as this passed date is recognized as a pending
	 *            report
	 * @return
	 */
	List<BaseMailReport> findPendingMailReports(final Date sendingStartDateLimit);

	/**
	 * Deletes the passed mail report regardless it's state
	 * 
	 * @param mailReport
	 */
	void deleteMailReport(BaseMailReport mailReport);
	
}
