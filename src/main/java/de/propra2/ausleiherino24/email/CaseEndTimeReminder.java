package de.propra2.ausleiherino24.email;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Case;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaseEndTimeReminder {

    private final Logger logger = LoggerFactory.getLogger(CaseEndTimeReminder.class);

    private CaseRepository cases;
    private EmailSender emailSender;

    @Autowired
    public CaseEndTimeReminder(CaseRepository cases, EmailSender emailSender){
        this.cases = cases;
        this.emailSender = emailSender;
    }

    //@Scheduled(fixedDelay = 5000, initialDelay = 20000)
    void sendRemindingEmail() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDateTime currentTime = LocalDate.parse(LocalDateTime.now().format(formatter), formatter).atStartOfDay();
        List<Case> activeCases = cases.findAll()
                .stream()
                .filter(c -> c.getRequestStatus() == Case.RUNNING)
                .filter(c -> LocalDate.parse(c.getFormattedEndTime(), formatter).atStartOfDay().isEqual(currentTime.plusDays(1L)))
                .collect(Collectors.toList());

        activeCases.forEach(c -> {
            try {
                emailSender.sendRemindingEmail(c);
            } catch (Exception e) {
                logger.info("Could not send reminder email for case {}.", c.getId());
            }
        });
    }

}
