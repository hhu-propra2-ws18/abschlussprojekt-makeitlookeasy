package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.ReservationHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ConflictService {

    private final CaseService caseService;
    private final ConflictRepository conflicts;
    private final EmailSender emailSender;
    private final ReservationHandler reservationHandler;

    @Autowired
    public ConflictService(final ConflictRepository conflicts, final EmailSender emailSender,
            final ReservationHandler reservationHandler, final CaseService caseService) {
        this.conflicts = conflicts;
        this.emailSender = emailSender;
        this.reservationHandler = reservationHandler;
        this.caseService = caseService;
    }

    void saveConflict(final Conflict conflict, final User user) throws Exception {
        isCorrectUser(conflict, user);
        conflict.setConflictedCaseConflict(conflict);
        conflicts.save(conflict);

        sendConflictEmail(conflict);
    }

    public void openConflict(final Case conflictedCase, final String conflictDescription)
            throws Exception {
        final Conflict conflict = new Conflict();
        conflict.setConflictDescription(conflictDescription);
        conflict.setConflictedCase(conflictedCase);
        conflict.setConflictReporterUsername(conflictedCase.getOwner().getUsername());
        caseService.conflictOpened(conflictedCase.getId());

        saveConflict(conflict, conflictedCase.getOwner());
    }

    void sendConflictEmail(final Conflict conflict) {
        emailSender.sendConflictEmail(conflict);
    }

    public void deactivateConflict(final Long id, final User user) throws Exception {
        final Optional<Conflict> conflictToDeactivate = conflicts.findById(id);
        if (!conflictToDeactivate.isPresent()) {
            throw new DataAccessException("No such conflict.") {
            };
        }
        isConflictReporterOrAdmin(conflictToDeactivate.get(), user);
        final Conflict theConflictToDeactivate = conflictToDeactivate.get();
        theConflictToDeactivate
                .setConflictDescription("Conflict with id: " + theConflictToDeactivate.getId()
                        + " was deactivated by :" + user.getUsername());
        sendConflictEmail(theConflictToDeactivate);
        conflicts.delete(theConflictToDeactivate);
    }

    public List<Conflict> getAllConflictsByUser(final User user) {
        final List<Conflict> allConflicts = new ArrayList<>();
        allConflicts.addAll(conflicts.findAllByReceiver(user));
        allConflicts.addAll(conflicts.findAllByArticleOwner(user));

        return allConflicts;
    }

    public Conflict getConflict(final Long id, final User user) throws Exception {
        final Optional<Conflict> conflict = conflicts.findById(id);
        if (!conflict.isPresent()) {
            throw new DataAccessException("No such conflict"){};
        }
        isCorrectUser(conflict.get(), user);
        return conflict.get();
    }

    public boolean isConflictedArticleOwner(final Conflict conflict, final User user)
            throws Exception {
        if (user == null) {
            throw new NullPointerException("User was null");
        }
        return user.equals(conflict.getOwner());
    }

    public List<User> getConflictParticipants(final Conflict conflict) throws Exception {
        if (conflict == null) {
            throw new NullPointerException("Conflict was null");
        }
        return Arrays.asList(conflict.getOwner(), conflict.getReceiver());
    }

    private boolean isCorrectUser(final Conflict conflict, final User user) throws Exception {
        if (!(user.equals(conflict.getOwner()) || user.equals(conflict.getReceiver()))
                && !isUserAdmin(
                user)) {
            throw new Exception("Access denied!");
        }
        return true;
    }

    private boolean isConflictReporterOrAdmin(final Conflict conflict, final User user)
            throws Exception {
        if (!(conflict.getConflictReporterUsername().equals(user.getUsername()) || isUserAdmin(
                user))) {
            throw new Exception("Access denied!");
        }
        return true;
    }

    private boolean isUserAdmin(final User user) {
        return "admin".equals(user.getRole());
    }

    public void solveConflict(final Conflict conflictToSolve, final User user,
            final User depositReceiver)
            throws Exception {
        if (!isUserAdmin(user)) {
            throw new Exception("No permission!");
        }
        if (depositReceiver.equals(conflictToSolve.getOwner())) {
            reservationHandler.punishReservationByCase(conflictToSolve.getConflictedCase());
            return;
        }
        reservationHandler.releaseReservationByCase(conflictToSolve.getConflictedCase());
    }
}
