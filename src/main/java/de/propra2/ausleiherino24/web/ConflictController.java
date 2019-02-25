package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.ResolveConflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ConflictService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConflictController {

    private final ConflictService conflictService;
    private final UserService userService;
    private final CaseRepository caseRepository;

    private static final String SOMEVIEW_STRING = "someView";

    @Autowired
    public ConflictController(ConflictService conflictService, UserService userService,
            CaseRepository caseRepository) {
        this.conflictService = conflictService;
        this.userService = userService;
        this.caseRepository = caseRepository;
    }

    /**
     * Methode wird aufgerufen, wenn ein Conflict abgesendet wird. Sie erstellt dann einen
     * entsprechenden Conflict, speichert diesen und sendet eine Conflict-Email.
     * Weitergeleitet wird wieder auf myOverview
     * @param id CaseId
     * @param conflictDescription Beschreibung (mind. 15 Zeichen, max. 2048 Zeichen)
     * @return redirect myOverview mit entsprechendem Parameter
     * @throws Exception Wirft tatsächlich nie eine exception
     */
    @PostMapping("/openconflict")
    public String sendConflict(@RequestParam Long id, String conflictDescription) throws Exception {
        Optional<Case> optionalCase = caseRepository.findById(id);
        if(!optionalCase.isPresent()) {
            return "redirect:/myOverview?returned&conflictfailed";
        }
        conflictService.openConflict(optionalCase.get(), conflictDescription);

        return "redirect:/myOverview?returned&openedconflict";
    }

    /**
     * TODO Javadoc.
     *
     * @param id Description
     * @param principal Description
     * @param model Description
     * @throws Exception Description
     */
    @DeleteMapping("/deactivateconflict")
    public String deactivateConflict(@RequestParam Long id, Principal principal, Model model)
            throws Exception {
        User user = userService.findUserByPrincipal(principal);
        conflictService.deactivateConflict(id, user);

        return "redirect:/myOverview?returned&deactivatedconflict";
    }

    @GetMapping("/conflict")
    public String displayConflict(@RequestParam("id") Long id, Principal principal, Model model)
            throws Exception {
        User user = userService.findUserByPrincipal(principal);
        Conflict conflictToDisplay = conflictService.getConflict(id, user);

        model.addAttribute("conflict", conflictToDisplay);
        model.addAttribute("user", user);
        if (conflictService.isConflictedArticleOwner(conflictToDisplay, user)) {
            // view with delete-conflict-button
        }

        return SOMEVIEW_STRING; //view without delete button
    }

    @GetMapping("/conflicts")
    public String displayAllConflicts(Principal principal, Model model) {
        User user = userService.findUserByPrincipal(principal);
        List<Conflict> conflicts = conflictService.getAllConflictsByUser(user);

        model.addAttribute("conflicts", conflicts);
        model.addAttribute("user", user);
        return SOMEVIEW_STRING;
    }

    @GetMapping("/solveConflictView")
    public String solveConflictView(@RequestParam("id") Long id, Principal principal, Model model)
            throws Exception {
        User user = userService.findUserByPrincipal(principal);
        Conflict conflictToDisplay = conflictService.getConflict(id, user);

        model.addAttribute("conflict", conflictToDisplay);
        model.addAttribute("user", user);
        model.addAttribute("participants",
                conflictService.getConflictParticipants(conflictToDisplay));
        return SOMEVIEW_STRING;
    }

    @PostMapping("/solveConflict")
    public String solveConflict(@RequestBody ResolveConflict resolveConflict, Principal principal,
            Model model) throws Exception {
        User user = userService.findUserByPrincipal(principal);
        Conflict conflictToSolve = conflictService
                .getConflict(resolveConflict.getConflictId(), user);

        conflictService.solveConflict(conflictToSolve, user, resolveConflict.getDepositReceiver());
        conflictService.deactivateConflict(resolveConflict.getConflictId(), user);
        List<Conflict> conflicts = conflictService.getAllConflictsByUser(user);

        model.addAttribute("conflicts", conflicts);
        model.addAttribute("user", user);
        return SOMEVIEW_STRING;
    }
}

