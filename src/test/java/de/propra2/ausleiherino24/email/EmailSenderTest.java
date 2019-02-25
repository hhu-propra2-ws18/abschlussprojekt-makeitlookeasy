package de.propra2.ausleiherino24.email;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class EmailSenderTest {

    private EmailConfig emailConfigMock;
    private JavaMailSenderImpl javaMailSenderMock;
    private EmailSender emailSender;
    private UserService userService;

    @Before
    public void init() {
        emailConfigMock = mock(EmailConfig.class);
        javaMailSenderMock = mock(JavaMailSenderImpl.class);
        userService = mock(UserService.class);
        emailSender = new EmailSender(emailConfigMock, javaMailSenderMock, new SimpleMailMessage(),
                userService);
    }

    @Test
    public void sendOneEmail() throws Exception {
        User conflictReporter = new User();
        conflictReporter.setEmail("test@mail.de");
        conflictReporter.setUsername("user1");
        Case conflictCase = new Case();
        conflictCase.setId(0L);
        conflictCase.setReceiver(conflictReporter);
        Conflict conflict = new Conflict();
        conflict.setConflictReporterUsername("user1");
        conflict.setConflictedCase(conflictCase);

        when(userService.findUserByUsername("user1")).thenReturn(conflictReporter);

        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom("test@mail.de");
        expectedMessage.setTo("Clearing@Service.com");
        expectedMessage.setSubject("Conflicting Case id: 0");

        emailSender.sendConflictEmail(conflict);

        verify(javaMailSenderMock).send(expectedMessage);
    }

    @Test
    public void sendOneEmail2() throws Exception {

        User conflictReporter = new User();
        conflictReporter.setEmail("test@mail.de");
        conflictReporter.setUsername("user2");
        Case conflictCase = new Case();
        conflictCase.setId(1L);
        conflictCase.setReceiver(conflictReporter);
        Conflict conflict = new Conflict();
        conflict.setConflictReporterUsername("user2");
        conflict.setConflictedCase(conflictCase);
        conflict.setConflictDescription("Dies hier ist ein einfacher Test");

        when(userService.findUserByUsername("user2")).thenReturn(conflictReporter);

        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom("test@mail.de");
        expectedMessage.setTo("Clearing@Service.com");
        expectedMessage.setSubject("Conflicting Case id: 1");
        expectedMessage.setText("Dies hier ist ein einfacher Test");

        emailSender.sendConflictEmail(conflict);

        verify(javaMailSenderMock).send(expectedMessage);
    }

    @Test
    public void sendOneEmailAndCheckEmailConfig() throws Exception {
        when(emailConfigMock.getHost()).thenReturn("TestHost");
        when(emailConfigMock.getPort()).thenReturn(4321);
        when(emailConfigMock.getUsername()).thenReturn("TestUsername");
        when(emailConfigMock.getPassword()).thenReturn("password");

        User conflictReporter = new User();
        conflictReporter.setEmail("test@mail.de");
        conflictReporter.setUsername("user2");
        Case conflictCase = new Case();
        conflictCase.setId(1L);
        conflictCase.setReceiver(conflictReporter);
        Conflict conflict = new Conflict();
        conflict.setConflictReporterUsername("user2");
        conflict.setConflictedCase(conflictCase);
        conflict.setConflictDescription("Dies hier ist ein einfacher Test");

        when(userService.findUserByUsername("user2")).thenReturn(conflictReporter);

        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom("test@mail.de");
        expectedMessage.setTo("Clearing@Service.com");
        expectedMessage.setSubject("Conflicting Case id: 1");
        expectedMessage.setText("Dies hier ist ein einfacher Test");

        emailSender.sendConflictEmail(conflict);
        verify(javaMailSenderMock).setHost("TestHost");
        verify(javaMailSenderMock).setPort(4321);
        verify(javaMailSenderMock).setUsername("TestUsername");
        verify(javaMailSenderMock).setPassword("password");
    }

    @Test(expected = MailSendException.class)
    public void sendOneEmailThrow() throws Exception {
        User conflictReporter = new User();
        conflictReporter.setEmail("test@mail.de");
        conflictReporter.setUsername("user1");
        Case conflictCase = new Case();
        conflictCase.setId(0L);
        conflictCase.setReceiver(conflictReporter);
        Conflict conflict = new Conflict();
        conflict.setConflictReporterUsername("user1");
        conflict.setConflictedCase(conflictCase);

        when(userService.findUserByUsername("user1")).thenReturn(conflictReporter);

        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom("test@mail.de");
        expectedMessage.setTo("Clearing@Service.com");
        expectedMessage.setSubject("Conflicting Case id: 0");

        doThrow(new MailSendException("")).when(javaMailSenderMock).send(expectedMessage);
        emailSender.sendConflictEmail(conflict);
    }

}
