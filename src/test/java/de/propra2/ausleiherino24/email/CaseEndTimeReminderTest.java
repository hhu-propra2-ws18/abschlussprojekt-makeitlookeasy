package de.propra2.ausleiherino24.email;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Case;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CaseEndTimeReminderTest {

    private DateTimeFormatter formatter;
    private EmailSender es;
    private Case a;
    private CaseEndTimeReminder r;
    private LocalDateTime today;

    @Before
    public void init() {
        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        es = Mockito.mock(EmailSender.class);
        final CaseRepository caseRepository = Mockito.mock(CaseRepository.class);
        a = Mockito.mock(Case.class);
        Mockito.when(a.getRequestStatus()).thenReturn(Case.RUNNING);
        Mockito.when(caseRepository.findAll()).thenReturn(Arrays.asList(a));
        r = new CaseEndTimeReminder(caseRepository, es);
        today = LocalDate.parse(LocalDateTime.now().format(formatter), formatter).atStartOfDay();
    }

    @Test
    public void sendRemindingEmailShouldSendEmailIfCaseEndTimeIsTomorrow() {
        final LocalDateTime tomorrow = today.plusDays(1L);
        final String s = tomorrow.format(formatter);
        Mockito.when(a.getFormattedEndTime()).thenReturn(s);
        r.sendRemindingEmail();

        Mockito.verify(es, Mockito.times(1)).sendRemindingEmail(a);
    }

    @Test
    public void sendRemindingEmailShouldNotSendEmailIfCaseEndTimeIsToday() {
        final String s = today.format(formatter);
        Mockito.when(a.getFormattedEndTime()).thenReturn(s);
        r.sendRemindingEmail();

        Mockito.verify(es, Mockito.times(0)).sendRemindingEmail(a);
    }

    @Test
    public void sendRemindingEmailShouldNOtSendEmailIfCaseEndTimeWasYesterday() {
        final LocalDateTime yesterday = today.minusDays(1L);
        final String s = yesterday.format(formatter);
        Mockito.when(a.getFormattedEndTime()).thenReturn(s);
        r.sendRemindingEmail();

        Mockito.verify(es, Mockito.times(0)).sendRemindingEmail(a);
    }

}
