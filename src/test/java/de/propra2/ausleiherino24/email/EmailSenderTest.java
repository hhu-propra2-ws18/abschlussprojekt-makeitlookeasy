package de.propra2.ausleiherino24.email;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class EmailSenderTest {

    private final static String USER_EMAIL = "test@mail.de";
    private final static String SERVICE_EMAIL = "test@mail.de";
    private EmailConfig emailConfigMock;
    private JavaMailSenderImpl javaMailSenderMock;
    private EmailSender emailSender;
    private UserService userService;
    private Case conflictedCase;
    private Conflict conflict;
    private User conflictReporter;

    @Before
    public void init() {
        emailConfigMock = mock(EmailConfig.class);
        javaMailSenderMock = mock(JavaMailSenderImpl.class);
        userService = mock(UserService.class);
        emailSender = new EmailSender(emailConfigMock, javaMailSenderMock, new SimpleMailMessage(),
                userService);

        conflictedCase = new Case();
        conflict = new Conflict();
        conflictReporter = new User();
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
    public void sendConflictEmailShouldSendCorrectMail() {
        when(userService.findUserByUsername("user2")).thenReturn(conflictReporter);

        final SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom(EmailSenderTest.USER_EMAIL);
        expectedMessage.setTo(EmailSenderTest.SERVICE_EMAIL);
        expectedMessage.setSubject("Conflicting Case id: 1");
        expectedMessage.setText("Dies hier ist ein einfacher Test");

        emailSender.sendConflictEmail(conflict);

        verify(javaMailSenderMock).send(expectedMessage);
    }

    @Test
    public void sendConflictEmailShouldSetCorrectMailConfig() {

        when(userService.findUserByUsername("user2")).thenReturn(conflictReporter);

        emailSender.sendConflictEmail(conflict);
        verify(javaMailSenderMock).setHost("TestHost");
        verify(javaMailSenderMock).setPort(4321);
        verify(javaMailSenderMock).setUsername("TestUsername");
        verify(javaMailSenderMock).setPassword("password");
    }

    @Test(expected = MailSendException.class)
    public void sendConflictEmailShouldThrowException() {
        when(userService.findUserByUsername("user2")).thenReturn(conflictReporter);

        final SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom(EmailSenderTest.USER_EMAIL);
        expectedMessage.setTo(EmailSenderTest.SERVICE_EMAIL);
        expectedMessage.setSubject("Conflicting Case id: 1");
        expectedMessage.setText("Dies hier ist ein einfacher Test");

        doThrow(new MailSendException("")).when(javaMailSenderMock).send(expectedMessage);
        emailSender.sendConflictEmail(conflict);
    }

    @Test
    public void sendRemindingEmailShouldSendCorrectMail() {
        final Article art = new Article();
        art.setName("testArticle");
        conflictedCase.setArticle(art);

        final SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setTo(EmailSenderTest.USER_EMAIL);
        expectedMessage.setFrom(EmailSenderTest.SERVICE_EMAIL);
        expectedMessage.setSubject("Reminder: Article: testArticle has to be returned tomorrow!");
        expectedMessage.setText("Please do not forget to return the article on time!");

        emailSender.sendRemindingEmail(conflictedCase);
        verify(javaMailSenderMock).setHost("TestHost");
        verify(javaMailSenderMock).setPort(4321);
        verify(javaMailSenderMock).setUsername("TestUsername");
        verify(javaMailSenderMock).setPassword("password");
        verify(javaMailSenderMock).send(expectedMessage);

    }

}
