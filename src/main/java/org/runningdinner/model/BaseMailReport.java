package org.runningdinner.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.model.AbstractEntity;
import org.runningdinner.core.util.CoreUtil;

/**
 * Simple Base class for tracking the state of a sending emails task.<br>
 * It holds mainly the information about the sending state and the information
 * about the failed and/or succeeded mails that have been sent.<br>
 * <br>
 * Note: Even if all mail addresses are marked as being successful sent, this
 * must not be true, as we can just track whether we could submit the email
 * messages to the concrete mail server.
 * 
 * @author Clemens Stich
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "mailType", discriminatorType = DiscriminatorType.STRING)
@Access(AccessType.FIELD)
@Table(name = "MailReport")
public abstract class BaseMailReport extends AbstractEntity {

    private static final long serialVersionUID = -66572458843072687L;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date sendingStartDate;

    protected boolean sending = false;

    protected boolean interrupted = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "MailAddressStatusMapping")
    protected Map<String, Boolean> mailAddressStatusMapping;

    // @OneToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn(name = "dinner_id", unique = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dinner_id", unique = false)
    protected RunningDinner runningDinner;

    protected BaseMailReport() {
	// JPA
    }

    public BaseMailReport(RunningDinner runningDinner) {
	this.runningDinner = runningDinner;
    }

    /**
     * Gets the date when the sending task was started
     * 
     * @return
     */
    public Date getSendingStartDate() {
	return sendingStartDate;
    }

    /**
     * Checks whether a sending is ongoing
     * 
     * @return
     */
    public boolean isSending() {
	return sending;
    }

    /**
     * Indicates whether there this job for sending the mails was interrupted
     * (e.g. by container shutdown)
     * 
     * @return
     */
    public boolean isInterrupted() {
	return interrupted;
    }

    public void interrupt() {
	this.interrupted = true;
    }

    /**
     * Indicates a new sending task with current date
     */
    public void applyNewSending() {
	this.sendingStartDate = new Date();
	this.sending = true;
    }

    /**
     * Indicates that the sending task is finished now and passes all mail
     * addresses with a boolean flag (successful/failed) as a result.
     * 
     * @param mailAddressStatusMappings
     */
    public void applySendingFinished(
	    Map<String, Boolean> mailAddressStatusMappings) {
	this.sending = false;
	this.interrupted = false;
	this.mailAddressStatusMapping = mailAddressStatusMappings;
    }

    /**
     * Returns the status of all sent emails as a map with each email as key and
     * a boolean indicating success or failure.
     * 
     * @return
     */
    public Map<String, Boolean> getMailAddressStatusMapping() {
	return mailAddressStatusMapping;
    }

    /**
     * Gets all mail addresses that could not be sent
     * 
     * @return
     */
    public Set<String> getFailedMails() {
	return filterMails(false);
    }

    /**
     * Returns a comma separated string containing all mail address that could
     * not be sent
     * 
     * @return
     */
    public String getFailedMailsAsString() {
	Set<String> failedMails = filterMails(false);
	if (CoreUtil.isEmpty(failedMails)) {
	    return StringUtils.EMPTY;
	}
	StringBuilder result = new StringBuilder();
	int cnt = 0;
	for (String failedMail : failedMails) {
	    if (cnt++ > 0) {
		result.append(", ");
	    }
	    result.append(failedMail);
	}
	return result.toString();
    }

    /**
     * Gets all mail addresses that could successfully be sent
     * 
     * @return
     */
    public Set<String> getSucceededMails() {
	return filterMails(true);
    }

    protected Set<String> filterMails(boolean status) {
	if (mailAddressStatusMapping == null
		|| mailAddressStatusMapping.size() == 0) {
	    return Collections.emptySet();
	}

	Set<String> result = new HashSet<String>();
	for (Entry<String, Boolean> entry : mailAddressStatusMapping.entrySet()) {
	    if (entry.getValue().booleanValue() == status) {
		result.add(entry.getKey());
	    }
	}

	return result;
    }

    public RunningDinner getRunningDinner() {
	return runningDinner;
    }

    public void setRunningDinner(RunningDinner runningDinner) {
	this.runningDinner = runningDinner;
    }

    @Override
    public String toString() {
	return "Report-Status: "
		+ isSending()
		+ "; Sending-Start: "
		+ CoreUtil.getFormattedTime(getSendingStartDate(),
			CoreUtil.getDefaultDateFormat(), "Unknown");
    }

}
