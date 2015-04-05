package org.runningdinner.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.runningdinner.core.NoPossibleRunningDinnerException;
import org.runningdinner.core.Team;
import org.runningdinner.core.util.CoreUtil;
import org.runningdinner.model.TeamMailReport;
import org.runningdinner.service.email.TeamArrangementMessageFormatter;
import org.runningdinner.service.impl.UrlGenerator;
import org.runningdinner.test.util.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { TestUtil.APP_CONTEXT, TestUtil.MAIL_CONTEXT })
@ActiveProfiles("junit")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TestMailReports {

	@Autowired
	protected RunningDinnerService runningDinnerService;
	
	@Autowired
	protected CommunicationService communicationService;

	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	protected UrlGenerator urlGenerator;

	@Test
	public void testNoMailReportAvailable() throws NoPossibleRunningDinnerException {
		String uuid = "test";
		TestUtil.createExampleDinnerAndVisitationPlans(runningDinnerService, uuid);

		TeamMailReport lastTeamMailReport = communicationService.findLastTeamMailReport(uuid);
		assertEquals(null, lastTeamMailReport);
	}

	@Test
	public void testMailReportAvailable() throws NoPossibleRunningDinnerException, InterruptedException {
		String uuid = "test";
		TestUtil.createExampleDinnerAndVisitationPlans(runningDinnerService, uuid);

		List<Team> teams = runningDinnerService.loadRegularTeamsFromDinner(uuid);
		List<String> teamKeys = CoreUtil.getNaturalKeysForEntities(teams);

		TeamArrangementMessageFormatter messageFormatter = new TeamArrangementMessageFormatter(messageSource, Locale.GERMAN);
		messageFormatter.setMessageTemplate(messageSource.getMessage("message.template.teams", null, Locale.GERMAN));
		messageFormatter.setHostMessagePartTemplate(messageSource.getMessage("message.template.teams.host", null, Locale.GERMAN));
		messageFormatter.setNonHostMessagePartTemplate(messageSource.getMessage("message.template.teams.nonhost", null, Locale.GERMAN));
		messageFormatter.setUrlGenerator(urlGenerator);
		communicationService.sendTeamMessages(uuid, teamKeys, messageFormatter, null);

		TeamMailReport mailReport = communicationService.findLastTeamMailReport(uuid);
		assertEquals(true, mailReport.isSending());
		assertEquals(false, mailReport.isInterrupted());
		assertTrue("Sending starting date of mail report must be before current time", new Date().after(mailReport.getSendingStartDate()));

		Thread.sleep(18 * 10 + 300); // Each participant needs 10 ms for mail being sent (-> MailSEnderMockInMemory)

		mailReport = communicationService.findLastTeamMailReport(uuid);
		assertEquals(false, mailReport.isSending());
		assertEquals(false, mailReport.isInterrupted());
		assertEquals(18, mailReport.getSucceededMails().size());
		assertEquals(0, mailReport.getFailedMails().size());
	}
}
