package org.runningdinner.ui.json;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.runningdinner.ui.dto.SingleTeamHostChange;

public class TeamHostChangeJsonConversionTest {

	List<SingleTeamHostChange> teamHostChanges = new ArrayList<SingleTeamHostChange>();

	@Before
	public void setUp() {
		teamHostChanges.add(new SingleTeamHostChange("team1", "p1"));
		teamHostChanges.add(new SingleTeamHostChange("team2", "p2"));
		teamHostChanges.add(new SingleTeamHostChange("team3", "p3"));
	}

	@Test
	public void testConvertToJson() throws JsonGenerationException, JsonMappingException, IOException {
		String json = getJsonObjectMapper().writeValueAsString(teamHostChanges);
		System.out.println(json);
	}

	@Test
	public void testConvertFromJson() throws JsonParseException, JsonMappingException, IOException {
		String json = "[{\"teamKey\":\"team1\",\"newHostParticipantKey\":\"p1\"},{\"teamKey\":\"team2\",\"newHostParticipantKey\":\"p2\"},{\"teamKey\":\"team3\",\"newHostParticipantKey\":\"p3\"}]";
		List<SingleTeamHostChange> result = getJsonObjectMapper().readValue(json, new TypeReference<List<SingleTeamHostChange>>() {});
		assertEquals(3, result.size());
	}

	protected ObjectMapper getJsonObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper;
	}
}
