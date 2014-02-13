package org.runningdinner.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.runningdinner.core.GeneratedTeamsResult;
import org.runningdinner.core.MealClass;
import org.runningdinner.core.NoPossibleRunningDinnerException;
import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.core.Team;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.ConverterFactory.INPUT_FILE_TYPE;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.runningdinner.exceptions.DinnerNotFoundException;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.model.RunningDinnerInfo;
import org.runningdinner.service.email.DinnerRouteMessageFormatter;
import org.runningdinner.service.email.TeamArrangementMessageFormatter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Main service that provides the means for persisting and loading of running dinner instances (and all involved entities).<br>
 * Furthermore it also provides the means for sending different (email) messages and it also provides support for handling the business
 * logic of the core classes.
 * 
 * @author Clemens Stich
 * 
 */
public interface RunningDinnerService {

	/**
	 * Parses participant list from an uploaded file
	 * 
	 * @param file The uploaded file
	 * @param parsingConfiguration The configuration that is used during parsing
	 * @return
	 * @throws IOException
	 * @throws ConversionException
	 */
	List<Participant> parseParticipants(MultipartFile file, ParsingConfiguration parsingConfiguration) throws IOException,
			ConversionException;

	/**
	 * Checks whether all participants can assigned into teams with the provided configuration.<br>
	 * If not, the resulting list contain all participants that cannot be assigned.<br>
	 * If the resulting list is empty, then all participants can successfully be assigned into teams.<br>
	 * 
	 * @param runningDinnerConfig
	 * @param participants
	 * @return
	 */
	List<Participant> calculateNotAssignableParticipants(RunningDinnerConfig runningDinnerConfig, List<Participant> participants);

	/**
	 * Tries to determine the file-type of the uploaded file.<br>
	 * If file type cannot be determined and/or it is not supported the result is INPUT_FILE_TYPE.UNKNOWN
	 * 
	 * @param file
	 * @return
	 */
	INPUT_FILE_TYPE determineFileType(MultipartFile file);

	/**
	 * Creates a new persistent running dinner instance
	 * 
	 * @param runningDinnerInfo Basic detail infos about the running dinner to create
	 * @param runningDinnerConfig The options of the running dinner
	 * @param participants All participants of the dinner
	 * @param newUuid A unique identifier that is used for retrieving this running dinner later on
	 * @return
	 */
	RunningDinner createRunningDinner(RunningDinnerInfo runningDinnerInfo, RunningDinnerConfig runningDinnerConfig,
			List<Participant> participants, String newUuid);

	/**
	 * Randomly creates teams for the running dinner identified by the passed uuid.<br>
	 * This method assumes that there exist no teams till now.
	 * 
	 * @param uuid
	 * @return
	 * @throws NoPossibleRunningDinnerException
	 */
	GeneratedTeamsResult createTeamAndVisitationPlans(String uuid) throws NoPossibleRunningDinnerException;

	/**
	 * 
	 * @param uuid
	 * @param teamHostMappings Contains the naturalKey of a team as key in the mapping, and the naturalKey of the new hosting participant as
	 *            value in the mapping
	 */
	void updateTeamHosters(String uuid, Map<String, String> teamHostMappings);

	/**
	 * Swap the parent team of the first participant with the parent team of the second participant.<br>
	 * This works only if both participants are assigned to different teams.
	 * 
	 * @param uuid The running dinner to which the passed participants belong to
	 * @param firstParticipantKey Natural key of the first participant
	 * @param secondParticipantKey Natural key of the second participant
	 * @return The two (updated) parent teams
	 */
	List<Team> switchTeamMembers(String uuid, String firstParticipantKey, String secondParticipantKey);

	/**
	 * Send messages to the members of all teams identified by the passed teamKeys of a running dinner.<br>
	 * 
	 * @param uuid The parent running dinner
	 * @param teamKeys The natural keys of the teams to which all participants shall be sent messages
	 * @param messageFormatter A setup formatter that is used for replacing some template-variables inside a message
	 * @return
	 */
	int sendTeamMessages(String uuid, List<String> teamKeys, TeamArrangementMessageFormatter messageFormatter);

