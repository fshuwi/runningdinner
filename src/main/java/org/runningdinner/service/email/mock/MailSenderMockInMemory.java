package org.runningdinner.service.email.mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * MailSender implementation that just stores its received messages in an internal collection.<br>
 * Should just be used in test environments.
 * 
 * @author i01002492
 * 
 */
public class MailSenderMockInMemory implements MailSender {

	protected Set<SimpleMailMessage> messages = new HashSet<SimpleMailMessage>();

	@Override
	public void send(SimpleMailMessage simpleMessage) throws MailException {
		this.send(new SimpleMailMessage[] { simpleMessage });
	}

	@Override
	public void send(SimpleMailMessage[] simpleMessages) throws MailException {
		this.messages.addAll(Arrays.asList(simpleMessages));
	}

	public Set<SimpleMailMessage> getMessages() {
		return messages;
	}

	public void removeAllMessages() {
		this.messages.clear();
	}

}
