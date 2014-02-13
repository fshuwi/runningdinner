package org.runningdinner.service.email;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.runningdinner.event.listener.NewRunningDinnerMailListener;
import org.runningdinner.events.NewRunningDinnerEvent;
import org.runningdinner.model.RunningDinner;
import org.runningdinner.service.email.mock.MailSenderMockInMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-context.xml", "classpath:spring/mail-context.xml" })
@ActiveProfiles("junit")
public class TestEmailService {

	@Autowired
	EmailService emailService;

	@Autowired
	NewRunningDinnerMailListener listener;

	MailSenderMockInMemory mockedMailSender;

	@Before
	public void setUp() throws Exception {
		mockedMailSender = (MailSenderMockInMemory)emailService.getMailSender();
	}

	@Test
	public void testSendRunningDinnerCreatedMail() {
		assertEquals(0, mockedMailSender.getMessages().size());
		emailService.sendRunningDinnerCreatedMessage("clemensstich@googlemail.com", "ADMIN URL");
		assertEquals(1, mockedMailSender.getMessages().size());
		assertEquals("clemensstich@googlemail.com", mockedMailSender.getMessages().iterator().next().getTo()[0]);
	}

	@Test
	public void testMailQueue() throws InterruptedException {

		assertEquals(0, mockedMailSender.getMessages().size());

		RunningDinner tmpDinner = new RunningDinner();
		tmpDinner.setUuid("uuid");
		tmpDinner.setEmail("recipient@recipient.com");

		// Simulate new event:
		NewRunningDinnerEvent event = new NewRunningDinnerEvent(this, tmpDinner);
		listener.onApplicationEvent(event);

		Thread.sleep(250); // Give worker threads a little time

		Set<SimpleMailMessage> messages = mockedMailSender.getMessages();
		assertEquals(1, messages.size());
		assertEquals("recipient@recipient.com", messages.iterator().next().getTo()[0]);
	}

	@After
	public void tearDown() throws Exception {
		mockedMailSender.removeAllMessages();
	}
}
