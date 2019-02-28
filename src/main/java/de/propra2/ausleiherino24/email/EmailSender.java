package de.propra2.ausleiherino24.email;

import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    private final EmailConfig config;
    private final JavaMailSenderImpl mailSender;
    private final SimpleMailMessage message;

    /**
     * Autowired constructor.
     */
    @Autowired
    public EmailSender(final EmailConfig config, final JavaMailSenderImpl mailSender,
            final SimpleMailMessage message) {
        this.config = config;
        this.mailSender = mailSender;
        this.message = message;
    }

    /**
     * Sends an email to the ConflictReporter.
     */
    public void sendConflictEmail(final Conflict conflict) {
        final User reporter = conflict.getOwner();
        final User reported = conflict.getReceiver();
        //Gmail does not allow arbitrary from Address therefore we specify the Email-sender in the Email-Body
        //Mailtrap does allow arbitrary from Address
        message.setFrom(reporter.getEmail());
        message.setTo("ausleiherino24@gmail.com"); // FakeEmail -> does not matter what goes in here
        message.setSubject("Conflicting Case id: " + conflict.getConflictedCase().getId());
        message.setText(conflict.getConflictDescription() +" Email sent from: "+reporter.getEmail()
                +" Other participant: "+reported.getEmail());

        mailSender.send(message);
    }

    void sendRemindingEmail(final Case acase) {
        message.setFrom("ausleiherino24@gmail.com");
        message.setTo(acase.getReceiver().getEmail());
        message.setSubject(
                "Reminder: Article: " + acase.getArticle().getName()
                        + " has to be returned tomorrow!");
        message.setText("Please do not forget to return the article on time!");

        mailSender.send(message);
    }

    public void configureMailSender() {
        final Properties properties = new Properties();
        properties.putAll(config.getProperties());
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPort());
        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(config.getPassword());
        mailSender.setJavaMailProperties(properties);
    }
}
