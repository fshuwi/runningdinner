package org.runningdinner.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.FuzzyBoolean;
import org.runningdinner.core.GeneratedTeamsResult;
import org.runningdinner.core.MealClass;
import org.runningdinner.core.NoPossibleRunningDinnerException;
import org.runningdinner.core.Participant;
import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.core.ParticipantName;
import org.runningdinner.core.RunningDinnerCalculator;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.core.Team;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.ConverterFactory;
import org.runningdinner.core.converter.ConverterFactory.INPUT_FILE_TYPE;
import org.runningdinner.core.converter.FileConverter;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.runningdinner.event.publisher.EventPublisher;
import org.runningdinner.exceptions.DinnerNotFoundException;
import org.runningdinner.model.BaseMailReport;
import org.runningdinner.model.DinnerRouteMailReport;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.RunningDinnerInfo;
import org.runningdinner.model.TeamMailReport;
import org.runningdinner.repository.jpa.RunningDinnerRepositoryJpa;
import org.runningdinner.service.RunningDinnerService;
import org.runningdinner.service.TempParticipantLocationHandler;
import org.runningdinner.service.UuidGenerator;
import org.runningdinner.service.email.DinnerRouteMessageFormatter;
import org.runningdinner.service.email.TeamArrangementMessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

public class RunningDinnerServiceImpl implements RunningDinnerService {

	// Spring managed dependencies
	private RunningDinnerRepositoryJpa repository;
	private TempParticipantLocationHandler tempParticipantLocationHandler;
	private UuidGenerator uuidGenerator;
	private EventPublisher eventPublisher;

	// Self managed dependency
	private RunningDinnerCalculator runningDinnerCalculator = new RunningDinnerCalculator();

	private static Logger LOGGER = LoggerFactory.getLogger(RunningDinnerServiceImpl.class);

	@Override
	public List<Participant> parseParticipants(final MultipartFile file, final ParsingConfiguration parsingConfiguration)
			throws IOException, ConversionException {

		final INPUT_FILE_TYPE fileType = determineFileType(file);

		InputStream inputStream = null;
		try {
			inputStream = file.getInputStream();
			FileConverter fileConverter = ConverterFactory.newConverter(parsingConfiguration, fileType);
			return fileConverter.parseParticipants(inputStream);
		}
		finally {
			CoreUtil.closeStream(inputStream);
		}
	}

	/**
	 * Checks whether all participants can assigned into teams with the provided configuration.<br>
	 * If not, the resulting list contain all participants that cannot be assigned.<br>
	 * If the resulting list is empty, then all participants can successfully be assigned into teams.<br>
	 * 
	 * @param runningDinnerConfig
	 * @param participants
	 * @return
	 */
	@Override
	public List<Participant> calculateNotAssignableParticipants(RunningDinnerConfig runningDinnerConfig, List<Participant> participants) {
		return runningDinnerCalculator.calculateNotAssignableParticipants(runningDinnerConfig, participants);
	}

	/**
	 * Tries to determine the file-type of the uploaded file.<br>
	 * If file type cannot be determined and/or it is not supported the result is INPUT_FILE_TYPE.UNKNOWN
	 * 
	 * @param file
	 * @return
	 */
	@Override
	public INPUT_FILE_TYPE determineFileType(final MultipartFile file) {
		LOGGER.debug("Trying to determine file type of MultipartFile {}", file);
		if (file != null) {
			INPUT_FILE_TYPE fileType = ConverterFactory.determineFileType(file.getOriginalFilename());
			if (INPUT_FILE_TYPE.UNKNOWN != fileType) {
				return fileType;
			}

			fileType = ConverterFactory.determineFileType(file.getContentType());
			if (INPUT_FILE_TYPE.UNKNOWN != fileType) {
				return fileType;
			}
		}
		return INPUT_FILE_TYPE.UNKNOWN;
	}

