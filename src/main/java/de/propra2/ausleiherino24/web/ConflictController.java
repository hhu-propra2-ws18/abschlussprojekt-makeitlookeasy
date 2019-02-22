package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.ResolveConflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.CaseService;
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
    private final CaseService caseService;
    private final CaseRepository caseRepository;

    @Autowired
    public ConflictController(ConflictService conflictService, UserService userService,
            CaseService caseService, CaseRepository caseRepository) {
        this.conflictService = conflictService;
        this.userService = userService;
        this.caseService = caseService;
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
    @PostMapping("/accessed/user/openconflict")
    public String sendConflict(@RequestParam Long id, String conflictDescription) throws Exception {
        Optional<Case> optionalCase = caseRepository.findById(id);
        if(!optionalCase.isPresent()) {
            return "redirect:/myOverview?returned&conflictfailed";
        }

        Conflict conflict = new Conflict();
        conflict.setConflictDescription(conflictDescription);
        conflict.setConflictedCase(optionalCase.get());
        conflict.setConflictReporterUsername(optionalCase.get().getArticle().getOwner().getUsername());

        conflictService.saveConflict(conflict, optionalCase.get().getOwner());
        conflictService.sendConflictEmail(conflict);

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
    @DeleteMapping("/deactivateConflict")
    public String deactivateConflict(@RequestParam Long id, Principal principal, Model model)
            throws Exception {
        User user = userService.findUserByPrincipal(principal);
        conflictService.deactivateConflict(id, user);

        model.addAttribute("user", user);
        model.addAttribute("conflicts", conflictService.getAllConflictsByUser(user));
        return "someView";
    }

    /**
     * Todo Javadoc.
     * @param id Description
     * @param principal Description
     * @param model Description
     * @return Description
     * @throws Exception Description
     */
    @GetMapping("/conflict")
    public String displayConflict(@RequestParam("id") Long id, Principal principal, Model model)
            throws Exception {
        User user = userService.findUserByPrincipal(principal);
        Conflict conflictToDisplay = conflictService.getConflict(id, user);

        model.addAttribute("conflict", conflictToDisplay);
        model.addAttribute("user", user);
        return "someView";
    }

    /**
     * TODO Javadoc.
     * @param principal Description
     * @param model Description
     * @return Description
     * @throws Exception Description
     */
    @GetMapping("/conflicts")
    public String displayAllConflicts(Principal principal, Model model) throws Exception {
        User user = userService.findUserByPrincipal(principal);
        List<Conflict> conflicts = conflictService.getAllConflictsByUser(user);

        model.addAttribute("conflicts", conflicts);
        model.addAttribute("user", user);
        return "someView";
    }

    /**
     * TODO Javadoc.
     * @param id Description
     * @param principal Description
     * @param model Description
     * @return Description
     * @throws Exception Description
     */
    @GetMapping("/solveConflictView")
    public String solveConflictView(@RequestParam("id") Long id, Principal principal, Model model)
            throws Exception {
        User user = userService.findUserByPrincipal(principal);
        Conflict conflictToDisplay = conflictService.getConflict(id, user);

        model.addAttribute("conflict", conflictToDisplay);
        model.addAttribute("user", user);
        model.addAttribute("participants",
                conflictService.getConflictParticipants(conflictToDisplay));
        return "someView";
    }

    /**
     * TODO Javadoc.
     * @param resolveConflict Description
     * @param principal Description
     * @param model Description
     * @return Description
     * @throws Exception Description
     */
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
        return "someView";
    }
}

