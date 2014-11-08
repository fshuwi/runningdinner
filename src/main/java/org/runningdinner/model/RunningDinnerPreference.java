package org.runningdinner.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.runningdinner.core.model.AbstractEntity;

@Entity
@Access(AccessType.FIELD)
public class RunningDinnerPreference extends AbstractEntity {

	private static final long serialVersionUID = 3460406979540058769L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dinner_id", unique = false)
	protected RunningDinner runningDinner;

	@Column(nullable = false, length = 255)
	protected String preferenceName;

	@Column(nullable = false, length = 4096)
	protected String preferenceValue;

	protected RunningDinnerPreference() {
		// JPA
	}

	public RunningDinnerPreference(RunningDinner runningDinner) {
		this.runningDinner = runningDinner;
	}

	public RunningDinner getRunningDinner() {
		return runningDinner;
	}

	public void setRunningDinner(RunningDinner runningDinner) {
		this.runningDinner = runningDinner;
	}

	public String getPreferenceName() {
		return preferenceName;
	}

	public void setPreferenceName(String preferenceName) {
		this.preferenceName = preferenceName;
	}

	public String getPreferenceValue() {
		return preferenceValue;
	}

	public void setPreferenceValue(String preferenceValue) {
		this.preferenceValue = preferenceValue;
	}

	@Override
	public String toString() {
		return getPreferenceName() + ": " + getPreferenceValue();
	}
}
