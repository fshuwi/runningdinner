package org.runningdinner.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.runningdinner.core.Participant;
import org.runningdinner.core.RunningDinnerConfig;
import org.runningdinner.core.Team;
import org.runningdinner.core.model.AbstractEntity;

@Entity
public class RunningDinner extends AbstractEntity implements RunningDinnerInfo {

	private static final long serialVersionUID = -6099048543502048569L;

	@Column(length = 48)
	private String uuid;

	private String title;

	private String city;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	private String email;

	@Embedded
	private RunningDinnerConfig configuration;

	@OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Participant> participants;

	@OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Team> teams;

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

	public List<Participant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

}
