package org.runningdinner.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

import org.runningdinner.core.CoreUtil;
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
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.RunningDinnerInfo;
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
			repository.save(mealClass);
		}
		result.setConfiguration(runningDinnerConfig);

		result.setUuid(newUuid);

		// Note: On performance problems use a bulk/batch update mechanisms
		LOGGER.info("Saving {} participants for running dinner {}", participants.size(), newUuid);
		for (Participant participant : participants) {
			repository.save(participant);
		}
		result.setParticipants(participants);

		LOGGER.info("Saving complete running dinner {}", newUuid);
		repository.save(result);

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

		// create new team- and visitation-plans
		GeneratedTeamsResult result = generateTeamPlan(dinner.getConfiguration(), dinner.getParticipants());

		for (Team team : result.getRegularTeams()) {
			System.out.println(team.getVisitationPlan().toString());
		}

		List<Team> regularTeams = result.getRegularTeams();
		List<Participant> notAssignedParticipants = result.getNotAssignedParticipants();

		// #1 Save first every team
		for (Team regularTeam : regularTeams) {
			repository.save(regularTeam);
		}

		// #2 Finally assign visitation plans (and therefore teams):
		// We just assign the visitation-plans to the Running-Dinner. By using the Visitation-Plans we are able to navigate to the teams
		// etc. pp:
		dinner.setTeams(regularTeams);

		// #3 Save the team-less participants:
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
	public void updateTeamHosters(final String uuid, Map<String, String> teamHostMappings) {

		Set<String> teamKeys = teamHostMappings.keySet();
		LOGGER.info("Call updateTeamHosters with {} teamKeys", teamKeys.size());

		Collection<String> newParticipantKeysTmp = teamHostMappings.values();
		LOGGER.info("Call updateTeamHosters with {} newParticipantKeys", newParticipantKeysTmp.size());

		HashSet<String> newParticipantKeys = new HashSet<String>(newParticipantKeysTmp);
		Assert.state(newParticipantKeys.size() == newParticipantKeysTmp.size(),
				"Each participant naturalKey should have been unique in the passed teamHostMappings object, but it was not");
		LOGGER.info("All participant naturalKeys are unique");

		List<Team> teams = repository.loadRegularTeamsFromDinnerByKeys(uuid, teamKeys);
		LOGGER.info("Found {} teams for the passed teamKeys", teams.size());
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
	}

	@Override
	@Transactional
	public List<Team> switchTeamMembers(String uuid, String firstParticipantKey, String secondParticipantKey) {

		// List<Team> parentTeams = new ArrayList<Team>(2);

		// TODO #1: Use query instead
		// List<Team> allTeams = repository.loadRegularTeamsFromDinner(uuid);
		// for (Team team : allTeams) {
		// for (Participant participant : team.getTeamMembers()) {
		// if (participant.getNaturalKey().equals(firstParticipantKey) || participant.getNaturalKey().equals(secondParticipantKey)) {
		// parentTeams.add(team);
		// }
		// }
		// }

		// TODO #2: Normally I would use this query, but it doesn't work correctly:
		List<Team> parentTeams = repository.loadTeamsForParticipants(uuid,
				new HashSet<String>(Arrays.asList(firstParticipantKey, secondParticipantKey)));

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

		if (firstParticipant == null || secondParticipant == null) {
			throw new EntityNotFoundException("At least one participant could not be fetched");
		}

		if (teamOfFirstParticipant.equals(teamOfSecondParticipant)) {
			return parentTeams; // Nothing to do
		}

		teamOfFirstParticipant.getTeamMembers().remove(firstParticipant);
		teamOfSecondParticipant.getTeamMembers().remove(secondParticipant);

		// Check hosts:
		// As the host-flag may be changed during the checkHostingForTeam calls, we have to save it before:
		boolean firstParticipantIsHost = firstParticipant.isHost();
		boolean secondParticipantIsHost = secondParticipant.isHost();
		checkHostingForTeam(teamOfFirstParticipant, firstParticipantIsHost, secondParticipant);
		checkHostingForTeam(teamOfSecondParticipant, secondParticipantIsHost, firstParticipant);

		teamOfFirstParticipant.getTeamMembers().add(secondParticipant);
		teamOfSecondParticipant.getTeamMembers().add(firstParticipant);

		return parentTeams;
	}

	/**
	 * 
	 * @param team
	 * @param oldParticipantWasHost Was the old (now switched) participant a host?
	 * @param newParticipant The new participant that shall now be in the team
	 */
	protected void checkHostingForTeam(Team team, boolean oldParticipantWasHost, Participant newParticipant) {
		// Note: It would be more "intelligent" to search for a new host in the remaining team-members list instead of just switching the
		// host-flag, but for now this should be sufficient (TODO for future)
		if (oldParticipantWasHost && !newParticipant.isHost()) {
			newParticipant.setHost(true);
		}
		else if (!oldParticipantWasHost && newParticipant.isHost()) {
			newParticipant.setHost(false);
		}
		// When reaching here, either both have been hosts, or both have not been hosts, so we don't to perform any further action
	}

	@Override
	public int sendTeamMessages(String uuid, final List<String> teamKeys, final TeamArrangementMessageFormatter messageFormatter) {

		Set<String> teamKeysAsSet = convertTeamKeysToSet(teamKeys);

		List<Team> teams = repository.loadRegularTeamsFromDinnerByKeys(uuid, teamKeysAsSet);

		checkLoadedTeamSize(teams, teamKeysAsSet.size());

		eventPublisher.publishTeamMessages(teams, messageFormatter);
		return teams.size();
	}

	@Override
	public int sendDinnerRouteMessages(final String uuid, final List<String> selectedTeamKeys,
			final DinnerRouteMessageFormatter dinnerRouteFormatter) {

		Set<String> teamKeys = convertTeamKeysToSet(selectedTeamKeys);

		List<Team> teams = repository.loadTeamsWithVisitationPlan(teamKeys, true);

		checkLoadedTeamSize(teams, teamKeys.size());

		eventPublisher.publishDinnerRouteMessages(teams, dinnerRouteFormatter);
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
		Participant existingParticipant = repository.loadParticipant(participantKey);

		// Note: Currently concurrent modifications are not checked

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
