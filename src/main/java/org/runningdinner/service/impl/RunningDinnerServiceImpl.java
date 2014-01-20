package org.runningdinner.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
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
import org.runningdinner.exceptions.NoPossibleRunningDinnerRuntimeException;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.RunningDinnerInfo;
import org.runningdinner.repository.RunningDinnerRepository;
import org.runningdinner.service.TempParticipantLocationHandler;
import org.runningdinner.service.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

public class RunningDinnerServiceImpl {

	// Spring managed dependencies
	private RunningDinnerRepository repository;
	private TempParticipantLocationHandler tempParticipantLocationHandler;
	private UuidGenerator uuidGenerator;
	private EventPublisher eventPublisher;

	// Self managed dependency
	private RunningDinnerCalculator runningDinnerCalculator = new RunningDinnerCalculator();

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
	 * <br>
	 * Callers must also check the NoPossibleRunningDinnerException which is thrown if there are e.g. too few participants.
	 * 
	 * @param runningDinnerConfig
	 * @param participants
	 * @return
	 * @throws NoPossibleRunningDinnerException Thrown when it is not possible to construct a running dinner with the provided configuration
	 *             and the provided participants. This can happen if there are too few participants.
	 */
	public List<Participant> calculateNotAssignableParticipants(RunningDinnerConfig runningDinnerConfig, List<Participant> participants)
			throws NoPossibleRunningDinnerException {

		GeneratedTeamsResult generatedTeamsResult = runningDinnerCalculator.generateTeams(runningDinnerConfig, participants);
		if (generatedTeamsResult.hasNotAssignedParticipants()) {
			return generatedTeamsResult.getNotAssignedParticipants();
		}
		return Collections.emptyList();
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
		result = repository.save(result);

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

	@Transactional
	public GeneratedTeamsResult createTeamAndVisitationPlans(final String uuid) throws NoPossibleRunningDinnerRuntimeException {

		RunningDinner dinner = repository.findDinnerByUuidWithParticipants(uuid);
		if (dinner == null) {
			throw new DinnerNotFoundException("Could not find dinner with uuid " + uuid);
		}

		// create new team- and visitation-plans
		GeneratedTeamsResult result = null;
		try {
			result = runningDinnerCalculator.generateTeams(dinner.getConfiguration(), dinner.getParticipants());
			runningDinnerCalculator.assignRandomMealClasses(result, dinner.getConfiguration().getMealClasses());
			runningDinnerCalculator.generateDinnerExecutionPlan(result, dinner.getConfiguration());
		}
		catch (NoPossibleRunningDinnerException ex) {
			throw new NoPossibleRunningDinnerRuntimeException(ex);
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

		// // #2 Cumulate all involved teams...
		// Set<Team> teamsToFetch = new HashSet<Team>();
		// for (VisitationPlan visitationPlan : visitationPlans) {
		// Set<? extends Team> hostTeams = visitationPlan.getHostTeams();
		// Set<? extends Team> guestTeams = visitationPlan.getGuestTeams();
		// Team team = visitationPlan.getTeam();
		//
		// teamsToFetch.add(team);
		// teamsToFetch.addAll(hostTeams);
		// teamsToFetch.addAll(guestTeams);
		// }
		//
		// // #3 ... and fetch these teams by their Ids:
		// // (Note: The number of teams that are actually fetched is significantly smaller as expected.
		// // Example: For 6 VisitationPlan entities which have e.g. 5 associated team-entities per each instance, we do NOT fetch 6*5=30
		// // team-instances, but we fetch in fact just 6 team-instances. This is because the same team-entities are distributed across
		// several
		// // VisitationPlan entities. Thus we have limited the whole method to one bigger JOIN query and one smaller IN-Query.)
		// Set<Long> teamIds = repository.getEntityIds(teamsToFetch);
		// List<Team> fullyLoadedTeams = repository.loadTeamsById(teamIds);
		//
		// Map<Long, Team> tmpTemMappings = new HashMap<Long, Team>();
		// for (Team fullyLoadedTeam : fullyLoadedTeams) {
		// tmpTemMappings.put(fullyLoadedTeam.getId(), fullyLoadedTeam);
		// }
		//
		// ArrayList<VisitationPlanInfo> result = new ArrayList<VisitationPlanInfo>(visitationPlans.size());
		//
		// // #4 Reassign the loaded teams to the according visitation-plans and return DTO list:
		// for (VisitationPlan visitationPlan : visitationPlans) {
		// Long teamId = visitationPlan.getTeam().getId();
		// Team team = tmpTemMappings.get(teamId);
		//
		// VisitationPlanInfo vpi = new VisitationPlanInfo(team);
		//
		// Set<? extends Team> hostTeams = visitationPlan.getHostTeams();
		// for (Team hostTeam : hostTeams) {
		// vpi.addHostTeam(tmpTemMappings.get(hostTeam.getId()));
		// }
		//
		// Set<? extends Team> guestTeams = visitationPlan.getGuestTeams();
		// for (Team guestTeam : guestTeams) {
		// vpi.addGuestTeam(tmpTemMappings.get(guestTeam.getId()));
		// }
		//
		// result.add(vpi);
		// }
		//
		// return result;
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
	 * Loads all generated visitation-plans for a dinner identified by the passed uuid.<br>
	 * Note: The resulting visitation-plans contain just light team-objects, so for retrieving team-based info for a single visitation-plan
	 * these info must be separately fetched.
	 * 
	 * @param uuid
	 * @return
	 */
	// public List<VisitationPlan> loadVisitationPlansForDinner(final String uuid) {
	// return repository.loadVisitationPlansForDinner(uuid);
	// }

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
