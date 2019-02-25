package de.propra2.ausleiherino24.email;

import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    private EmailConfig config;
    private JavaMailSenderImpl mailSender;
    private SimpleMailMessage message;
    private UserService userService;
    Logger logger = LoggerFactory.getLogger(EmailSender.class);

    /**
     * TODO JavaDoc.
     *
     * @param config Description
     * @param mailSender Description
     * @param message Description
     * @param userService Description
     */
    @Autowired
    public EmailSender(EmailConfig config, JavaMailSenderImpl mailSender,
            SimpleMailMessage message, UserService userService) {
        this.config = config;
        this.mailSender = mailSender;
        this.message = message;
        this.userService = userService;
    }

    /**
     * TODO JavaDoc.
     * @param conflict Description
     * @throws Exception Description
     */
    public void sendConflictEmail(Conflict conflict) throws Exception {
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPort());
        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(config.getPassword());

        User user = userService.findUserByUsername(conflict.getConflictReporterUsername());

        message.setFrom(user.getEmail());
        message.setTo("Clearing@Service.com"); // FakeEmail -> does not matter what goes in here
        message.setSubject("Conflicting Case id: " + conflict.getConflictedCase().getId());
        message.setText(conflict.getConflictDescription());

        mailSender.send(message);
    }

    public void sendRemindingEmail(Case c) throws MailException {
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPort());
        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(config.getPassword());

        message.setFrom("Clearing@Service.com");
        message.setTo(c.getReceiver().getEmail());
        message.setSubject("Reminder: Article: "+ c.getArticle().getName()+" has to be returned tomorrow!");
        message.setText("Please do not forget to return the article on time!");

        mailSender.send(message);
    }
}
