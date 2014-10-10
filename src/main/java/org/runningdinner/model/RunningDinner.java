package org.runningdinner.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.core.Team;
import org.runningdinner.core.model.AbstractEntity;

/**
 * Central entity of application.<br>
 * User can create RunnignDinner instances and administrate them later.<br>
 * RunningDinner instance contains basic info details, the configuration options
 * of the dinner and a very simple "workflow"-state about administration
 * activities.<br>
 * Furthermore it contains all participants and the team-arrangements of the
 * participants including the visitation-plans (dinner-routes) for each regular
 * team.<br>
 * A dinner instance is identified by an (on creation) generated UUID.
 * 
 * @author i01002492
 * 
 */
@Entity
@Access(AccessType.FIELD)
public class RunningDinner extends AbstractEntity implements RunningDinnerInfo {

	private static final long serialVersionUID = -6099048543502048569L;

	@Column(length = 48, unique = true, nullable = false)
	private String uuid;

	@Column(nullable = false)
	private String title;

	private String city;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	private String email;

	@Embedded
	private RunningDinnerConfig configuration;

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumn(name = "dinner_id")
	@OrderBy(value = "participantNumber")
	private Set<Participant> participants;

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinColumn(name = "dinner_id")
	@OrderBy(value = "teamNumber")
	private Set<Team> teams;

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinTable(name = "NotAssignedParticipant", joinColumns = @JoinColumn(name = "dinner_id"), inverseJoinColumns = @JoinColumn(name = "participant_id"))
	@OrderBy(value = "participantNumber")
	private Set<Participant> notAssignedParticipants;

	public RunningDinner() {
		super();
	}

	/**
	 * UUID which is used to find a RunningDinner instance
	 * 
	 * @return
	 */
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * The email address of the creator of the RunningDinner
	 */
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public RunningDinnerConfig getConfiguration() {
		return configuration;
	}

	public void setConfiguration(RunningDinnerConfig configuration) {
		this.configuration = configuration;
	}

	/**
	 * Retrieves all participants of this running dinner
	 * 
	 * @return
	 */
	public List<Participant> getParticipants() {
		if (participants == null) {
			return Collections.emptyList();
		}
		ArrayList<Participant> result = new ArrayList<Participant>(participants);
		return result;
	}

	public void setParticipants(List<Participant> participants) {
		this.participants = new HashSet<Participant>(participants);
	}

	/**
	 * Retrieves all regular teams of this dinner.<br>
	 * The result may be empty (e.g. if there exist no teams yet)
	 * 
	 * @return
	 */
	public Set<Team> getTeams() {
		if (teams == null) {
			return Collections.emptySet();
		}
		return teams;
	}

	public void setTeams(Collection<Team> teams) {
		this.teams = new HashSet<Team>(teams);
	}

	/**
	 * Retrieves all participants that could not be assigned into teams of this
	 * dinner.<br>
	 * The result may be empty (e.g. if there exist no teams yet or if all
	 * participants could successfully be assigned into teams)
	 * 
	 * @return
	 */
	public List<Participant> getNotAssignedParticipants() {
		if (notAssignedParticipants == null) {
			return Collections.emptyList();
		}
		ArrayList<Participant> result = new ArrayList<Participant>(
				notAssignedParticipants);
		return result;
	}

	public void setNotAssignedParticipants(
			List<Participant> notAssignedParticipants) {
		this.notAssignedParticipants = new HashSet<Participant>(
				notAssignedParticipants);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(11, 31).append(getUuid()).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		RunningDinner other = (RunningDinner) obj;
		return new EqualsBuilder().append(getUuid(), other.getUuid())
				.isEquals();
	}

	@Override
	public String toString() {
		return getTitle() + " - " + getUuid();
	}

}
