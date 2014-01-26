package org.runningdinner.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.GeneratedTeamsResult;
import org.runningdinner.core.MealClass;
import org.runningdinner.core.NoPossibleRunningDinnerException;
import org.runningdinner.core.Participant;
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
import org.runningdinner.repository.RunningDinnerRepository;
import org.runningdinner.service.TempParticipantLocationHandler;
import org.runningdinner.service.UuidGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

public class RunningDinnerServiceImpl {

	// Spring managed dependencies
	private RunningDinnerRepository repository;
	private TempParticipantLocationHandler tempParticipantLocationHandler;
	private UuidGenerator uuidGenerator;
	private EventPublisher eventPublisher;

	// Self managed dependency
	private RunningDinnerCalculator runningDinnerCalculator = new RunningDinnerCalculator();

	private static Logger LOGGER = LoggerFactory.getLogger(RunningDinnerServiceImpl.class);

	/**
	 * Parses participant list out from uploaded file
	 * 
	 * @param file
	 * @param parsingConfiguration
	 * @return
	 * @throws IOException
	 * @throws ConversionException
	 */
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

	@Transactional
	public RunningDinner createRunningDinner(final RunningDinnerInfo runningDinnerInfo, final RunningDinnerConfig runningDinnerConfig,
			final List<Participant> participants, final String newUuid) {

		RunningDinner result = new RunningDinner();
		result.setCity(runningDinnerInfo.getCity());
		result.setEmail(runningDinnerInfo.getEmail());
		result.setTitle(runningDinnerInfo.getTitle());
		result.setDate(runningDinnerInfo.getDate());

		Set<MealClass> mealClasses = runningDinnerConfig.getMealClasses();
		for (MealClass mealClass : mealClasses) {
			repository.save(mealClass);
		}
		result.setConfiguration(runningDinnerConfig);

		result.setUuid(newUuid);

		// TODO On performance problems use a bulk/batch update mechanisms

		for (Participant participant : participants) {
			repository.save(participant);
		}
		result.setParticipants(participants);
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

	// TODO: Is this sufficient?
	@Transactional(rollbackFor = NoPossibleRunningDinnerException.class)
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

		// TODO On performance problems use a bulk/batch update mechanisms

		return result;
	}

	protected GeneratedTeamsResult generateTeamPlan(final RunningDinnerConfig runningDinnerConfig, final List<Participant> participants)
			throws NoPossibleRunningDinnerException {
		GeneratedTeamsResult generatedTeams = runningDinnerCalculator.generateTeams(runningDinnerConfig, participants);
		runningDinnerCalculator.assignRandomMealClasses(generatedTeams, runningDinnerConfig.getMealClasses());
		runningDinnerCalculator.generateDinnerExecutionPlan(generatedTeams, runningDinnerConfig);
		return generatedTeams;
	}

	public List<Team> loadRegularTeamsWithVisitationPlanFromDinner(final String uuid) {
		return repository.loadRegularTeamsWithArrangementsFromDinner(uuid);
		// TODO: Implement paging
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
	public RunningDinner loadDinnerWithBasicDetails(final String uuid) {
		RunningDinner result = repository.findDinnerByUuid(uuid);
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
	public List<Participant> loadAllParticipantsOfDinner(final String uuid) {
		// TODO: This may also return an empty list even if the dinner with uuid doesn't exist
		return repository.loadAllParticipantsOfDinner(uuid);
	}

	/**
	 * Loads all participants that could not successfully be assigned into teams.<br>
	 * Note: This makes only sense if there have been built teams already.
	 * 
	 * @param uuid
	 * @return
	 */
	public List<Participant> loadNotAssignableParticipantsOfDinner(final String uuid) {
		return repository.loadNotAssignableParticipantsFromDinner(uuid);
	}

	/**
	 * Loads all successfully generated teams (and all contained data) for a dinner.<br>
	 * 
	 * @param uuid
	 * @return
	 */
	public List<Team> loadRegularTeamsFromDinner(final String uuid) {
		return repository.loadRegularTeamsFromDinner(uuid);
	}

	/**
	 * Detects how many teams have been built for the dinner identified by the passed uuid
	 * 
	 * @param uuid
	 * @return
	 */
	public int loadNumberOfTeamsForDinner(final String uuid) {
		return repository.loadNumberOfTeamsForDinner(uuid);
	}

	/**
	 * 
	 * @param uuid
	 * @param teamHostMappings Contains the naturalKey of a team as key in the mapping, and the naturalKey of the new hosting participant as
	 *            value in the mapping
	 */
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

	public String copyParticipantFileToTempLocation(final MultipartFile file, final String uniqueIdentifier) throws IOException {
		return tempParticipantLocationHandler.pushToTempLocation(file, uniqueIdentifier);
	}

	public List<Participant> getParticipantsFromTempLocation(final String location, final ParsingConfiguration parsingConfiguration)
			throws IOException, ConversionException {
		return tempParticipantLocationHandler.popFromTempLocation(location, parsingConfiguration);
	}

	/**
	 * Generate a new UUID which can e.g. be used for a new running dinner
	 * 
	 * @return
	 */
	public String generateNewUUID() {
		return uuidGenerator.generateNewUUID();
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
	public void setRepository(RunningDinnerRepository repository) {
		this.repository = repository;
	}

	@Autowired
	public void setEventPublisher(EventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

}
