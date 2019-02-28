package de.propra2.ausleiherino24.features.email;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Case;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CaseEndTimeReminderTest {

    private DateTimeFormatter formatter;
    private EmailSender es;
    private Case a;
    private CaseEndTimeReminder r;
    private LocalDateTime today;
    private CaseRepository caseRepository;

    @BeforeEach
    void init() {
        formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        es = Mockito.mock(EmailSender.class);
        caseRepository = Mockito.mock(CaseRepository.class);
        a = Mockito.mock(Case.class);
        Mockito.when(a.getRequestStatus()).thenReturn(Case.RUNNING);
        Mockito.when(caseRepository.findAll()).thenReturn(Arrays.asList(a));
        r = new CaseEndTimeReminder(caseRepository, es);
        today = LocalDate.parse(LocalDateTime.now().format(formatter), formatter).atStartOfDay();
    }

    @Test
    void sendRemindingEmailShouldSendEmailIfCaseEndTimeIsTomorrow() {
        final LocalDateTime tomorrow = today.plusDays(1L);
        final String s = tomorrow.format(formatter);
        Mockito.when(a.getFormattedEndTime()).thenReturn(s);
        r.getRunningCasesOneDayBeforeEndTime();

        Mockito.verify(es, Mockito.times(1)).sendRemindingEmail(a);
    }

    @Test
    void sendRemindingEmailShouldNotSendEmailIfCaseEndTimeIsToday() {
        final String s = today.format(formatter);
        Mockito.when(a.getFormattedEndTime()).thenReturn(s);
        r.getRunningCasesOneDayBeforeEndTime();

        Mockito.verify(es, Mockito.times(0)).sendRemindingEmail(a);
    }

    @Test
    void sendRemindingEmailShouldNotSendEmailIfCaseEndTimeWasYesterday() {
        final LocalDateTime yesterday = today.minusDays(1L);
        final String s = yesterday.format(formatter);
        Mockito.when(a.getFormattedEndTime()).thenReturn(s);
        r.getRunningCasesOneDayBeforeEndTime();

        Mockito.verify(es, Mockito.times(0)).sendRemindingEmail(a);
    }

    @Test
    void sendRemindingEmailShouldSendOnlyOneEmailIfCaseEndTimeIsTomorrow() {
        final LocalDateTime tomorrow = today.plusDays(1L);
        final String s = tomorrow.format(formatter);
        Mockito.when(a.getFormattedEndTime()).thenReturn(s);
        r.getRunningCasesOneDayBeforeEndTime();
        Mockito.when(a.getRequestStatus()).thenReturn(Case.RUNNING_EMAILSENT);
        r.getRunningCasesOneDayBeforeEndTime();

        Mockito.verify(a, Mockito.times(1)).setRequestStatus(Case.RUNNING_EMAILSENT);
        Mockito.verify(caseRepository, Mockito.times(1)).save(a);
        Mockito.verify(es, Mockito.times(1)).sendRemindingEmail(a);
    }

}
