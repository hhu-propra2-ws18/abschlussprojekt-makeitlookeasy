package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.ResolveConflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ConflictService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.List;
import javax.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConflictController {

    private final ConflictService conflictService;
    private final UserService userService;

    @Autowired
    public ConflictController(ConflictService conflictService, UserService userService) {
        this.conflictService = conflictService;
        this.userService = userService;
    }

    /**
     * TODO JavaDoc.
     *
     * @param conflict Description
     * @param bindingResult Description
     * @param model Description
     * @param principal Description
     * @return Description
     * @throws Exception Description
     */
    @PostMapping("/accessed/user/openconflict")
    public String sendConflict(@RequestParam Long id, @RequestBody Conflict conflict,
            BindingResult bindingResult, Model model, Principal principal) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Conflict is not Valid");
        }

        User user = userService.findUserByPrincipal(principal);
        conflictService.saveConflict(conflict, user);
        conflictService.sendConflictEmail(conflict);

        model.addAttribute("user", user);
        model.addAttribute("conflicts", conflictService.getAllConflictsByUser(user));
        return "someView";
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

