package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.ResolveConflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.CaseService;
import de.propra2.ausleiherino24.service.ConflictService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ConflictController {

    private final CaseService caseService;
    private final ConflictService conflictService;
    private final UserService userService;

    private static final String SOMEVIEW_STRING = "someView";
    private static final String USER_STRING = "user";

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
    public String sendConflict(final @RequestParam Long id, final String conflictDescription)
            throws Exception {
        if (!caseService.isValidCase(id)) {
            return "redirect:/myOverview?returned&conflictFailed";
        }

        final Case aCase = caseService.findCaseById(id);
        conflictService.openConflict(aCase, conflictDescription);
        return "redirect:/myOverview?returned&openedConflict";
    }

    @DeleteMapping("/deactivateconflict")
    public String deactivateConflict(final @RequestParam Long id, final Principal principal)
            throws Exception {
        final User user = userService.findUserByPrincipal(principal);
        conflictService.deactivateConflict(id, user);

        return "redirect:/myOverview?returned&deactivatedConflict";
    }

    @GetMapping("/conflict")
    public String displayConflict(final @RequestParam("id") Long id, final Principal principal,
            final Model model)
            throws Exception {
        final User user = userService.findUserByPrincipal(principal);
        final Conflict conflictToDisplay = conflictService.getConflict(id, user);

        model.addAttribute("conflict", conflictToDisplay);
        model.addAttribute(USER_STRING, user);
        if (conflictService.isConflictedArticleOwner(conflictToDisplay, user)) {
            // view with delete-conflict-button
        }

        return SOMEVIEW_STRING; //view without delete button
    }

    @GetMapping("/conflictsbyUser")
    public String displayAllConflicts(final Principal principal, final Model model) {
        final User user = userService.findUserByPrincipal(principal);
        final List<Conflict> conflicts = conflictService.getAllConflictsByUser(user);

        model.addAttribute("conflicts", conflicts);
        model.addAttribute(USER_STRING, user);
        return SOMEVIEW_STRING;
    }

    @GetMapping("/solveConflictView")
    public String solveConflictView(final @RequestParam("id") Long id, final Principal principal,
            final Model model)
            throws Exception {
        final User user = userService.findUserByPrincipal(principal);
        final Conflict conflictToDisplay = conflictService.getConflict(id, user);

        model.addAttribute("conflict", conflictToDisplay);
        model.addAttribute(USER_STRING, user);
        model.addAttribute("participants",
                conflictService.getConflictParticipants(conflictToDisplay));
        return SOMEVIEW_STRING;
    }

    /**
     * Admin decides conflict for Owner.
     * @param id CaseId
     */
    @PostMapping("/decideforowner")
    public String solveConflictOwner(@RequestParam Long id, final Principal principal)
            throws Exception {
        final Case c = caseService.findCaseById(id);
        final User user = userService.findUserByPrincipal(principal);
        final Conflict conflictToSolve = conflictService
                .getConflict(c.getConflict().getId(), user);

        conflictService.solveConflict(conflictToSolve, user, c.getOwner());
        conflictService.deactivateConflict(c.getConflict().getId(), user);

        return "redirect:/conflicts";
    }

    /**
     * Admin decides conflict for Receiver.
     * @param id CaseId
     */
    @PostMapping("/decideforreceiver")
    public String solveConflictReceiver(@RequestParam Long id, final Principal principal)
            throws Exception {
        final Case c = caseService.findCaseById(id);
        final User user = userService.findUserByPrincipal(principal);
        final Conflict conflictToSolve = conflictService
                .getConflict(c.getConflict().getId(), user);

        conflictService.solveConflict(conflictToSolve, user, c.getReceiver());
        conflictService.deactivateConflict(c.getConflict().getId(), user);

        return "redirect:/conflicts";
    }

    /**
     * Mapping for admins to show all open conflicts.
     */
    @GetMapping("/conflicts")
    public ModelAndView solveConflicts(Principal principal) {
        final ModelAndView mav = new ModelAndView("/admin/conflict");

        final User currentUser = userService.findUserByPrincipal(principal);
        final List<Case> conflicts = caseService.findAllCasesWithOpenConflicts();

        mav.addObject(USER_STRING, currentUser);
        mav.addObject("conflicts", conflicts);
        return mav;
    }
}

