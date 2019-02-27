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
    private final ConflictRepository conflictRepository;
    private final EmailSender emailSender;
    private final ReservationHandler reservationHandler;

    @Autowired
    public ConflictService(final ConflictRepository conflictRepository, final EmailSender emailSender,
            final ReservationHandler reservationHandler, final CaseService caseService) {
        this.conflictRepository = conflictRepository;
        this.emailSender = emailSender;
        this.reservationHandler = reservationHandler;
        this.caseService = caseService;
    }

    void saveConflict(final Conflict conflict, final User user) throws Exception {
        isCorrectUser(conflict, user);
        conflict.setConflictedCaseConflict(conflict);
        conflictRepository.save(conflict);

        //sendConflictEmail(conflict);
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

    /**
     * Deactivates conflict with id.
     * @param id conflictId
     */
    public void deactivateConflict(final Long id, final User user) throws Exception {
        final Optional<Conflict> conflictToDeactivate = conflictRepository.findById(id);
        if (!conflictToDeactivate.isPresent()) {
            throw new DataAccessException("No such conflict."){};
        }
        isConflictReporterOrAdmin(conflictToDeactivate.get(), user);
        final Conflict theConflictToDeactivate = conflictToDeactivate.get();
        theConflictToDeactivate
                .setConflictDescription("Conflict with id: "+ theConflictToDeactivate.getId()
                        + " was deactivated by :" + user.getUsername());
        //sendConflictEmail(theConflictToDeactivate);
        theConflictToDeactivate.getConflictedCase().setRequestStatus(Case.FINISHED);
        deleteConflictById(id);
    }

    /**
     * Safe delete conflict. Sets conflict of case to null and deletes the conflict without
     * deleting the case related to it.
     */
    private void deleteConflictById(Long id) {
        Optional<Conflict> optionalConflict = conflictRepository.findById(id);
        if (!optionalConflict.isPresent()) {
            return;
        }
        Conflict c = optionalConflict.get();
        c.getConflictedCase().setConflict(null);
        c.setConflictedCase(new Case());
        conflictRepository.save(c);
        conflictRepository.deleteById(id);
    }

    public List<Conflict> getAllConflictsByUser(final User user) {
        final List<Conflict> allConflicts = new ArrayList<>();
        allConflicts.addAll(conflictRepository.findAllByReceiver(user));
        allConflicts.addAll(conflictRepository.findAllByArticleOwner(user));

        return allConflicts;
    }

    public Conflict getConflict(final Long id, final User user) throws Exception {
        final Optional<Conflict> conflict = conflictRepository.findById(id);
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

    /**
     * Solves a conflict. The depositReceiver gets the whole deposit.
     * @param conflictToSolve the conflict
     * @param user person, who solved the conflict
     * @param depositReceiver person, who gets the deposit
     * @throws Exception if user has no permissions to solve a conflict
     */
    public void solveConflict(final Conflict conflictToSolve, final User user,
            final User depositReceiver) throws Exception {
        if (!isUserAdmin(user)) {
            throw new Exception("No permission!");
        }
        if (depositReceiver.equals(conflictToSolve.getOwner())) {
            reservationHandler.punishReservationByCase(conflictToSolve.getConflictedCase());
            return;
        }
        reservationHandler.releaseReservationByCase(conflictToSolve.getConflictedCase());
    }

    public int size() {
        return conflictRepository.findAll().size();
    }
}
