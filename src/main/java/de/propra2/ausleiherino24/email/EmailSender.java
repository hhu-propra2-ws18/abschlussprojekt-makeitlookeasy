package de.propra2.ausleiherino24.email;

import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    private EmailConfig config;
    private JavaMailSenderImpl mailSender;
    private SimpleMailMessage message;
    private UserService userService;

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
    public void sendEmail(Conflict conflict) throws Exception {
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
}
