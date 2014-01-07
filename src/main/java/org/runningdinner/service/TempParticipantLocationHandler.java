package org.runningdinner.service;

import java.io.IOException;
import java.util.List;

import org.runningdinner.core.Participant;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.springframework.web.multipart.MultipartFile;

public interface TempParticipantLocationHandler {

	/**
	 * Copies an uploaded file to a new storage location from within it survives the duration of a request.<br>
	 * Typically this is a special directory in the file-system of the application server, but it might also be imaginable that there
	 * exist implementations in which such a file is hold in-memory or is temporarily persisted in database.
	 * 
	 * @param file The uploaded file
	 * @param uniqueIdentifier a unique file for each user for distinguishing uploaded files of several users. For several files of the same
	 *            user it is acceptable that this uniqueIdentifier is the same (yielding in overwriting equal files of the same user)
	 * @return
	 * @throws IOException
	 */
	String pushToTempLocation(final MultipartFile file, final String uniqueIdentifier) throws IOException;

	/**
	 * Retrieves the parsed participants from a previously uploaded participant-file.<br>
	 * Once this method is called, it is not guaranteed that subsequent calls succeed for the same location-identifier as some
	 * implementations might remove the temporary data immediately.
	 * 
	 * @param uniqueIdentifier The location identifier which was retrieved when {@link pushToTempLocation} was called
	 * @param parsingConfiguration The parsingConfiguration which was used for parsing the participants
	 * @return
	 * @throws IOException
	 * @throws ConversionException
	 */
	List<Participant> popFromTempLocation(final String location, final ParsingConfiguration parsingConfiguration) throws IOException,
			ConversionException;

}
