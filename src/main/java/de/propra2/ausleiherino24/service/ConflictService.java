package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.features.email.EmailSender;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.data.ReservationHandler;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ConflictService {

    private final CaseService caseService;
    private final ConflictRepository conflictRepository;
    private final EmailSender emailSender;
    private final ReservationHandler reservationHandler;

    /**
     * Autowired constructor.
     */
    @Autowired
    public ConflictService(final ConflictRepository conflictRepository,
            final EmailSender emailSender, final ReservationHandler reservationHandler,
            final CaseService caseService) {
        this.conflictRepository = conflictRepository;
        this.emailSender = emailSender;
        this.reservationHandler = reservationHandler;
        this.caseService = caseService;
    }

    /**
     * saves conflict in database if possible and sends an conflict mail.
     */
    void saveConflict(final Conflict conflict, final User user) throws AccessDeniedException {
        isCorrectUser(conflict, user);
        conflict.setConflictedCaseConflict(conflict);
        conflictRepository.save(conflict);

        sendConflictEmail(conflict);
    }

    /**
     * Opens a conflict from given Data.
     */
    public void openConflict(final Case conflictedCase, final String conflictDescription)
            throws AccessDeniedException {
        final Conflict conflict = new Conflict();
        conflict.setConflictDescription(conflictDescription);
        conflict.setConflictedCase(conflictedCase);
        conflict.setConflictReporterUsername(conflictedCase.getOwner().getUsername());
        caseService.conflictOpened(conflictedCase.getId());

        saveConflict(conflict, conflictedCase.getOwner());
    }

    void sendConflictEmail(final Conflict conflict) {
        emailSender.configureMailSender();
        emailSender.sendConflictEmail(conflict);
    }

    /**
     * Deactivates conflict with id.
     *
     * @param id conflictId
     */
    public void deactivateConflict(final Long id, final User user) throws DataAccessException {
        final Optional<Conflict> conflictToDeactivate = conflictRepository.findById(id);
        if (!conflictToDeactivate.isPresent() || !isUserAdmin(user)) {
            throw new DataAccessException("No such conflict.") {
            };
        }
        final Conflict theConflictToDeactivate = conflictToDeactivate.get();

        theConflictToDeactivate
                .setConflictDescription("Conflict with id: " + theConflictToDeactivate.getId()
                        + " was deactivated by :" + user.getUsername());
        sendConflictEmail(theConflictToDeactivate);
        theConflictToDeactivate.getConflictedCase().setRequestStatus(Case.FINISHED);
        deleteConflictById(id, theConflictToDeactivate);
    }

    /**
     * Safe delete conflict. Sets conflict of case to null and deletes the conflict without deleting
     * the case related to it.
     */
    private void deleteConflictById(final Long id, final Conflict cases) {
        cases.getConflictedCase().setConflict(null);
        cases.setConflictedCase(new Case());
        conflictRepository.save(cases);
        conflictRepository.deleteById(id);
    }

    /**
     * Gets a conflict by its id.
     *
     * @param id conflict id
     * @param user User, which want to access the data
     * @return conflict
     * @throws DataAccessException in case the user has no rights to do so
     */
    public Conflict getConflict(final Long id, final User user) throws AccessDeniedException {
        final Optional<Conflict> conflict = conflictRepository.findById(id);
        if (!conflict.isPresent()) {
            throw new DataAccessException("No such conflict") {
            };
        }
        isCorrectUser(conflict.get(), user);
        return conflict.get();
    }

    /**
     * returns a list of users, which contains the owner and the receiver of the given conflict.
     */
    List<User> getConflictParticipants(final Conflict conflict) {
        if (conflict == null) {
            throw new NullPointerException("Conflict was null");
        }
        return Arrays.asList(conflict.getOwner(), conflict.getReceiver());
    }

    /**
     * Checks whether the user is either a admin or a user involved in given conflict.
     *
     * @return true or throws Exception
     * @throws AccessDeniedException when user is not involved in conflict and is no admin.
     */
    private boolean isCorrectUser(final Conflict conflict, final User user)
            throws AccessDeniedException {
        if (!(user.equals(conflict.getOwner()) || user.equals(conflict.getReceiver()))
                && !isUserAdmin(
                user)) {
            throw new AccessDeniedException("Access denied!");
        }
        return true;
    }

    /**
     * Checks, whether the given user is an admin.
     */
    private boolean isUserAdmin(final User user) {
        return "admin".equals(user.getRole());
    }

    /**
     * Solves a conflict. The depositReceiver gets the whole deposit.
     *
     * @param conflictToSolve the conflict
     * @param user person, who solved the conflict
     * @param depositReceiver person, who gets the deposit
     * @throws AccessDeniedException if user has no permissions to solve a conflict
     */
    public boolean solveConflict(final Conflict conflictToSolve, final User user,
            final User depositReceiver) throws AccessDeniedException {

        if (!reservationHandler.checkAvailability()) {
            return false;
        }
        if (!isUserAdmin(user)) {
            throw new AccessDeniedException("No permission!");
        }
        if (depositReceiver.equals(conflictToSolve.getOwner())) {
            reservationHandler.punishReservationByCase(conflictToSolve.getConflictedCase());
            return true;
        }
        reservationHandler.releaseReservationByCase(conflictToSolve.getConflictedCase());
        return true;
    }

    public int size() {
        return conflictRepository.findAll().size();
    }
}
