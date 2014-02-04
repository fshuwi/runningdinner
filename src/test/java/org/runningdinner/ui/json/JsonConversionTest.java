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

public class JsonConversionTest {

	List<SingleTeamParticipantChange> teamHostChanges = new ArrayList<SingleTeamParticipantChange>();

	@Before
	public void setUp() {
		teamHostChanges.add(new SingleTeamParticipantChange("team1", "p1"));
		teamHostChanges.add(new SingleTeamParticipantChange("team2", "p2"));
		teamHostChanges.add(new SingleTeamParticipantChange("team3", "p3"));
	}

	@Test
	public void testConvertToJson() throws JsonGenerationException, JsonMappingException, IOException {
		String json = getJsonObjectMapper().writeValueAsString(teamHostChanges);
		System.out.println(json);
	}

	@Test
	public void testConvertFromJson() throws JsonParseException, JsonMappingException, IOException {
		String json = "[{\"teamKey\":\"team1\",\"participantKey\":\"p1\"},{\"teamKey\":\"team2\",\"participantKey\":\"p2\"},{\"teamKey\":\"team3\",\"participantKey\":\"p3\"}]";
		List<SingleTeamParticipantChange> result = getJsonObjectMapper().readValue(json,
				new TypeReference<List<SingleTeamParticipantChange>>() {});
		assertEquals(3, result.size());
	}

	@Test
	public void testConvertFromSWitchTeamMembersJson() throws JsonParseException, JsonMappingException, IOException {
		String json = "[{\"participantKey\":\"p1\"},{\"participantKey\":\"p2\"}]";
		SwitchTeamMembers result = getJsonObjectMapper().readValue(json, new TypeReference<SwitchTeamMembers>() {});
		assertEquals(2, result.size());

	}

	protected ObjectMapper getJsonObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper;
	}
}
