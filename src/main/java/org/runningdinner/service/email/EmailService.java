package org.runningdinner.service.email;

import java.util.Collection;
import java.util.Locale;

import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class EmailService {

	private MailSender mailSender;

	private SimpleMailMessage runningDinnerCreatedMessageTemplate;
	private SimpleMailMessage participantsMessageTemplate;
	private SimpleMailMessage teamArrangmentMessageTemplate;
	private SimpleMailMessage dinnerRouteMessageTemplate;
	private SimpleMailMessage singleTeamMessageTemplate;

	private MessageSource emailMessageSource;

	public void sendRunningDinnerCreatedMessage(final String recipientEmail, final String administrationUrl) {
		SimpleMailMessage message = new SimpleMailMessage(runningDinnerCreatedMessageTemplate);
		message.setTo(recipientEmail);
		String text = emailMessageSource.getMessage("create.runningdinner.template", new Object[] { administrationUrl }, Locale.GERMAN);
		message.setText(text);

		mailSender.send(message); // TODO: Exception Handling
	}

	public void sendMessageToAllParticipants(final Collection<Participant> participants, final String subject, final String message) {

	}

	public void sendTeamArrangementMessage(final Collection<Team> teams, final String subject, final String message) {

	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setRunningDinnerCreatedMessageTemplate(SimpleMailMessage runningDinnerCreatedMessageTemplate) {
		this.runningDinnerCreatedMessageTemplate = runningDinnerCreatedMessageTemplate;
	}

	public void setParticipantsMessageTemplate(SimpleMailMessage participantsMessageTemplate) {
		this.participantsMessageTemplate = participantsMessageTemplate;
	}

	public void setTeamArrangmentMessageTemplate(SimpleMailMessage teamArrangmentMessageTemplate) {
		this.teamArrangmentMessageTemplate = teamArrangmentMessageTemplate;
	}

	public void setDinnerRouteMessageTemplate(SimpleMailMessage dinnerRouteMessageTemplate) {
		this.dinnerRouteMessageTemplate = dinnerRouteMessageTemplate;
	}

	public void setSingleTeamMessageTemplate(SimpleMailMessage singleTeamMessageTemplate) {
		this.singleTeamMessageTemplate = singleTeamMessageTemplate;
	}

	public void setEmailMessageSource(MessageSource emailMessageSource) {
		this.emailMessageSource = emailMessageSource;
	}

}
