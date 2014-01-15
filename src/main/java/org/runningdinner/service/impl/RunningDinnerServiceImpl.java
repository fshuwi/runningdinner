package org.runningdinner.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.runningdinner.core.VisitationPlan;
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

		// On performance problems use a bulk/batch update mechanism...
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

		RunningDinner dinner = repository.findRunningDinnerByUuidWithParticipants(uuid);

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

		ArrayList<VisitationPlan> visitationPlans = new ArrayList<VisitationPlan>();
		for (Team regularTeam : regularTeams) {
			visitationPlans.add(regularTeam.getVisitationPlan());
			repository.save(regularTeam);
		}
		for (VisitationPlan visitationPlan : visitationPlans) {
			repository.save(visitationPlan);
		}

		// TODO
		// for (Participant notAssignedParticipant : notAssignedParticipants) {
		// notAssignedParticipant.set
		// }

		return result;
	}

	public int getNumberOfTeamsForDinner(final String uuid) {
		return repository.getNumberOfTeamsForDinner(uuid);
	}

	public GeneratedTeamsResult generateTeamPlan(final RunningDinnerConfig runningDinnerConfig, final List<Participant> participants)
			throws NoPossibleRunningDinnerException {
		GeneratedTeamsResult generatedTeams = runningDinnerCalculator.generateTeams(runningDinnerConfig, participants);
		runningDinnerCalculator.assignRandomMealClasses(generatedTeams, runningDinnerConfig.getMealClasses());
		runningDinnerCalculator.generateDinnerExecutionPlan(generatedTeams, runningDinnerConfig);
		return generatedTeams;
	}

	/**
	 * 
	 * @param uuid
	 * @throws DinnerNotFoundException If dinner with passed uuid could not be found
	 * @return
	 */
	public RunningDinner findRunningDinner(final String uuid) {
		RunningDinner result = repository.findRunningDinnerByUuid(uuid);
		if (result == null) {
			throw new DinnerNotFoundException("There exists no dinner for uuid " + uuid);
		}
		return result;
	}

	/**
	 * 
	 * @param uuid
	 * @throws DinnerNotFoundException If dinner with passed uuid could not be found
	 * @return
	 */
	public RunningDinner findRunningDinnerWithParticipants(final String uuid) {
		RunningDinner result = repository.findRunningDinnerByUuidWithParticipants(uuid);
		if (result == null) {
			throw new DinnerNotFoundException("There exists no dinner for uuid " + uuid);
		}
		return result;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public List<Participant> getParticipantsFromRunningDinner(final String uuid) {
		// TODO: This may also return an empty list even if the dinner with uuid doesn't exist
		return repository.getParticipantsFromRunningDinner(uuid);
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
