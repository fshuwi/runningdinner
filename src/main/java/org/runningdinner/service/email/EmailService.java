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
import org.runningdinner.core.util.CoreUtil;
import org.runningdinner.exceptions.MailServerConnectionFailedException;
import org.runningdinner.exceptions.MailServerConnectionFailedException.MAIL_CONNECTION_ERROR;
import org.runningdinner.model.ChangeTeamHost;
import org.runningdinner.service.impl.UrlGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.google.common.base.Optional;

/**
 * Encapsulates the logic for sending emails
 * 
 * @author Clemens Stich
 * 
 */
public class EmailService {

	private MailSender mailSender;

	private MessageSource messageSource;

	private UrlGenerator urlGenerator;
	
	private String defaultReplyTo;
	private String defaultFrom;

	/**
	 * Used for sending all mails to the same recipient (useful in test/Dev scenarios when real mail interaction shall be tested)
	 */
	private String testEmailRecipient;

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

	public void sendRunningDinnerCreatedMessage(final String recipientEmail, final String administrationUrl) {

		final Locale locale = CoreUtil.getDefaultLocale();

		if (!isEmailValid(recipientEmail)) {
			return;
		}

		String email = getMailAddress(recipientEmail);

		SimpleMailMessage message = createSimpleMailMessage(Optional.<MailServerSettings> absent());
		message.setSubject(messageSource.getMessage("message.subject.runningdinner.created", null, locale));
		message.setTo(email);
		String text = messageSource.getMessage("message.template.runningdinner.created", new Object[] { administrationUrl }, locale);
		message.setText(text);

		LOGGER.info("Send running dinner created mail with size of {} characters to {}", text.length(), email);
		try {
			mailSender.send(message);
		}
		catch (Exception ex) {
			LOGGER.error("Could not send new running dinner email to recipient {}", email, ex);
		}
	}

	public Map<String, Boolean> sendTeamArrangementMessages(final Collection<Team> teams, final TeamArrangementMessageFormatter formatter,
			final Optional<MailServerSettings> customMailServerSettings) {

		Map<String, Boolean> sendingResults = new HashMap<String, Boolean>();

		MailSender mailSenderActive = getActiveMailSender(customMailServerSettings);

		final String subject = formatter.getSubject();
		for (Team team : teams) {

			Set<Participant> allTeamMembers = team.getTeamMembers();

			for (Participant teamMember : allTeamMembers) {

				LOGGER.debug("Process {} of team {}", teamMember.getName().getFullnameFirstnameFirst(), team);

				final String messageText = formatter.formatTeamMemberMessage(teamMember, team);
				sendMessageToParticipant(teamMember, subject, messageText, mailSenderActive, customMailServerSettings, sendingResults,
						formatter.getLocale());
			}
		}

		return sendingResults;
	}

	public Map<String, Boolean> sendDinnerRouteMessages(List<Team> teams, DinnerRouteMessageFormatter formatter,
			final Optional<MailServerSettings> customMailServerSettings) {

		final Map<String, Boolean> sendingResults = new HashMap<String, Boolean>();
		final MailSender mailSenderActive = getActiveMailSender(customMailServerSettings);
		final String subject = formatter.getSubject();

		for (Team team : teams) {
			LOGGER.debug("Process team {} for dinnerroute message", team);
			List<Team> teamDinnerRoute = TeamRouteBuilder.generateDinnerRoute(team);

			for (Participant teamMember : team.getTeamMembers()) {
				final String messageText = formatter.formatDinnerRouteMessage(teamMember, team, teamDinnerRoute);
				sendMessageToParticipant(teamMember, subject, messageText, mailSenderActive, customMailServerSettings, sendingResults,
						formatter.getLocale());
			}
		}

		return sendingResults;
	}

