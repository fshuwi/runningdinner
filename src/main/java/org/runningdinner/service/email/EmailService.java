package org.runningdinner.service.email;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.service.TeamRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Encapsulates the logic for sending emails
 * 
 * @author Clemens Stich
 * 
 */
public class EmailService {

	private MailSender mailSender;

	private SimpleMailMessage runningDinnerCreatedMessageTemplate;
	private SimpleMailMessage baseMessageTemplate;

	private MessageSource messageSource;

	/**
	 * Used for sending all mails to the same recipient (useful in test/Dev scenarios when real mail interaction shall be tested)
	 */
	private String testEmailRecipient;

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

	public void sendRunningDinnerCreatedMessage(final String recipientEmail, final String administrationUrl) {

		if (!isEmailValid(recipientEmail)) {
			return;
		}

		String email = getMailAddress(recipientEmail);

		SimpleMailMessage message = new SimpleMailMessage(runningDinnerCreatedMessageTemplate);
		message.setTo(email);
		String text = messageSource.getMessage("message.template.runningdinner.created", new Object[] { administrationUrl }, Locale.GERMAN);
		message.setText(text);

		LOGGER.info("Send running dinner created mail with size of {} characters to {}", text.length(), email);
		try {
			mailSender.send(message);
		}
		catch (Exception ex) {
			LOGGER.error("Could not send new running dinner email to recipient {}", email, ex);
		}
	}

	public void sendTeamArrangementMessages(final Collection<Team> teams, final TeamArrangementMessageFormatter formatter) {

		final String subject = formatter.getSubject();

		for (Team team : teams) {

			Set<Participant> allTeamMembers = team.getTeamMembers();
			for (Participant teamMember : allTeamMembers) {

				LOGGER.debug("Process {} of team {}", teamMember.getName().getFullnameFirstnameFirst(), team);

				final String teamMemberMail = teamMember.getEmail();
				if (!isEmailValid(teamMemberMail)) {
					continue;
				}

				final String email = getMailAddress(teamMemberMail);

				String messageText = formatter.formatTeamMemberMessage(teamMember, team);

				SimpleMailMessage mailMessage = new SimpleMailMessage(baseMessageTemplate);
				mailMessage.setSubject(subject);
				mailMessage.setTo(email);
				mailMessage.setText(messageText);

				LOGGER.info("Send mail with size of {} characters to {}", messageText.length(), email);

				try {
					mailSender.send(mailMessage);
				}
				catch (Exception ex) {
					LOGGER.error("Failed to send mail to {}", email, ex);
				}
			}
		}
	}

	public void sendDinnerRouteMessages(List<Team> teams, DinnerRouteMessageFormatter formatter) {

		final String subject = formatter.getSubject();

		for (Team team : teams) {

			LOGGER.debug("Process team {} for dinnerroute message", team);
			List<Team> teamDinnerRoute = TeamRouteBuilder.generateDinnerRoute(team);

			for (Participant teamMember : team.getTeamMembers()) {

				final String teamMemberEmail = teamMember.getEmail();
				if (!isEmailValid(teamMemberEmail)) {
					continue;
				}

				final String email = getMailAddress(teamMemberEmail);

				String messageText = formatter.formatDinnerRouteMessage(teamMember, team, teamDinnerRoute);

				SimpleMailMessage mailMessage = new SimpleMailMessage(baseMessageTemplate);
				mailMessage.setSubject(subject);
				mailMessage.setTo(email);
				mailMessage.setText(messageText);

				LOGGER.info("Send mail with size of {} characters to {}", messageText.length(), email);

				try {
					mailSender.send(mailMessage);
				}
				catch (Exception ex) {
					LOGGER.error("Failed to send mail to {}", email, ex);
				}
			}
		}

	}

	public void sendMessageToParticipants(final Collection<Participant> participants, final String subject, final String message) {

		for (Participant participant : participants) {

			LOGGER.debug("Process {}", participant.getName().getFullnameFirstnameFirst());

			final String participantEmail = participant.getEmail();
			if (!isEmailValid(participantEmail)) {
				continue;
			}

			final String email = getMailAddress(participantEmail);

			String messageText = message; // TODO: Use formatter to replace some templates

			SimpleMailMessage mailMessage = new SimpleMailMessage(baseMessageTemplate);
			mailMessage.setSubject(subject);
			mailMessage.setTo(email);
			mailMessage.setText(messageText);

			LOGGER.info("Send mail with size of {} characters to {}", messageText.length(), email);

			try {
				mailSender.send(mailMessage);
			}
			catch (Exception ex) {
				LOGGER.error("Failed to send mail to {}", email, ex);
			}
		}
	}

	protected boolean isEmailValid(final String email) {
		if (StringUtils.isEmpty(email) || !email.contains("@")) {
			LOGGER.error("{} is no valid email address", email);
			return false;
		}
		return true;
	}

	/**
	 * Convenience method to either get back the passed mailAddress (standard) or get the test-recipient-email-adress which is used to send
	 * all mails to.
	 * 
	 * @param mailAddress
	 * @return
	 */
	protected String getMailAddress(final String mailAddress) {
		if (StringUtils.isNotEmpty(testEmailRecipient)) {
			return testEmailRecipient;
		}
		return mailAddress;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setRunningDinnerCreatedMessageTemplate(SimpleMailMessage runningDinnerCreatedMessageTemplate) {
		this.runningDinnerCreatedMessageTemplate = runningDinnerCreatedMessageTemplate;
	}

	public void setBaseMessageTemplate(SimpleMailMessage baseMessageTemplate) {
		this.baseMessageTemplate = baseMessageTemplate;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setTestEmailRecipient(String testEmailRecipient) {
		this.testEmailRecipient = testEmailRecipient;
	}

	public MailSender getMailSender() {
		return mailSender;
	}

}
