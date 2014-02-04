package org.runningdinner.service;

import java.io.IOException;
import java.util.List;

import org.runningdinner.core.Participant;

public interface TempParticipantLocationHandler {

	/**
	 * Copies the parsed participant list to a new storage location from within it survives the duration of a request.<br>
	 * Typically this is a special directory in the file-system of the application server, but it might also be imaginable that there
	 * exist implementations in which the list is hold in-memory or is temporarily persisted in database.
	 * 
	 * @param participants The list with the parsed participants
	 * @param uniqueIdentifier a unique identifier for each user for distinguishing participant-lists of several users. For several
	 *            participant-lists of the same user it is acceptable that this uniqueIdentifier is the same (yielding in overwriting equal
	 *            participant-lists of the same user)
	 * @return
	 * @throws IOException
	 */
	String pushToTempLocation(final List<Participant> participants, final String uniqueIdentifier) throws IOException;

	/**
	 * Retrieves the parsed participants from a previously stored location.<br>
	 * Once this method is called, it is not guaranteed that subsequent calls succeed for the same location-identifier as some
	 * implementations might remove the temporary data immediately.
	 * 
	 * @param location The location identifier which was retrieved when {@link pushToTempLocation} was called
	 * @return
	 * @throws IOException
	 */
	List<Participant> popFromTempLocation(final String location) throws IOException;

}
