package org.runningdinner.service.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.runningdinner.core.MealClass;
import org.runningdinner.core.Participant;
import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.core.ParticipantName;
import org.runningdinner.core.Team;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-context.xml", "classpath:spring/mail-context.xml" })
@ActiveProfiles("junit")
public class TestTeamMessageFormatter {

	private static String timeFormat = "HH:mm";
	private TeamArrangementMessageFormatter formatter;

	private static int participantCounter = 1;
	static final String ZIP = "79100 Freiburg";
	static final String EMAIL = "email";
	static final String MOBILE = "mobile";

	private String time1 = "19:00";
	private String time2 = "21:00";
	private SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat);

	protected MessageSource messages;

	@Before
	public void setUp() {
		Locale locale = Locale.GERMAN;
		// MessageSource is defined in web-context... just load it manually:
		ResourceBundleMessageSource tmp = new ResourceBundleMessageSource();
		tmp.setBasename("messages");
		messages = tmp;

		formatter = new TeamArrangementMessageFormatter(messages, locale, dateFormat);
		formatter.setMessageTemplate("{firstname} {lastname}/{meal}/{mealtime}/{host}/{partner}");
		formatter.setNonHostMessagePartTemplate("{partner}");
		formatter.setHostMessagePartTemplate("YOU");

	}

	@Test
	public void testFormatting() throws ParseException {
		Participant p1 = generateParticipant("F1 L1", "S 1", true);
		Participant p2 = generateParticipant("F2 L2", "S 2", false);

		Participant p3 = generateParticipant("F3 L3", "S 3", true);
		Participant p4 = generateParticipant("F4 L4", "S 4", false);

		Team t1 = new Team(1);
		MealClass m1 = MealClass.APPETIZER();
		m1.setTime(dateFormat.parse(time1));
		t1.setTeamMembers(new HashSet<Participant>(Arrays.asList(p1, p2)));
		t1.setMealClass(m1);

		Team t2 = new Team(2);
		MealClass m2 = MealClass.MAINCOURSE();
		m2.setTime(dateFormat.parse(time2));
		t2.setTeamMembers(new HashSet<Participant>(Arrays.asList(p3, p4)));
		t2.setMealClass(m2);

		String result = formatter.formatTeamMemberMessage(p1, t1);
		System.out.println(result);
		String[] resultParts = result.split("/");

		assertEquals(5, resultParts.length);
		assertEquals("F1 L1", resultParts[0]);
		assertEquals(m1.getLabel(), resultParts[1]);
		assertEquals(time1, resultParts[2]);
		assertEquals("YOU", resultParts[3]);
		assertTrue(resultParts[4].contains(ZIP));
		assertTrue(resultParts[4].contains(EMAIL));
		assertTrue(resultParts[4].contains(MOBILE));
		assertTrue(resultParts[4].contains("S 2"));
		assertTrue(resultParts[4].contains("F2 L2"));

		result = formatter.formatTeamMemberMessage(p4, t2);
		System.out.println(result);
		resultParts = result.split("/");

		assertEquals(5, resultParts.length);
		assertEquals("F4 L4", resultParts[0]);
		assertEquals(m2.getLabel(), resultParts[1]);
		assertEquals(time2, resultParts[2]);
		assertEquals("F3 L3", resultParts[3]); // Partner is host
		assertTrue(resultParts[4].contains(ZIP));
		assertTrue(resultParts[4].contains(EMAIL));
		assertTrue(resultParts[4].contains(MOBILE));
		assertTrue(resultParts[4].contains("S 3"));
		assertTrue(resultParts[4].contains("F3 L3"));

	}

	public static Participant generateParticipant(String fullname, String streetWithNr, boolean host) {
		Participant participant = new Participant(participantCounter++);
		participant.setName(ParticipantName.newName().withCompleteNameString(fullname));
		participant.setAddress(ParticipantAddress.parseFromString(streetWithNr + "\r\n" + ZIP));
		participant.setEmail(EMAIL);
		participant.setMobileNumber(MOBILE);
		participant.setHost(host);
		return participant;
	}
}