	protected void sendMessageToParticipant(final Participant participant, final String subject, final String messageText,
			final MailSender mailSender, final Optional<MailServerSettings> customMailServerSettings,
			final Map<String, Boolean> sendingResults, final Locale locale) {

		final String teamMemberEmail = participant.getEmail();
		if (!isEmailValid(teamMemberEmail)) {
			putInvalidMailToResults(sendingResults, participant, teamMemberEmail, locale);
			return;
		}

		final String email = getMailAddress(teamMemberEmail);

		SimpleMailMessage mailMessage = createSimpleMailMessage(customMailServerSettings);
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

	protected void putInvalidMailToResults(final Map<String, Boolean> sendingResults, final Participant teamMember,
			final String teamMemberEmail, Locale locale) {
		String failedMail = teamMemberEmail;
		if (StringUtils.isEmpty(teamMemberEmail)) {
			failedMail = teamMember.getName().getFullnameFirstnameFirst() + " ("
					+ messageSource.getMessage("message.template.no.email", null, locale) + ")";
		}
		sendingResults.put(failedMail, false);
	}

	public Map<String, Boolean> sendMessageToParticipants(final List<Participant> participants,
			final ParticipantMessageFormatter formatter, final Optional<MailServerSettings> customMailServerSettings) {

		final Map<String, Boolean> sendingResults = new HashMap<String, Boolean>();
		final MailSender mailSenderActive = getActiveMailSender(customMailServerSettings);
		final String subject = formatter.getSubject();

		for (Participant participant : participants) {
			final String messageText = formatter.formatParticipantMessage(participant);
			sendMessageToParticipant(participant, subject, messageText, mailSenderActive, customMailServerSettings, sendingResults,
					formatter.getLocale());
		}

		return sendingResults;
	}

	public void sendTeamHostChangedMail(final Team team, final ChangeTeamHost changeTeamHost) {

		final Locale locale = CoreUtil.getDefaultLocale();

		final Map<String, Boolean> sendingResults = new HashMap<String, Boolean>();
		final MailSender mailSenderActive = getActiveMailSender(Optional.<MailServerSettings> absent());

		final String subject = messageSource.getMessage("message.subject.team.host.changed", null, locale);
		
		TeamHostChangeFormatter formatter = new TeamHostChangeFormatter(messageSource, locale, urlGenerator);

		Participant teamHostEditor = team.getTeamMemberByKey(changeTeamHost.getModificationParticipantKey());
		
		Set<Participant> teamMembers = team.getTeamMembers();
		for (Participant teamMember : teamMembers) {

			// Don't send mail to participant which changed the host settings
			if (!changeTeamHost.isSendMailToMe() && teamHostEditor.equals(teamMember)) {
				continue;
			}

			final String comment = changeTeamHost.getComment();
			
			final String message = formatter.formatTeamHostChangeMessage(team, teamMember, comment, teamHostEditor);
			
			sendMessageToParticipant(teamMember, subject, message, mailSenderActive, Optional.<MailServerSettings> absent(),
					sendingResults, locale);
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
			return testEmailRecipient.trim();
		}
		return mailAddress.trim();
	}

	/**
	 * Gets the concrete mailsender instance to be used for sending mails. Either the custom mailserver of user if present or the built-in
	 * running dinner mailserver
	 * 
	 * @param customMailServerSettings
	 * @return
	 */
	protected MailSender getActiveMailSender(Optional<MailServerSettings> customMailServerSettings) {
		if (customMailServerSettings.isPresent()) {
			return createCustomMailSender(customMailServerSettings.get());
		}
		return this.mailSender;
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

	private MailSender createCustomMailSender(final MailServerSettings mailServerSettings) {
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

	/**
	 * If custom mail server shall be used, we enrich the message with appropriate "from" and "replyto" fields. Otherwise we use the default
	 * settings.
	 * 
	 * @param mailMessage
	 * @param customMailServerSettings
	 */
	protected SimpleMailMessage createSimpleMailMessage(final Optional<MailServerSettings> customMailServerSettings) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setSubject("Running Dinner");

		if (!customMailServerSettings.isPresent()) {
			simpleMailMessage.setReplyTo(defaultReplyTo);
			simpleMailMessage.setFrom(defaultFrom);
			return simpleMailMessage;
		}

		MailServerSettings mailServerSettings = customMailServerSettings.get();
		simpleMailMessage.setFrom(mailServerSettings.getFrom());

		if (StringUtils.isNotEmpty(mailServerSettings.getReplyTo())) {
			simpleMailMessage.setReplyTo(mailServerSettings.getReplyTo());
		}

		return simpleMailMessage;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
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

	public void setDefaultReplyTo(String defaultReplyTo) {
		this.defaultReplyTo = defaultReplyTo;
	}

	public void setDefaultFrom(String defaultFrom) {
		this.defaultFrom = defaultFrom;
	}

	public void setUrlGenerator(UrlGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}
}
