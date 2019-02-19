package de.propra2.ausleiherino24.email;

import de.propra2.ausleiherino24.model.Conflict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

	private EmailConfig config;
	private JavaMailSenderImpl mailSender;
	private SimpleMailMessage message;

	@Autowired
	public EmailSender(EmailConfig config, JavaMailSenderImpl mailSender,
					   SimpleMailMessage message) {
		this.config = config;
		this.mailSender = mailSender;
		this.message = message;
	}

	public void sendEmail(Conflict conflict) {
		mailSender.setHost(config.getHost());
		mailSender.setPort(config.getPort());
		mailSender.setUsername(config.getUsername());
		mailSender.setPassword(config.getPassword());

		message.setFrom(conflict.getConflictReporter().getEmail());
		message.setTo("Clearing@Service.com"); // FakeEmail -> does not matter what goes in here
		message.setSubject("Conflicting Case id: " + conflict.getConflictedCase().getId());
		message.setText(conflict.getConflictDescription());

		mailSender.send(message);
	}
}
