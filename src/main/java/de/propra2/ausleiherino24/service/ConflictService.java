package de.propra2.ausleiherino24.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;

@Service
public class ConflictService {
	private final ConflictRepository conflicts;
	private final EmailSender emailSender;
	private final AccountHandler accountHandler;
	private final Logger LOGGER = LoggerFactory.getLogger(ConflictService.class);

	@Autowired
	public ConflictService(ConflictRepository conflicts, EmailSender emailSender, AccountHandler accountHandler) {
		this.conflicts = conflicts;
		this.emailSender = emailSender;
		this.accountHandler = accountHandler;
	}

	public void saveConflict(Conflict conflict, User user) throws Exception {
		if(conflict == null) {
			throw new Exception("No such conflict");
		}
		isCorrectUser(conflict, user);
		conflicts.save(conflict);
	}

	public void sendConflictEmail(Conflict conflict) throws Exception {
		emailSender.sendEmail(conflict);
	}

	public void deactivateConflict(Long id, User user) throws Exception {
		Optional<Conflict> conflictToDeactivate = conflicts.findById(id);
		if(!conflictToDeactivate.isPresent()) {
			throw new Exception("No such conflict");
		}
		isCorrectUser(conflictToDeactivate.get(), user);
		conflicts.delete(conflictToDeactivate.get());
	}

	public List<Conflict> getAllConflictsByUser(User user){
		List<Conflict> allConflicts = new ArrayList<>();
		allConflicts.addAll(conflicts.findAllByReceiver(user));
		allConflicts.addAll(conflicts.findAllByArticleOwner(user));

		return allConflicts;
	}

	public Conflict getConflict(Long id, User user) throws Exception {
		Optional<Conflict> conflict = conflicts.findById(id);
		if(!conflict.isPresent()) {
			throw new Exception("No such conflict");
		}
		isCorrectUser(conflict.get(), user);
		return conflict.get();
	}

	public boolean isConflictedArticleOwner(Conflict conflict, User user) throws Exception {
		if(user == null) {
			throw new Exception("No such user");
		}
		return user.equals(conflict.getConflictedCase().getOwner());
	}

	private boolean isCorrectUser(Conflict conflict, User user) throws Exception {
		if(!user.getUsername().equals(conflict.getConflictReporterUsername())) {
			throw new Exception("Access denied!");
		}
		return true;
	}


	public boolean isUserAdmin(HttpServletRequest request) {
		if("admin".equals(RoleService.getUserRole(request))) {
			return true;
		}
		return false;
	}

	public void solveConflict(HttpServletRequest request) throws Exception {
		if(!isUserAdmin(request)) {
			throw new Exception("No permission!");
		}
		//accountHandler
	}

}
