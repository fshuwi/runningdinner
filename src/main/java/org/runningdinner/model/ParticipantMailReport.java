package org.runningdinner.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "participant")
@Access(AccessType.FIELD)
public class ParticipantMailReport extends BaseMailReport {

	private static final long serialVersionUID = -6731581498314335850L;

	public ParticipantMailReport(RunningDinner runningDinner) {
		super(runningDinner);
	}

	protected ParticipantMailReport() {
		super();
	}
}
