package org.runningdinner.service.email.mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * MailSender implementation that just stores its received messages in an internal collection.<br>
 * Should just be used in test environments.
 * 
 * @author Clemens Stich
 * 
 */
public class MailSenderMockInMemory implements MailSender {

	protected Set<SimpleMailMessage> messages = new HashSet<SimpleMailMessage>();

	private static Logger LOGGER = LoggerFactory.getLogger(MailSenderMockInMemory.class);

	@Override
	public void send(SimpleMailMessage simpleMessage) throws MailException {
		this.send(new SimpleMailMessage[] { simpleMessage });
	}

	@Override
	public void send(SimpleMailMessage[] simpleMessages) throws MailException {
		if (simpleMessages == null) {
			return;
		}
		if (simpleMessages.length == 1) {
			LOGGER.info("Sending mail to {}", simpleMessages[0].getTo()[0]); // To-field is actually always field... anyway this is just a
																				// mock
		}
		else {
			LOGGER.info("Sending {} mails", simpleMessages.length);
		}

		this.messages.addAll(Arrays.asList(simpleMessages));
	}

	public Set<SimpleMailMessage> getMessages() {
		return messages;
	}

	public void removeAllMessages() {
		this.messages.clear();
	}

}
