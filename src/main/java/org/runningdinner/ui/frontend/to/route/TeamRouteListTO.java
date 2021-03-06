package org.runningdinner.ui.frontend.to.route;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("teamRouteList")
public class TeamRouteListTO {

	private String teamMemberNames;

	private List<TeamRouteEntryTO> teamRouteEntries = new ArrayList<TeamRouteEntryTO>();

	public String getTeamMemberNames() {
		return teamMemberNames;
	}

	public void setTeamMemberNames(String teamMemberNames) {
		this.teamMemberNames = teamMemberNames;
	}

	public List<TeamRouteEntryTO> getTeamRouteEntries() {
		return teamRouteEntries;
	}

	public void setTeamRouteEntries(List<TeamRouteEntryTO> teamRouteEntries) {
		this.teamRouteEntries = teamRouteEntries;
	}

	public void addTeamRouteEntry(TeamRouteEntryTO teamRouteEntry) {
		this.teamRouteEntries.add(teamRouteEntry);
	}

}
