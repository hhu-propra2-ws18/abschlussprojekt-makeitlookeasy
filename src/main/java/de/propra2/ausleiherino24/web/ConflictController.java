package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.model.Conflict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.ValidationException;

@Controller
@RequestMapping("/conflict")
public class ConflictController {

	private final EmailSender emailSender;
	private final ConflictRepository conflicts;
	//private final UserRepository users;

	@Autowired
	public ConflictController(ConflictRepository conflicts, EmailSender emailSender) {
		this.emailSender = emailSender;
		this.conflicts = conflicts;
	}

	@PostMapping("/newConflict")
	public String sendConflict(@RequestBody Conflict conflict, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationException("Conflict is not Valid");
		}
		//User conflictReporter = conflict.getConflictReporter();
		//conflictReporter.addConflict(conflict);
		conflicts.save(conflict);
		//users.save(conflictReporter);
		emailSender.sendEmail(conflict);
		return "someView";
	}
}
