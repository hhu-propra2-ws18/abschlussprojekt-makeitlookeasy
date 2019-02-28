package de.propra2.ausleiherino24.features.email;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Case;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaseEndTimeReminder {

    private static final Long ONE_DAY = 1L;
    private final CaseRepository cases;
    private final EmailSender emailSender;

    @Autowired
    public CaseEndTimeReminder(final CaseRepository cases, final EmailSender emailSender) {
        this.cases = cases;
        this.emailSender = emailSender;
    }

    //TODO: uncomment in production
    //@Scheduled(fixedDelay = 5000, initialDelay = 20000)
    void getRunningCasesOneDayBeforeEndTime() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        final LocalDateTime currentTime = LocalDate
                .parse(LocalDateTime.now().format(formatter), formatter).atStartOfDay();
        final List<Case> activeCases = cases.findAll()
                .stream()
                .filter(c -> c.getRequestStatus() == Case.RUNNING)
                .filter(c -> LocalDate.parse(c.getFormattedEndTime(), formatter).atStartOfDay()
                        .isEqual(currentTime.plusDays(CaseEndTimeReminder.ONE_DAY)))
                .collect(Collectors.toList());

        sendRemindingEmail(activeCases);
    }

    private void sendRemindingEmail(final List<Case> activeCases) {
        emailSender.configureMailSender();
        activeCases.forEach(c -> {
            emailSender.sendRemindingEmail(c);
            c.setRequestStatus(Case.RUNNING_EMAILSENT);
            cases.save(c);
        });
    }

}
