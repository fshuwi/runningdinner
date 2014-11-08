package org.runningdinner.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.core.util.CoreUtil;
import org.runningdinner.event.publisher.EventPublisher;
import org.runningdinner.model.BaseMailReport;
import org.runningdinner.model.DinnerRouteMailReport;
import org.runningdinner.model.ParticipantMailReport;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.TeamMailReport;
import org.runningdinner.repository.jpa.RunningDinnerRepositoryJpa;
import org.runningdinner.service.CommunicationService;
import org.runningdinner.service.email.DinnerRouteMessageFormatter;
import org.runningdinner.service.email.MailServerSettings;
import org.runningdinner.service.email.ParticipantMessageFormatter;
import org.runningdinner.service.email.TeamArrangementMessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class CommunicationServiceImpl implements CommunicationService {

	// Spring managed dependencies
	private RunningDinnerRepositoryJpa repository;
	private EventPublisher eventPublisher;

	private static Logger LOGGER = LoggerFactory.getLogger(CommunicationServiceImpl.class);
	
	@Override
	@Transactional
	public int sendTeamMessages(String uuid, final List<String> teamKeys, final TeamArrangementMessageFormatter messageFormatter,
			final MailServerSettings customMailServerSettings) {

		LOGGER.info("Send team-arrangement email messages for {} teams for dinner {}", teamKeys.size(), uuid);

		Set<String> teamKeysAsSet = convertTeamOrParticipantKeysToSet(teamKeys);

		if (CoreUtil.isEmpty(teamKeysAsSet)) {
			LOGGER.warn("No teams passed for sending messages!");
			return 0;
		}

		final RunningDinner dinner = repository.findDinnerWithBasicDetailsByUuid(uuid);
		final List<Team> teams = repository.loadRegularTeamsFromDinnerByKeys(uuid, teamKeysAsSet);

		checkLoadedTeamOrParticipantSize(teams, teamKeysAsSet.size());

		final TeamMailReport teamMailStatusInfo = new TeamMailReport(dinner);
		teamMailStatusInfo.applyNewSending();
		repository.saveOrMerge(teamMailStatusInfo);

		// Publish event only after transaction is successfully committed:
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCommit() {
				eventPublisher.publishTeamMessages(dinner, teams, messageFormatter, teamMailStatusInfo, customMailServerSettings);
			}
		});

		return teams.size();
	}

	@Override
	@Transactional
	public int sendDinnerRouteMessages(final String uuid, final List<String> selectedTeamKeys,
			final DinnerRouteMessageFormatter dinnerRouteFormatter, final MailServerSettings customMailServerSettings) {

		LOGGER.info("Send final dinner-route email messages for {} teams for dinner {}", selectedTeamKeys.size(), uuid);

		Set<String> teamKeys = convertTeamOrParticipantKeysToSet(selectedTeamKeys);

		if (CoreUtil.isEmpty(teamKeys)) {
			LOGGER.warn("No teams passed for sending dinner route messages!");
			return 0;
		}

		final List<Team> teams = repository.loadTeamsWithVisitationPlan(teamKeys, true);
		final RunningDinner dinner = repository.findDinnerWithBasicDetailsByUuid(uuid);

		checkLoadedTeamOrParticipantSize(teams, teamKeys.size());

		final DinnerRouteMailReport dinnerRouteMailReport = new DinnerRouteMailReport(dinner);
		dinnerRouteMailReport.applyNewSending();
		repository.saveOrMerge(dinnerRouteMailReport);

		// Publish event only after transaction is successfully committed:
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCommit() {
				eventPublisher.publishDinnerRouteMessages(dinner, teams, dinnerRouteFormatter, dinnerRouteMailReport, customMailServerSettings);
			}
		});

		return teams.size();
	}

	@Override
	@Transactional
	public int sendParticipantMessages(String uuid, List<String> participantKeys, final ParticipantMessageFormatter participantFormatter,
			final MailServerSettings customMailServerSettings) {
		LOGGER.info("Send participant email messages for {} participants for dinner {}", participantKeys.size(), uuid);

		Set<String> participantKeysAsSet = convertTeamOrParticipantKeysToSet(participantKeys);

		if (CoreUtil.isEmpty(participantKeysAsSet)) {
			LOGGER.warn("No participants passed for sending messages!");
			return 0;
		}

		final RunningDinner dinner = repository.findDinnerByUuidWithParticipants(uuid);
		
		final List<Participant> participants = dinner.getParticipants(); // TODO: Only selected participants!
		checkLoadedTeamOrParticipantSize(participants, participantKeysAsSet.size());

		final ParticipantMailReport mailReport = new ParticipantMailReport(dinner);
		mailReport.applyNewSending();
		repository.saveOrMerge(mailReport);

		// Publish event only after transaction is successfully committed:
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCommit() {
				eventPublisher.publishParticipantMessages(dinner, participants, participantFormatter, mailReport, customMailServerSettings);
			}
		});

		return participants.size();
	}

	@Override
	@Transactional
	public BaseMailReport updateMailReport(final BaseMailReport mailReport) {
		LOGGER.info("Update Mail Report from {} with sending-status: {}",
				CoreUtil.getFormattedTime(mailReport.getSendingStartDate(), CoreUtil.getDefaultDateFormat(), "Unknown"),
				mailReport.isSending());
		return repository.saveOrMerge(mailReport);
	}

	@Override
	public TeamMailReport findLastTeamMailReport(final String dinnerUuid) {
		List<TeamMailReport> tmpResult = repository.findAllMailReportsForDinner(dinnerUuid, TeamMailReport.class);
		if (CoreUtil.isEmpty(tmpResult)) {
			return null;
		}
		return tmpResult.iterator().next();
	}

	@Override
	public DinnerRouteMailReport findLastDinnerRouteMailReport(String dinnerUuid) {
		List<DinnerRouteMailReport> tmpResult = repository.findAllMailReportsForDinner(dinnerUuid, DinnerRouteMailReport.class);
		if (CoreUtil.isEmpty(tmpResult)) {
			return null;
		}
		return tmpResult.iterator().next();
	}

	@Override
	public ParticipantMailReport findLastParticipantMailReport(final String dinnerUuid) {
		List<ParticipantMailReport> tmpResult = repository.findAllMailReportsForDinner(dinnerUuid, ParticipantMailReport.class);
		if (CoreUtil.isEmpty(tmpResult)) {
			return null;
		}
		return tmpResult.iterator().next();
	}

	@Override
	@Transactional
	public void deleteMailReport(BaseMailReport mailReport) {
		BaseMailReport mergedReport = repository.saveOrMerge(mailReport);
		repository.remove(mergedReport);
	}

	@Override
	public List<BaseMailReport> findPendingMailReports(final Date sendingStartDateLimit) {
		return repository.findPendingMailReports(sendingStartDateLimit);
	}

	/**
	 * Simple helper method for checking for duplicates in a passed team/participant key list
	 * 
	 * @param teamKeysList
	 * @return
	 */
	protected Set<String> convertTeamOrParticipantKeysToSet(final List<String> teamKeysList) {
		Set<String> teamKeys = new HashSet<String>(teamKeysList);
		if (teamKeys.size() != teamKeysList.size()) {
			throw new IllegalStateException("Passed team key list contained some duplicates!");
		}
		return teamKeys;
	}

	/**
	 * Asserts that the size of loaded teams and/or participants is the same as the passed expectedSize.<br>
	 * 
	 * @param loadedEntities
	 * @param expectedSize
	 */
	protected <T> void checkLoadedTeamOrParticipantSize(final List<T> loadedEntities, final int expectedSize) {
		if (CoreUtil.isEmpty(loadedEntities) && expectedSize > 0) {
			throw new IllegalStateException("No teams/participants available => Impossible to finalize and/or send messages");
		}
		if (loadedEntities.size() != expectedSize) {
			throw new IllegalStateException("Expected " + expectedSize + " teams/participants to be found, but there were "
					+ loadedEntities.size());
		}
	}

	@Autowired
	public void setRepository(RunningDinnerRepositoryJpa repository) {
		this.repository = repository;
	}

	@Autowired
	public void setEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

}
