package org.runningdinner.service;

/**
 * Generates a unique identifier which might be used for storing sensitive information.<br>
 * Max. length of this identifier should not exceed 48.
 * 
 * @author i01002492
 * 
 */
public interface UuidGenerator {

	String generateNewUUID();

	/**
	 * Checks whether a given string is a valid UUID.<br>
	 * This can be used for pre-checking passed UUIDs before passing them to database queries.
	 * 
	 * @param uuid
	 * @return
	 */
	boolean isValid(final String uuid);
}
