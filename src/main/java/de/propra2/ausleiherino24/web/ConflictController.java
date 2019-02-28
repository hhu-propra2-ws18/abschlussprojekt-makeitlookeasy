package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.CaseService;
import de.propra2.ausleiherino24.service.ConflictService;
import de.propra2.ausleiherino24.service.UserService;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ConflictController {

    private static final String USER_STRING = "user";
    private final CaseService caseService;
    private final ConflictService conflictService;
    private final UserService userService;

    /**
     * Autowired constructor.
     */
    @Autowired
    public ConflictController(final CaseService caseService, final ConflictService conflictService,
            final UserService userService) {
        this.caseService = caseService;
        this.conflictService = conflictService;
        this.userService = userService;
    }

    /**
     * Methode wird aufgerufen, wenn ein Conflict abgesendet wird. Sie erstellt dann einen
     * entsprechenden Conflict, speichert diesen und sendet eine Conflict-Email. Weitergeleitet wird
     * wieder auf myOverview
     *
     * @param id CaseId
     * @param conflictDescription Beschreibung (mind. 15 Zeichen, max. 2048 Zeichen)
     * @return redirect myOverview mit entsprechendem Parameter
     * @throws Exception Wirft tatsächlich nie eine exception
     */
    @PostMapping("/openconflict")
    public String sendConflict(final @RequestParam Long id, final String conflictDescription) {
        if (!caseService.isValidCase(id)) {
            return "redirect:/myOverview?returned&conflictFailed";
        }

        final Case aCase = caseService.findCaseById(id);
        try {
            conflictService.openConflict(aCase, conflictDescription);
        } catch (AccessDeniedException ex) {
            return "redirect:/myOverview?returned&conflictFailed";
        }

        return "redirect:/myOverview?returned&openedConflict";
    }

    /**
     * Admin decides conflict for Owner.
     *
     * @param id CaseId
     */
    @PostMapping("/decideforowner")
    public String solveConflictOwner(@RequestParam final Long id, final Principal principal)
            throws AccessDeniedException {
        final Case currentCase = caseService.findCaseById(id);
        final User user = userService.findUserByPrincipal(principal);
        final Conflict conflictToSolve = conflictService
                .getConflict(currentCase.getConflict().getId(), user);

        if (conflictService.solveConflict(conflictToSolve, user, currentCase.getOwner())) {
            return "redirect:/conflicts?propayUnavailable";
        }
        conflictService.deactivateConflict(currentCase.getConflict().getId(), user);
        return "redirect:/conflicts";
    }

    /**
     * Admin decides conflict for Receiver.
     *
     * @param id CaseId
     */
    @PostMapping("/decideforreceiver")
    public String solveConflictReceiver(@RequestParam final Long id, final Principal principal)
            throws AccessDeniedException {
        final Case currentCase = caseService.findCaseById(id);
        final User user = userService.findUserByPrincipal(principal);
        final Conflict conflictToSolve = conflictService
                .getConflict(currentCase.getConflict().getId(), user);

        if (conflictService.solveConflict(conflictToSolve, user, currentCase.getReceiver())) {
            return "redirect:/conflicts?propayUnavailable";
        }
        conflictService.deactivateConflict(currentCase.getConflict().getId(), user);

        return "redirect:/conflicts";
    }

    /**
     * Mapping for admins to show all open conflicts.
     */
    @GetMapping("/conflicts")
    public ModelAndView solveConflicts(final Principal principal) {
        final ModelAndView mav = new ModelAndView("/admin/conflict");

        final User currentUser = userService.findUserByPrincipal(principal);
        final List<Case> conflicts = caseService.findAllCasesWithOpenConflicts();

        mav.addObject(USER_STRING, currentUser);
        mav.addObject("conflicts", conflicts);
        return mav;
    }
}