	int sendDinnerRouteMessages(String uuid, List<String> selectedTeamKeys, DinnerRouteMessageFormatter dinnerRouteFormatter);

	/**
	 * Loads all teams of a running dinner with their complete dinner-routes
	 * 
	 * @param uuid
	 * @return
	 */
	List<Team> loadRegularTeamsWithVisitationPlanFromDinner(String uuid);

	/**
	 * Tries to load an existing running dinner.<br>
	 * There are Just the basic details loaded. For retrieving the associated entities like participants,teams and visitation-plans another
	 * load-method must be used.
	 * 
	 * @param uuid
	 * @throws DinnerNotFoundException If dinner with passed uuid could not be found
	 * @return
	 */
	RunningDinner loadDinnerWithBasicDetails(String uuid);

	/**
	 * See loadDinnerWithBasicDetails, but fetches also all participants of the dinner.
	 * 
	 * @param uuid
	 * @throws DinnerNotFoundException If dinner with passed uuid could not be found
	 * @return
	 */
	RunningDinner loadDinnerWithParticipants(String uuid);

	/**
	 * Loads all participants of a dinner identified by the passed uuid.<br>
	 * Use this, if you are just interested in the participants and not in other dinner-details.
	 * 
	 * @param uuid
	 * @return
	 */
	List<Participant> loadAllParticipantsOfDinner(String uuid);

	/**
	 * Loads all participants that could not successfully be assigned into teams.<br>
	 * Note: This makes only sense if there have been built teams already.
	 * 
	 * @param uuid
	 * @return
	 */
	List<Participant> loadNotAssignableParticipantsOfDinner(String uuid);

	/**
	 * Loads all successfully generated teams (and all contained data) for a dinner.<br>
	 * 
	 * @param uuid
	 * @return
	 */
	List<Team> loadRegularTeamsFromDinner(String uuid);

	/**
	 * Detects how many teams have been built for the dinner identified by the passed uuid
	 * 
	 * @param uuid
	 * @return
	 */
	int loadNumberOfTeamsForDinner(String uuid);

	/**
	 * Loads a single team with their complete dinner-route
	 * 
	 * @param teamKey
	 * @return
	 */
	Team loadSingleTeamWithVisitationPlan(String teamKey);

	/**
	 * Loads the participant identified by the passed natural key
	 * 
	 * @param participantKey
	 * @return
	 */
	Participant loadParticipant(String participantKey);

	/**
	 * Generate a new UUID which can e.g. be used for a new running dinner
	 * 
	 * @return
	 */
	String generateNewUUID();

	/**
	 * Temporarily saves a participant list for later retrieval
	 * 
	 * @param participants The participant list to temporarily save
	 * @param uniqueLocationIdentifier A marker that is used to uniquely identify the saved participants
	 * @return Return a unique location string that can later on be used for retrieving the saved participant list again
	 * @throws IOException
	 */
	String copyParticipantListToTempLocation(List<Participant> participants, String uniqueLocationIdentifier) throws IOException;

	/**
	 * Retrieves a previously temporarily saved participant list (-> {@link copyParticipantListToTempLocation})
	 * 
	 * @param location Unique location string that is used to load the participant list
	 * @return
	 * @throws IOException
	 */
	List<Participant> getParticipantListFromTempLocation(String location) throws IOException;

	/**
	 * Saves changes to a participant
	 * 
	 * @param participantKey The natural key of the participant to be updated
	 * @param participant The settings of this participant instance are updated into the participant identified by the passed participantKey
	 */
	void updateParticipant(String participantKey, Participant participant);

	/**
	 * Update the times of each passed meal of the running dinner identified by the passed uuid
	 * 
	 * @param uuid
	 * @param meals Contains the new times for each passed meal to be updated
	 */
	void updateMealTimes(String uuid, Set<MealClass> meals);

}