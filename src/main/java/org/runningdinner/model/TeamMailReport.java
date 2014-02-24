package org.runningdinner.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "team")
@Access(AccessType.FIELD)
public class TeamMailReport extends BaseMailReport {

	private static final long serialVersionUID = -7838678098357489019L;

	public TeamMailReport(RunningDinner runningDinner) {
		super(runningDinner);
	}

	protected TeamMailReport() {
		super();
	}
}
