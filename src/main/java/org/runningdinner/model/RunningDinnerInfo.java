package org.runningdinner.model;

import java.util.Date;

/**
 * Helper interface which holds some of the basic details about a RunningDinner.
 * 
 * @author i01002492
 * 
 */
public interface RunningDinnerInfo {

	/**
	 * Title of the running dinner (just used for display purposes in admin-area). Never null.
	 * 
	 * @return
	 */
	String getTitle();

	/**
	 * The date on which the running dinner shall be performed. Never null
	 * 
	 * @return
	 */
	Date getDate();

	/**
	 * Info about the city in which the running dinner shall take place.<br>
	 * Can be empty
	 * 
	 * @return
	 */
	String getCity();

	/**
	 * Email of the creator of a running dinner. Never null
	 * 
	 * @return
	 */
	String getEmail();

}
