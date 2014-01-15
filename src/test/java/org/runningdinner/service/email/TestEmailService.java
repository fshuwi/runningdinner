package org.runningdinner.service.email;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-context.xml", "classpath:spring/mail-context.xml" })
@ActiveProfiles("junit")
public class TestEmailService {

	@Autowired
	EmailService emailService;

	@Test
	public void testSendRunningDinnerCreatedMail() {
		emailService.sendRunningDinnerCreatedMessage("clemensstich@googlemail.com", "ADMIN URL");
	}
}
