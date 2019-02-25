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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ConflictService {
    private final CaseService caseService;
    private final ConflictRepository conflicts;
    private final EmailSender emailSender;
    private final ReservationHandler reservationHandler;
    private final Logger LOGGER = LoggerFactory.getLogger(ConflictService.class);

    /**
     * TODO JavaDoc.
     *
     * @param conflicts Description
     * @param emailSender Description
     * @param reservationHandler Description
     */
    @Autowired
    public ConflictService(ConflictRepository conflicts, EmailSender emailSender,
            ReservationHandler reservationHandler, CaseService caseService) {
        this.conflicts = conflicts;
        this.emailSender = emailSender;
        this.reservationHandler = reservationHandler;
        this.caseService = caseService;
    }

    /**
     * TODO JavaDoc.
     * @param conflict Description
     * @param user Description
     * @throws Exception Description
     */
    void saveConflict(Conflict conflict, User user) throws Exception {
        isCorrectUser(conflict, user);
        conflict.setConflictedCaseConflict(conflict);
        conflicts.save(conflict);

        sendConflictEmail(conflict);
    }

    public void openConflict(Case conflictedCase, String conflictDescription) throws Exception {
        Conflict conflict = new Conflict();
        conflict.setConflictDescription(conflictDescription);
        conflict.setConflictedCase(conflictedCase);
        conflict.setConflictReporterUsername(conflictedCase.getOwner().getUsername());
        caseService.conflictOpened(conflictedCase.getId());

        saveConflict(conflict, conflictedCase.getOwner());
    }

    void sendConflictEmail(Conflict conflict) throws Exception {
        emailSender.sendConflictEmail(conflict);
    }

    /**
     * TODO Javadoc.
     * @param id Description
     * @param user Description
     * @throws Exception Description
     */
    public void deactivateConflict(Long id, User user) throws Exception {
        Optional<Conflict> conflictToDeactivate = conflicts.findById(id);
        if (!conflictToDeactivate.isPresent()) {
            throw new DataAccessException("No such Conflict") {
            };
        }
        isConflictReporterOrAdmin(conflictToDeactivate.get(), user);
        Conflict theConflictToDeactivate = conflictToDeactivate.get();
        theConflictToDeactivate.setConflictDescription("ConflictDeactivated by :" + user.getUsername());
        sendConflictEmail(theConflictToDeactivate);
        conflicts.delete(theConflictToDeactivate);
    }

    /**
     * TODO JavaDoc.
     * @param user Description
     * @return Description
     */
    public List<Conflict> getAllConflictsByUser(User user) {
        List<Conflict> allConflicts = new ArrayList<>();
        allConflicts.addAll(conflicts.findAllByReceiver(user));
        allConflicts.addAll(conflicts.findAllByArticleOwner(user));

        return allConflicts;
    }

    /**
     * TODO JavaDoc.
     * @param id Description
     * @param user Description
     * @return Description
     * @throws Exception Description
     */
    public Conflict getConflict(Long id, User user) throws Exception {
        Optional<Conflict> conflict = conflicts.findById(id);
        if (!conflict.isPresent()) {
            throw new Exception("No such conflict");
        }
        isCorrectUser(conflict.get(), user);
        return conflict.get();
    }

    /**
     * TODO JavaDoc.
     * @param conflict Description
     * @param user Description
     * @return Description
     * @throws Exception Description
     */
    public boolean isConflictedArticleOwner(Conflict conflict, User user) throws Exception {
        if (user == null) {
            throw new Exception("No such user");
        }
        return user.equals(conflict.getOwner());
    }

    /**
     * TODO Javadoc.
     * @param conflict Description
     * @return Description
     * @throws Exception Description
     */
    public List<User> getConflictParticipants(Conflict conflict) throws Exception {
        if (conflict == null) {
            throw new Exception("No such conflict!");
        }
        return Arrays.asList(conflict.getOwner(), conflict.getReceiver());
    }

    private boolean isCorrectUser(Conflict conflict, User user) throws Exception {
        if (!(user.equals(conflict.getOwner()) || user.equals(conflict.getReceiver())) && !isUserAdmin(
                user)) {
            throw new Exception("Access denied!");
        }
        return true;
    }

    private boolean isConflictReporterOrAdmin(Conflict conflict, User user) throws Exception {
        if(!(conflict.getConflictReporterUsername().equals(user.getUsername()) || isUserAdmin(user))){
            throw new Exception("Access denied!");
        }
        return true;
    }

    private boolean isUserAdmin(User user) {
        return "admin".equals(user.getRole());
    }

    /**
     * TODO JavaDoc.
     * @param conflictToSolve Description
     * @param user Description
     * @param depositReceiver Description
     * @throws Exception Description
     */
    public boolean solveConflict(Conflict conflictToSolve, User user, User depositReceiver)
            throws Exception {
        if (!isUserAdmin(user)) {
            throw new Exception("No permission!");
        }
        if (depositReceiver.equals(conflictToSolve.getOwner())) {
            return reservationHandler.punishReservation(conflictToSolve.getConflictedCase());
        }
        return reservationHandler.releaseReservation(conflictToSolve.getConflictedCase());
    }
}
