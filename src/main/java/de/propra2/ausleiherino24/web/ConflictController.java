package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.model.Conflict;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ConflictService;
import de.propra2.ausleiherino24.service.UserService;

@Controller
public class ConflictController {

	private final ConflictRepository conflicts;
	private final ConflictService conflictService;
	private final UserService userService;

	@Autowired
	public ConflictController(ConflictRepository conflicts, ConflictService conflictService, UserService userService) {
		this.conflicts = conflicts;
		this.conflictService = conflictService;
		this.userService = userService;
	}

	@PostMapping("/newConflict")
	public String sendConflict(@RequestBody Conflict conflict, BindingResult bindingResult, Model model, Principal principal, HttpServletRequest request) throws Exception {
		if (bindingResult.hasErrors()) {
			throw new ValidationException("Conflict is not Valid");
		}
		User user = userService.findUserByPrincipal(principal);
		conflictService.saveConflict(conflict, user);
		conflictService.sendConflictEmail(conflict);
		return "someView";
	}

	@PutMapping("/deactivateConflict")
	public String deactivateConflict(@RequestParam Long id, Principal principal, Model model, HttpServletRequest request) throws Exception {
		User user = userService.findUserByPrincipal(principal);
		conflictService.deactivateConflict(id, user);
		model.addAttribute("user", user);
		model.addAttribute("conflicts", conflictService.getAllConflictsByUser(user));
		return "someView";
	}

	@GetMapping("/conflict")
	public String displayConflict(@RequestParam("id") Long id, Principal principal, Model model, HttpServletRequest request) throws Exception {
		User user = userService.findUserByPrincipal(principal);
		Conflict confictToDisplay = conflictService.getConflict(id, user);

		model.addAttribute("conflict", confictToDisplay);
		model.addAttribute("user", user);
		return "someView";
	}

	@GetMapping("/conflicts")
	public String displayAllConflicts(Principal principal, Model model, HttpServletRequest request) throws Exception {
		User user = userService.findUserByPrincipal(principal);
		List<Conflict> conflicts = conflictService.getAllConflictsByUser(user);
		model.addAttribute("conflicts", conflicts);
		model.addAttribute("user", user);
		return "someView";
	}

	@GetMapping("/solveConflict")
	public String solveConflict(@RequestParam("id") Long id, Principal principal, Model model, HttpServletRequest request) throws Exception {
		User user = userService.findUserByPrincipal(principal);

		Conflict conflictToSolve = conflictService.getConflict(id, user);
		List<Conflict> conflicts = conflictService.getAllConflictsByUser(user);
		model.addAttribute("conflicts", conflicts);
		model.addAttribute("user", user);
		return "someView";
	}
}

