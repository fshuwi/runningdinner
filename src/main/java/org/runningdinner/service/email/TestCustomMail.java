package org.runningdinner.service.email;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class TestCustomMail {

	public static void main(String[] args) {
		
		MailServerSettingsImpl mailServerSettings = new MailServerSettingsImpl();
		mailServerSettings.setMailServer("mail.gmx.net");
		mailServerSettings.setMailServerPort(587);
		mailServerSettings.setFrom("runyourdinner@gmx.de");
		mailServerSettings.setUseAuth(true);
		mailServerSettings.setUseTls(true);
		mailServerSettings.setUsername("runyourdinner@gmx.de");
		mailServerSettings.setPassword("bereitsverwendet");
		
		
//		mailServerSettings.setMailServer("smtp.gmail.com");
//		mailServerSettings.setMailServerPort(25);
//		mailServerSettings.setFrom("clemensstich@googlemail.com");
//		mailServerSettings.setUseAuth(true);
//		mailServerSettings.setUseTls(true);
//		mailServerSettings.setUsername("clemensstich@googlemail.com");
//		mailServerSettings.setPassword("Eclipse1");
		
		
		
		MailSender customMailSender = createCustomMailSender(mailServerSettings);

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setSubject("My Tst Subj");
		mailMessage.setTo("clemensstich@googlemail.com");
		mailMessage.setText("My TEst Msg");
		mailMessage.setFrom(mailServerSettings.getFrom());

		if (StringUtils.isNotEmpty(mailServerSettings.getReplyTo())) {
			mailMessage.setReplyTo(mailServerSettings.getReplyTo());
		}
		
		customMailSender.send(mailMessage);
	}

	private static MailSender createCustomMailSender(MailServerSettingsImpl mailServerSettings) {
		JavaMailSenderImpl result = new JavaMailSenderImpl();
		result.setHost(mailServerSettings.getMailServer());
		// result.setProtocol(); // TODO: This may also be needed !!!
		if (mailServerSettings.hasMailServerPort()) {
			result.setPort(mailServerSettings.getMailServerPort());
		}

		Properties props = new Properties();
		if (mailServerSettings.isUseAuth()) {
			result.setUsername(mailServerSettings.getUsername());
			result.setPassword(mailServerSettings.getPassword());
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtps.auth", "true");
			System.out.println("Use Auth");
		}
		else {
			props.put("mail.smtp.auth", false);
			System.out.println("Without Auth");
		}

		boolean useTls = mailServerSettings.isUseTls();
		System.out.println("Use TLS: " + useTls);
		props.put("mail.smtp.starttls.enable", String.valueOf(useTls));

		result.setDefaultEncoding("UTF-8");

		result.setJavaMailProperties(props);
		
		return result;
	}

}
