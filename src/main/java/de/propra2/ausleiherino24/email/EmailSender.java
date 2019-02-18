package de.propra2.ausleiherino24.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import de.propra2.ausleiherino24.model.Conflict;

public class EmailSender {

	private EmailConfig config;

	@Autowired
	public EmailSender(EmailConfig config) {
		this.config = config;
	}

	public void sendEmail(Conflict conflict) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(config.getHost());
		mailSender.setPort(config.getPort());
		mailSender.setUsername(config.getUsername());
		mailSender.setPassword(config.getPassword());

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(conflict.getConflictReporter().getEmail());
		message.setTo("Clearing@Service.com"); // FakeEmail -> does not matter what goes in here
		message.setSubject("Conflicting Case id: " + conflict.getConflictedCase().getId());
		message.setText(conflict.getConflictDescription());

		mailSender.send(message);
	}
}