	/**
	 * Creates a new persistent running dinner instance
	 * 
	 * @param runningDinnerInfo Basic detail infos about the running dinner to create
	 * @param runningDinnerConfig The options of the running dinner
	 * @param participants All participants of the dinner
	 * @param newUuid A unique identifier that is used for retrieving this running dinner later on
	 * @return
	 */
	@Override
	@Transactional
	public RunningDinner createRunningDinner(final RunningDinnerInfo runningDinnerInfo, final RunningDinnerConfig runningDinnerConfig,
			final List<Participant> participants, final String newUuid) {

		RunningDinner result = new RunningDinner();
		result.setCity(runningDinnerInfo.getCity());
		result.setEmail(runningDinnerInfo.getEmail());
		result.setTitle(runningDinnerInfo.getTitle());
		result.setDate(runningDinnerInfo.getDate());

		Set<MealClass> mealClasses = runningDinnerConfig.getMealClasses();
		LOGGER.info("Saving {} meals for running dinner {}", mealClasses.size(), newUuid);
		for (MealClass mealClass : mealClasses) {
			repository.saveOrMerge(mealClass);
		}
		result.setConfiguration(runningDinnerConfig);

		result.setUuid(newUuid);

		// Note: On performance problems use a bulk/batch update mechanisms
		LOGGER.info("Saving {} participants for running dinner {}", participants.size(), newUuid);
		for (Participant participant : participants) {
			repository.saveOrMerge(participant);
		}
		result.setParticipants(participants);

		LOGGER.info("Saving complete running dinner {}", newUuid);
		repository.saveOrMerge(result);

		final RunningDinner constResultRef = result;

		// Publish event only after transaction is successfully committed:
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCommit() {
				eventPublisher.notifyNewRunningDinner(constResultRef);
			}
		});

		return result;
	}

	/**
	 * Randomly creates teams for the running dinner identified by the passed uuid.<br>
	 * This method assumes that there exist no teams till now.
	 * 
	 * @param uuid
	 * @return
	 * @throws NoPossibleRunningDinnerException
	 */
	@Override
	@Transactional(rollbackFor = { NoPossibleRunningDinnerException.class, RuntimeException.class })
	public GeneratedTeamsResult createTeamAndVisitationPlans(final String uuid) throws NoPossibleRunningDinnerException {

		RunningDinner dinner = repository.findDinnerByUuidWithParticipants(uuid);
		if (dinner == null) {
			throw new DinnerNotFoundException("Could not find dinner with uuid " + uuid);
		}

		LOGGER.info("Create random teams and visitation-plans for dinner {}", uuid);

		// create new team- and visitation-plans
		GeneratedTeamsResult result = generateTeamPlan(dinner.getConfiguration(), dinner.getParticipants());

		// TODO: Wtf?!
		/*
		 * for (Team team : result.getRegularTeams()) {
		 * System.out.println(team.getVisitationPlan().toString());
		 * }
		 */

		List<Team> regularTeams = result.getRegularTeams();
		List<Participant> notAssignedParticipants = result.getNotAssignedParticipants();

		// #1 Save first every team
		LOGGER.debug("Save {} generated teams for dinner {}", regularTeams.size(), uuid);
		for (Team regularTeam : regularTeams) {
			repository.saveOrMerge(regularTeam);
		}

		// #2 Finally assign visitation plans (and therefore teams):
		// We just assign the visitation-plans to the Running-Dinner. By using the Visitation-Plans we are able to navigate to the teams
		// etc. pp:
		LOGGER.debug("Assign {} generated teams to dinner {}", regularTeams.size(), uuid);
		dinner.setTeams(regularTeams);

		// #3 Save the team-less participants:
		LOGGER.debug("Assign {} not assigned participants to dinner {}", notAssignedParticipants.size(), uuid);
		dinner.setNotAssignedParticipants(notAssignedParticipants);

		// Note: On performance problems use a bulk/batch update mechanisms

		return result;
	}

	protected GeneratedTeamsResult generateTeamPlan(final RunningDinnerConfig runningDinnerConfig, final List<Participant> participants)
			throws NoPossibleRunningDinnerException {
		GeneratedTeamsResult generatedTeams = runningDinnerCalculator.generateTeams(runningDinnerConfig, participants);
		runningDinnerCalculator.assignRandomMealClasses(generatedTeams, runningDinnerConfig.getMealClasses());
		runningDinnerCalculator.generateDinnerExecutionPlan(generatedTeams, runningDinnerConfig);
		return generatedTeams;
	}

	/**
	 * 
	 * @param uuid
	 * @param teamHostMappings Contains the naturalKey of a team as key in the mapping, and the naturalKey of the new hosting participant as
	 *            value in the mapping
	 */
	@Override
	@Transactional
	public List<Team> updateTeamHosters(final String uuid, Map<String, String> teamHostMappings) {

		Set<String> teamKeys = teamHostMappings.keySet();
		LOGGER.info("Call updateTeamHosters with {} teamKeys", teamKeys.size());

		Collection<String> newParticipantKeysTmp = teamHostMappings.values();
		LOGGER.debug("Call updateTeamHosters with {} newParticipantKeys", newParticipantKeysTmp.size());

		HashSet<String> newParticipantKeys = new HashSet<String>(newParticipantKeysTmp);
		Assert.state(newParticipantKeys.size() == newParticipantKeysTmp.size(),
				"Each participant naturalKey should have been unique in the passed teamHostMappings object, but it was not");
		LOGGER.debug("All participant naturalKeys are unique");

		List<Team> teams = repository.loadRegularTeamsFromDinnerByKeys(uuid, teamKeys);
		LOGGER.debug("Found {} teams for the passed teamKeys", teams.size());
		Assert.state(teams.size() == teamKeys.size(), "There should be modified " + teamKeys.size() + " teams, but found " + teams.size()
				+ " teams in database");

		for (Team team : teams) {
			Set<Participant> teamMembers = team.getTeamMembers();
			LOGGER.debug("Try to assign new hoster to team {}", team.getTeamNumber());

			for (Participant teamMember : teamMembers) {
				String naturalKey = teamMember.getNaturalKey();
				if (newParticipantKeys.contains(naturalKey)) {
					if (!teamMember.isHost()) { // Prevent unnecessary SQL update if this participant was already the host
						teamMember.setHost(true);
					}
				}
				else {
					if (teamMember.isHost()) {
						teamMember.setHost(false);
					}
				}
			}

			LOGGER.debug("Changed hoster of team {}", team.getTeamNumber());
		}

		return teams;
	}

	@Override
	@Transactional
	public List<Team> switchTeamMembers(String uuid, String firstParticipantKey, String secondParticipantKey) {
		LOGGER.info("Calling SwitchTeamMembers for dinner {}", uuid);

		List<Team> parentTeams = repository.loadTeamsForParticipants(uuid,
				new HashSet<String>(Arrays.asList(firstParticipantKey, secondParticipantKey)));
		LOGGER.debug("Found {} parent-teams for participant-keys {}", parentTeams.size(), ("[" + firstParticipantKey + ","
				+ secondParticipantKey + "]"));

		RunningDinner dinner = repository.findDinnerWithBasicDetailsByUuid(uuid);
		RunningDinnerConfig configuration = dinner.getConfiguration();
		LOGGER.debug("Configuration {} loaded for dinner {}", configuration, uuid);

		if (parentTeams.size() != 2) {
			throw new IllegalStateException("Retrieved " + parentTeams.size() + " teams, but expected 2 teams");
		}

		Participant firstParticipant = null;
		Team teamOfFirstParticipant = null;
		Participant secondParticipant = null;
		Team teamOfSecondParticipant = null;

		for (Team parentTeam : parentTeams) {
			Set<Participant> teamMembers = parentTeam.getTeamMembers();
			for (Participant teamMember : teamMembers) {
				if (teamMember.getNaturalKey().equals(firstParticipantKey)) {
					firstParticipant = teamMember;
					teamOfFirstParticipant = parentTeam;
				}
				else if (teamMember.getNaturalKey().equals(secondParticipantKey)) {
					secondParticipant = teamMember;
					teamOfSecondParticipant = parentTeam;
				}
			}
		}
		LOGGER.debug("Found first participant {} and second participant {}", firstParticipant, secondParticipant);

		if (firstParticipant == null || secondParticipant == null) {
			throw new EntityNotFoundException("At least one participant could not be fetched");
		}

		if (teamOfFirstParticipant.equals(teamOfSecondParticipant)) {
			LOGGER.debug("Parent-Team {} of both participants is the same", teamOfFirstParticipant);
			return parentTeams; // Nothing to do
		}

		LOGGER.debug("Removing both participants from their parent teams");
		teamOfFirstParticipant.getTeamMembers().remove(firstParticipant);
		teamOfSecondParticipant.getTeamMembers().remove(secondParticipant);

		// Check hosts:
		// As the host-flag may be changed during the checkHostingForTeam calls, we have to save it before:
		LOGGER.debug("Re-Assign hosting flag to both participants");
		boolean firstParticipantIsHost = firstParticipant.isHost();
		boolean secondParticipantIsHost = secondParticipant.isHost();
		checkHostingForTeam(teamOfFirstParticipant, firstParticipantIsHost, secondParticipant, configuration);
		checkHostingForTeam(teamOfSecondParticipant, secondParticipantIsHost, firstParticipant, configuration);

		LOGGER.debug("Assign participant {} to new parent team {}", secondParticipant, teamOfFirstParticipant);
		teamOfFirstParticipant.getTeamMembers().add(secondParticipant);
		LOGGER.debug("Assign participant {} to new parent team {}", firstParticipant, teamOfSecondParticipant);
		teamOfSecondParticipant.getTeamMembers().add(firstParticipant);

		return parentTeams;
	}

	/**
	 * This method performs some intelligent tasks for assigning a (potential new) optimal hosting participant of the parent-team of a
	 * swapped participant
	 * 
	 * @param team Parent team of newParticipant
	 * @param oldParticipantWasHost Was the old (now switched) participant a host?
	 * @param newParticipant The new participant that shall now be in the team
	 */
	protected void checkHostingForTeam(Team team, boolean oldParticipantWasHost, Participant newParticipant,
			RunningDinnerConfig configuration) {

		// #1: check hosting conidtions
		if (oldParticipantWasHost) {
			if (FuzzyBoolean.TRUE == configuration.canHost(newParticipant)) {
				// Because the old participant was the host we can safely set this participant as new host (it is then the only one)
				newParticipant.setHost(true);
				return;
			}
			// else: iterate later over all participants and set a new host (-> below)
		}
		else if (!oldParticipantWasHost && newParticipant.isHost()) {
			// In this case another participant was assigned to be the host.
			// But this may be not optimal as the other participant may have an unknown hosting capability
			newParticipant.setHost(false);
		}

		// #2 Ensure that we have one (optimal) hosting participant
		Participant currentHostingParticipant = null;
		Participant newOptimalHostingParticipant = null;

		for (Participant p : team.getTeamMembers()) {
			FuzzyBoolean canHost = configuration.canHost(p);

			if (canHost == FuzzyBoolean.UNKNOWN && newOptimalHostingParticipant == null) {
				newOptimalHostingParticipant = p; // This would be the fallback
			}
			if (canHost == FuzzyBoolean.TRUE) {
				newOptimalHostingParticipant = p;
			}
			if (p.isHost()) {
				currentHostingParticipant = p;
			}
		}

		if (currentHostingParticipant == null) {
			if (newOptimalHostingParticipant != null) {
				newOptimalHostingParticipant.setHost(true);
			}
			else {
				newParticipant.setHost(true); // Fallback
			}
			return;
		}

		if (newOptimalHostingParticipant == null || currentHostingParticipant.equals(newOptimalHostingParticipant)) {
			// Nothing to do, because there exist no better hosting-solution as the current one
			return;
		}

		// Swap hosting participants:
		currentHostingParticipant.setHost(false); // currentHostingParticipant is never null
		newOptimalHostingParticipant.setHost(true);
	}

	@Override
	@Transactional
	public int sendTeamMessages(String uuid, final List<String> teamKeys, final TeamArrangementMessageFormatter messageFormatter) {

		LOGGER.info("Send team-arrangement email messages for {} teams for dinner {}", teamKeys.size(), uuid);

		Set<String> teamKeysAsSet = convertTeamKeysToSet(teamKeys);

		if (CoreUtil.isEmpty(teamKeysAsSet)) {
			LOGGER.warn("No teams passed for sending messages!");
			return 0;
		}

		RunningDinner dinner = repository.findDinnerWithBasicDetailsByUuid(uuid);
		final List<Team> teams = repository.loadRegularTeamsFromDinnerByKeys(uuid, teamKeysAsSet);

		checkLoadedTeamSize(teams, teamKeysAsSet.size());

		final TeamMailReport teamMailStatusInfo = new TeamMailReport(dinner);
		teamMailStatusInfo.applyNewSending();
		repository.saveOrMerge(teamMailStatusInfo);

		// Publish event only after transaction is successfully committed:
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCommit() {
				eventPublisher.publishTeamMessages(teams, messageFormatter, teamMailStatusInfo);
			}
		});

		return teams.size();
	}

	@Override
	@Transactional
	public int sendDinnerRouteMessages(final String uuid, final List<String> selectedTeamKeys,
			final DinnerRouteMessageFormatter dinnerRouteFormatter) {

		LOGGER.info("Send final dinner-route email messages for {} teams for dinner {}", selectedTeamKeys.size(), uuid);

		Set<String> teamKeys = convertTeamKeysToSet(selectedTeamKeys);

		if (CoreUtil.isEmpty(teamKeys)) {
			LOGGER.warn("No teams passed for sending dinner route messages!");
			return 0;
		}

		final List<Team> teams = repository.loadTeamsWithVisitationPlan(teamKeys, true);
		final RunningDinner dinner = repository.findDinnerWithBasicDetailsByUuid(uuid);

		checkLoadedTeamSize(teams, teamKeys.size());

		final DinnerRouteMailReport dinnerRouteMailReport = new DinnerRouteMailReport(dinner);
		dinnerRouteMailReport.applyNewSending();
		repository.saveOrMerge(dinnerRouteMailReport);

		// Publish event only after transaction is successfully committed:
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
			@Override
			public void afterCommit() {
				eventPublisher.publishDinnerRouteMessages(teams, dinnerRouteFormatter, dinnerRouteMailReport);
			}
		});

		return teams.size();
	}

	@Override
	public List<Team> loadRegularTeamsWithVisitationPlanFromDinner(final String uuid) {
		return repository.loadRegularTeamsWithArrangementsFromDinner(uuid);
		// Consider eventually paging for future
	}

	/**
	 * Tries to load an existing running dinner.<br>
	 * There are Just the basic details loaded. For retrieving the associated entities like participants,teams and visitation-plans another
	 * load-method must be used.
	 * 
	 * @param uuid
	 * @throws DinnerNotFoundException If dinner with passed uuid could not be found
	 * @return
	 */
	@Override
	public RunningDinner loadDinnerWithBasicDetails(final String uuid) {
		RunningDinner result = repository.findDinnerWithBasicDetailsByUuid(uuid);
		if (result == null) {
			throw new DinnerNotFoundException("There exists no dinner for uuid " + uuid);
		}
		return result;
	}

	/**
	 * See loadDinnerWithBasicDetails, but fetches also all participants of the dinner.
	 * 
	 * @param uuid
	 * @throws DinnerNotFoundException If dinner with passed uuid could not be found
	 * @return
	 */
	@Override
	public RunningDinner loadDinnerWithParticipants(final String uuid) {
		RunningDinner result = repository.findDinnerByUuidWithParticipants(uuid);
		if (result == null) {
			throw new DinnerNotFoundException("There exists no dinner for uuid " + uuid);
		}
		return result;
	}

	/**
	 * Loads all participants of a dinner identified by the passed uuid.<br>
	 * Use this, if you are just interested in the participants and not in other dinner-details.
	 * 
	 * @param uuid
	 * @return
	 */
	@Override
	public List<Participant> loadAllParticipantsOfDinner(final String uuid) {
		return repository.loadAllParticipantsOfDinner(uuid);
	}

	@Override
	public List<RunningDinner> findDinnersWithEarlierStartDate(Date startDate) {
		return repository.findDinnersWithEarlierStartDate(startDate);
	}

	/**
	 * Loads all participants that could not successfully be assigned into teams.<br>
	 * Note: This makes only sense if there have been built teams already.
	 * 
	 * @param uuid
	 * @return
	 */
	@Override
	public List<Participant> loadNotAssignableParticipantsOfDinner(final String uuid) {
		return repository.loadNotAssignableParticipantsFromDinner(uuid);
	}

	/**
	 * Loads all successfully generated teams (and all contained data) for a dinner.<br>
	 * 
	 * @param uuid
	 * @return
	 */
	@Override
	public List<Team> loadRegularTeamsFromDinner(final String uuid) {
		return repository.loadRegularTeamsFromDinner(uuid);
	}

	/**
	 * Detects how many teams have been built for the dinner identified by the passed uuid
	 * 
	 * @param uuid
	 * @return
	 */
	@Override
	public int loadNumberOfTeamsForDinner(final String uuid) {
		return repository.loadNumberOfTeamsForDinner(uuid);
	}

	@Override
	public Team loadSingleTeamWithVisitationPlan(String teamKey) {
		return repository.loadSingleTeamWithVisitationPlan(teamKey, true);
	}

	@Override
	public Participant loadParticipant(String participantKey) {
		return repository.loadParticipant(participantKey);
	}

	@Override
	@Transactional
	public void deleteCompleteDinner(final RunningDinner dinner) {
		List<Team> teams = repository.loadRegularTeamsWithArrangementsFromDinner(dinner.getUuid());

		// Clear out team-references in visitation-plans
		for (Team team : teams) {
			team.setMealClass(null);
			team.setTeamMembers(null);
			team.getVisitationPlan().removeAllTeamReferences();
		}

		RunningDinner mergedDinner = repository.saveOrMerge(dinner);

		// First remove all mail status info entities as they have a foreign key relationship to runningdinner entity:
		List<TeamMailReport> teamMailStatusInfos = repository.findAllMailReportsForDinner(mergedDinner.getUuid(), TeamMailReport.class);
		List<DinnerRouteMailReport> dinnerRouteMailStatusInfos = repository.findAllMailReportsForDinner(mergedDinner.getUuid(),
				DinnerRouteMailReport.class);

		ArrayList<BaseMailReport> allStatusInfoObjects = new ArrayList<BaseMailReport>(teamMailStatusInfos);
		allStatusInfoObjects.addAll(dinnerRouteMailStatusInfos);
		for (BaseMailReport statusInfo : allStatusInfoObjects) {
			repository.remove(statusInfo);
		}

		// Then remove finally the dinner:
		// This removes automatically all participant-, mealclass- and team-associations (and entities):
		repository.remove(mergedDinner);
	}

	/**
	 * Generate a new UUID which can e.g. be used for a new running dinner
	 * 
	 * @return
	 */
	@Override
	public String generateNewUUID() {
		return uuidGenerator.generateNewUUID();
	}

	@Override
	public String copyParticipantListToTempLocation(List<Participant> participants, final String uniqueLocationIdentifier)
			throws IOException {
		return tempParticipantLocationHandler.pushToTempLocation(participants, uniqueLocationIdentifier);
	}

	@Override
	public List<Participant> getParticipantListFromTempLocation(final String location) throws IOException {
		return tempParticipantLocationHandler.popFromTempLocation(location);
	}

	@Override
	@Transactional
	public void updateParticipant(String participantKey, Participant participant) {

		LOGGER.info("Update participant {}", participantKey);

		Participant existingParticipant = repository.loadParticipant(participantKey);

		// Note: Currently concurrent modifications are not checked

		LOGGER.debug("Updating attributes of loaded participant {}", existingParticipant);

		existingParticipant.setGender(participant.getGender());
		existingParticipant.setEmail(participant.getEmail());
		existingParticipant.setMobileNumber(participant.getMobileNumber());
		existingParticipant.setNumSeats(participant.getNumSeats());

		existingParticipant.setName(ParticipantName.newName().withFirstname(participant.getName().getFirstnamePart()).andLastname(
				participant.getName().getLastname()));

		existingParticipant.setAddress(new ParticipantAddress(participant.getAddress().getStreet(), participant.getAddress().getStreetNr(),
				participant.getAddress().getZip()));
		existingParticipant.getAddress().setCityName(participant.getAddress().getCityName());

	}

	@Override
	@Transactional
	public void updateMealTimes(String uuid, Set<MealClass> meals) {
		RunningDinner dinner = repository.findDinnerWithBasicDetailsByUuid(uuid);
		Set<MealClass> existingMeals = dinner.getConfiguration().getMealClasses();

		LOGGER.info("Update {} meal-times for dinner {}", existingMeals.size(), uuid);

		if (existingMeals.size() != meals.size()) {
			throw new IllegalStateException("Expected " + meals.size() + " meals to be found, but there were  " + existingMeals.size());
		}

		for (MealClass modifiedMeal : meals) {

			boolean mealFound = false;
			for (MealClass existingMeal : existingMeals) {
				if (existingMeal.equals(modifiedMeal)) {
					existingMeal.setTime(modifiedMeal.getTime());
					mealFound = true;
					break;
				}
			}

			if (!mealFound) {
				throw new IllegalStateException("Meal " + modifiedMeal + " was not found for being updated!");
			}
		}
	}

	/**
	 * Simple helper method for checking for duplicates in a passed team key list
	 * 
	 * @param teamKeysList
	 * @return
	 */
	protected Set<String> convertTeamKeysToSet(final List<String> teamKeysList) {
		Set<String> teamKeys = new HashSet<String>(teamKeysList);
		if (teamKeys.size() != teamKeysList.size()) {
			throw new IllegalStateException("Passed team key list contained some duplicates!");
		}
		return teamKeys;
	}

	/**
	 * Asserts that the size of loaded teams is the same as the passed expectedSize.<br>
	 * 
	 * @param loadedTeams
	 * @param expectedSize
	 */
	protected void checkLoadedTeamSize(final List<Team> loadedTeams, final int expectedSize) {
		if (CoreUtil.isEmpty(loadedTeams) && expectedSize > 0) {
			throw new IllegalStateException("No teams available => Impossible to finalize and/or send messages");
		}
		if (loadedTeams.size() != expectedSize) {
			throw new IllegalStateException("Expected " + expectedSize + " teams to be found, but there were " + loadedTeams.size());
		}
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
	@Transactional
	public void deleteMailReport(BaseMailReport mailReport) {
		BaseMailReport mergedReport = repository.saveOrMerge(mailReport);
		repository.remove(mergedReport);
	}

	@Override
	public List<BaseMailReport> findPendingMailReports(final Date sendingStartDateLimit) {
		return repository.findPendingMailReports(sendingStartDateLimit);
	}

	@Autowired
	public void setTempParticipantLocationHandler(TempParticipantLocationHandler tempParticipantLocationHandler) {
		this.tempParticipantLocationHandler = tempParticipantLocationHandler;
	}

	@Autowired
	public void setUuidGenerator(UuidGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
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
