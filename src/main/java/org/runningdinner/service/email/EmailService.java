package org.runningdinner.service.email;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.core.dinnerplan.TeamRouteBuilder;
import org.runningdinner.exceptions.MailServerConnectionFailedException;
import org.runningdinner.exceptions.MailServerConnectionFailedException.MAIL_CONNECTION_ERROR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

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

	public Map<String, Boolean> sendTeamArrangementMessages(final Collection<Team> teams, final TeamArrangementMessageFormatter formatter) {

		Map<String, Boolean> sendingResults = new HashMap<String, Boolean>();

		final String subject = formatter.getSubject();
		for (Team team : teams) {

			Set<Participant> allTeamMembers = team.getTeamMembers();
			for (Participant teamMember : allTeamMembers) {

				LOGGER.debug("Process {} of team {}", teamMember.getName().getFullnameFirstnameFirst(), team);

				final String teamMemberMail = teamMember.getEmail();
				if (!isEmailValid(teamMemberMail)) {
					putInvalidMailToResults(sendingResults, teamMember, teamMemberMail, formatter.getLocale());
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
					sendingResults.put(email, true);
				}
				catch (Exception ex) {
					sendingResults.put(email, false);
					LOGGER.error("Failed to send mail to {}", email, ex);
				}
			}
		}

		return sendingResults;
	}

	public Map<String, Boolean> sendDinnerRouteMessages(List<Team> teams, DinnerRouteMessageFormatter formatter) {

		Map<String, Boolean> sendingResults = new HashMap<String, Boolean>();

		final String subject = formatter.getSubject();
		for (Team team : teams) {

			LOGGER.debug("Process team {} for dinnerroute message", team);
			List<Team> teamDinnerRoute = TeamRouteBuilder.generateDinnerRoute(team);

			for (Participant teamMember : team.getTeamMembers()) {

				final String teamMemberEmail = teamMember.getEmail();
				if (!isEmailValid(teamMemberEmail)) {
					putInvalidMailToResults(sendingResults, teamMember, teamMemberEmail, formatter.getLocale());
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
					sendingResults.put(email, true);
				}
				catch (Exception ex) {
					sendingResults.put(email, false);
					LOGGER.error("Failed to send mail to {}", email, ex);
				}
			}
		}

		return sendingResults;
	}

	protected void putInvalidMailToResults(final Map<String, Boolean> sendingResults, final Participant teamMember,
			final String teamMemberEmail, Locale locale) {
		String failedMail = teamMemberEmail;
		if (StringUtils.isEmpty(teamMemberEmail)) {
			failedMail = teamMember.getName().getFullnameFirstnameFirst() + " ("
					+ messageSource.getMessage("message.template.no.email", null, locale) + ")";
		}
		sendingResults.put(failedMail, false);
	}

	public Map<String, Boolean> sendMessageToParticipants(final List<Participant> participants, final ParticipantMessageFormatter formatter) {

		final Map<String, Boolean> sendingResults = new HashMap<String, Boolean>();

		final String subject = formatter.getSubject();

		for (Participant participant : participants) {

			LOGGER.debug("Process {}", participant.getName().getFullnameFirstnameFirst());

			final String participantEmail = participant.getEmail();
			if (!isEmailValid(participantEmail)) {
				putInvalidMailToResults(sendingResults, participant, participantEmail, formatter.getLocale());
				continue;
			}

			final String email = getMailAddress(participantEmail);

			String messageText = formatter.formatParticipantMessage(participant);

			SimpleMailMessage mailMessage = new SimpleMailMessage(baseMessageTemplate);
			mailMessage.setSubject(subject);
			mailMessage.setTo(email);
			mailMessage.setText(messageText);

			LOGGER.info("Send mail with size of {} characters to {}", messageText.length(), email);

			try {
				mailSender.send(mailMessage);
				sendingResults.put(email, true);
			}
			catch (Exception ex) {
				sendingResults.put(email, false);
				LOGGER.error("Failed to send mail to {}", email, ex);
			}
		}

		return sendingResults;
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
			return testEmailRecipient.trim();
		}
		return mailAddress.trim();
	}

	/**
	 * Tries to send a tets email to the passed testEmailAddress with using the passed subject and message.<br>
	 * If sending succeeds then no exception will be thrown.
	 * 
	 * @param mailServerSettings The settings of the mail server on which it shall be tried to send the test mail
	 * @param testEmailAddress
	 * @param testSubject
	 * @param testMessage
	 * @throws MailServerConnectionFailedException Thrown when test email could not be sent
	 */
	public void checkEmailConnection(MailServerSettings mailServerSettings, String testEmailAddress, String testSubject, String testMessage)
			throws MailServerConnectionFailedException {
		MailSender customMailSender = createCustomMailSender(mailServerSettings);

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setSubject(testSubject);
		mailMessage.setTo(testEmailAddress);
		mailMessage.setText(testMessage);
		mailMessage.setFrom(mailServerSettings.getFrom());

		if (StringUtils.isNotEmpty(mailServerSettings.getReplyTo())) {
			mailMessage.setReplyTo(mailServerSettings.getReplyTo());
		}

		try {
			customMailSender.send(mailMessage);
		}
		catch (MailAuthenticationException authEx) {
			throw new MailServerConnectionFailedException(authEx).setMailConnectionError(MAIL_CONNECTION_ERROR.AUTHENTICATION);
		}
		catch (MailSendException sendEx) {
			throw new MailServerConnectionFailedException(sendEx).setMailConnectionError(MAIL_CONNECTION_ERROR.SEND);
		}
		catch (Exception ex) {
			throw new MailServerConnectionFailedException(ex).setMailConnectionError(MAIL_CONNECTION_ERROR.UNKNOWN);
		}
	}

	private MailSender createCustomMailSender(MailServerSettings mailServerSettings) {
		JavaMailSenderImpl result = new JavaMailSenderImpl();
		result.setHost(mailServerSettings.getMailServer());

		if (mailServerSettings.hasMailServerPort()) {
			result.setPort(mailServerSettings.getMailServerPort());
		}

		if (mailServerSettings.isUseAuth()) {
			result.setUsername(mailServerSettings.getUsername());
			result.setPassword(mailServerSettings.getPassword());
			result.getJavaMailProperties().put("mail.smtp.auth", "true");
		}
		else {
			result.getJavaMailProperties().put("mail.smtp.auth", "false");
		}

		boolean useTls = mailServerSettings.isUseTls();
		result.getJavaMailProperties().put("mail.smtp.starttls.enable", String.valueOf(useTls));
		result.setDefaultEncoding("UTF-8");

		return result;
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
