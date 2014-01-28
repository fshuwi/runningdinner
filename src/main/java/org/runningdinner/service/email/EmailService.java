package org.runningdinner.service.email;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.ui.dto.FinalizeTeamsModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class EmailService {

	private MailSender mailSender;

	private SimpleMailMessage runningDinnerCreatedMessageTemplate;
	private SimpleMailMessage baseMessageTemplate;

	private MessageSource emailMessageSource;

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

	public void sendRunningDinnerCreatedMessage(final String recipientEmail, final String administrationUrl) {

		SimpleMailMessage message = new SimpleMailMessage(runningDinnerCreatedMessageTemplate);
		message.setTo(recipientEmail);
		String text = emailMessageSource.getMessage("create.runningdinner.template", new Object[] { administrationUrl }, Locale.GERMAN);
		LOGGER.info("Send running dinner created mail with size of {} characters to {}", text.length(), recipientEmail);
		message.setText(text);

		mailSender.send(message); // TODO: Exception Handling
	}

	public void sendTeamArrangementMessages(final Collection<Team> teams, final FinalizeTeamsModel finalizeTeamsModel) {

		TeamArrangementMessageFormatter formatter = new TeamArrangementMessageFormatter(finalizeTeamsModel);

		final String subject = finalizeTeamsModel.getSubject();

		int numTeams = 0;
		for (Team team : teams) {

			Set<Participant> allTeamMembers = team.getTeamMembers();
			for (Participant teamMember : allTeamMembers) {

				LOGGER.debug("Process {} of team {}", teamMember.getName().getFullnameFirstnameFirst(), team);

				String email = teamMember.getEmail();
				if (StringUtils.isEmpty(email) || !email.contains("@")) {
					LOGGER.error("Team-Member {} of team {} has no valid email address", teamMember, team);
					continue;
				}

				email = "clemensstich@web.de"; // TODO: Remove!!!

				String messageText = formatter.formatTeamMemberMessage(teamMember, team);

				SimpleMailMessage mailMessage = new SimpleMailMessage(baseMessageTemplate);
				mailMessage.setSubject(subject);
				mailMessage.setTo(email);
				mailMessage.setText(messageText);

				LOGGER.info("Send mail with size of {} characters to {}", messageText.length(), email);

				mailSender.send(mailMessage);
			}

			if (numTeams++ >= 2) {
				break; // TODO REmove
			}
		}
	}

	public void sendMessageToAllParticipants(final Collection<Participant> participants, final String subject, final String message) {

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

	public void setEmailMessageSource(MessageSource emailMessageSource) {
		this.emailMessageSource = emailMessageSource;
	}

}
