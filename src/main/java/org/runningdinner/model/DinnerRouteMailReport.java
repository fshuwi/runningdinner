package org.runningdinner.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "dinnerRoute")
@Access(AccessType.FIELD)
public class DinnerRouteMailReport extends BaseMailReport {

	private static final long serialVersionUID = 9078369885907078065L;

	public DinnerRouteMailReport(RunningDinner runningDinner) {
		super(runningDinner);
	}

	protected DinnerRouteMailReport() {
		super();
	}
}
