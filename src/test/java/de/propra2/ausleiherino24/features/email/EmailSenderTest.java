package de.propra2.ausleiherino24.features.email;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class EmailSenderTest {

    private final static String USER_EMAIL = "test@mail.de";
    private final static String SERVICE_EMAIL = "ausleiherino24@gmail.com";
    private JavaMailSenderImpl javaMailSenderMock;
    private EmailSender emailSender;
    private Conflict conflict;

    @BeforeEach
    void init() {
        EmailConfig emailConfigMock = mock(EmailConfig.class);
        javaMailSenderMock = mock(JavaMailSenderImpl.class);
        emailSender = new EmailSender(emailConfigMock, javaMailSenderMock, new SimpleMailMessage());
        User otherUser = new User();
        otherUser.setEmail("test@mail.de");
        final Article art = new Article();
        art.setName("testArticle");
        art.setOwner(otherUser);
        Case conflictedCase = new Case();
        conflictedCase.setArticle(art);
        conflict = new Conflict();
        User conflictReporter = new User();
        conflictReporter.setEmail(EmailSenderTest.USER_EMAIL);
        conflictReporter.setUsername("user2");
        conflictedCase.setId(1L);
        conflictedCase.setReceiver(conflictReporter);
        conflict.setConflictReporterUsername("user2");
        conflict.setConflictedCase(conflictedCase);
        conflict.setConflictDescription("Dies hier ist ein einfacher Test");

        when(emailConfigMock.getHost()).thenReturn("TestHost");
        when(emailConfigMock.getPort()).thenReturn(4321);
        when(emailConfigMock.getUsername()).thenReturn("TestUsername");
        when(emailConfigMock.getPassword()).thenReturn("password");
    }

    @Test
    void sendConflictEmailShouldSendCorrectMail() {
        final SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom(EmailSenderTest.USER_EMAIL);
        expectedMessage.setTo(EmailSenderTest.SERVICE_EMAIL);
        expectedMessage.setSubject("Conflicting Case id: 1");
        expectedMessage.setText(
                "Dies hier ist ein einfacher Test. | Email sent from: test@mail.de. | Other participant: test@mail.de");

        emailSender.sendConflictEmail(conflict);

        verify(javaMailSenderMock).send(expectedMessage);
    }

    @Test
    void configureMailSenderShouldSetCorrectMailConfig() {
        emailSender.configureMailSender();
        verify(javaMailSenderMock).setHost("TestHost");
        verify(javaMailSenderMock).setPort(4321);
        verify(javaMailSenderMock).setUsername("TestUsername");
        verify(javaMailSenderMock).setPassword("password");
    }

    @Test
    void sendConflictEmailShouldThrowException() {

        assertThrows(MailSendException.class, () -> {
            final SimpleMailMessage expectedMessage = new SimpleMailMessage();
            expectedMessage.setFrom(EmailSenderTest.USER_EMAIL);
            expectedMessage.setTo(EmailSenderTest.SERVICE_EMAIL);
            expectedMessage.setSubject("Conflicting Case id: 1");
            expectedMessage.setText(
                    "Dies hier ist ein einfacher Test. | Email sent from: test@mail.de. | Other participant: test@mail.de");

            doThrow(new MailSendException("")).when(javaMailSenderMock).send(expectedMessage);
            emailSender.sendConflictEmail(conflict);
        });

    }

}
