package de.propra2.ausleiherino24.email;

import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EmailSenderTest {
    private EmailConfig emailConfigMock;
    private JavaMailSenderImpl javaMailSenderMock;
    private EmailSender emailSender;

    @Before
    public void init(){
        emailConfigMock = mock(EmailConfig.class);
        javaMailSenderMock = mock(JavaMailSenderImpl.class);
        emailSender = new EmailSender(emailConfigMock, javaMailSenderMock, new SimpleMailMessage());
    }

    @Test
    public void sendOneEmail(){

        User conflictReporter = new User();
        conflictReporter.setEmail("test@mail.de");
        Case conflictCase = new Case();
        conflictCase.setId(0L);
        Conflict conflict = new Conflict();
        conflict.setConflictReporter(conflictReporter);
        conflict.setConflictedCase(conflictCase);

        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom("test@mail.de");
        expectedMessage.setTo("Clearing@Service.com");
        expectedMessage.setSubject("Conflicting Case id: 0");

        emailSender.sendEmail(conflict);

        verify(javaMailSenderMock).send(expectedMessage);
    }

    @Test
    public void sendOneEmail2(){

        User conflictReporter = new User();
        conflictReporter.setEmail("test@mail.de");
        Case conflictCase = new Case();
        conflictCase.setId(1L);
        Conflict conflict = new Conflict();
        conflict.setConflictReporter(conflictReporter);
        conflict.setConflictedCase(conflictCase);
        conflict.setConflictDescription("Dies hier ist ein einfacher Test");

        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setFrom("test@mail.de");
        expectedMessage.setTo("Clearing@Service.com");
        expectedMessage.setSubject("Conflicting Case id: 1");
        expectedMessage.setText("Dies hier ist ein einfacher Test");

        emailSender.sendEmail(conflict);

        verify(javaMailSenderMock).send(expectedMessage);
    }

}
