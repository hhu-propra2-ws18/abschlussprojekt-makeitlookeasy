package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;


@Service
public class UserService {

	private final PersonService personService;
	private final UserRepository userRepository;

	private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	public UserService(PersonService personService, UserRepository userRepository) {
		this.personService = personService;
		this.userRepository = userRepository;
	}

	private void saveUser(User user, String msg) {
		userRepository.save(user);
		LOGGER.info("%s user profile %s [ID=%L]", msg, user.getUsername(), user.getId());
	}


	/**
	 * Saves newly created/updated user and person data to database.
	 *
	 * @param user   User object to be saved to database.
	 * @param person Person object to be saved to database.
	 * @param msg    String to be displayed in the Logger.
	 */
	public void saveUserWithProfile(User user, Person person, String msg) {
		user.setRole("user");
		saveUser(user, msg);

        person.setUser(user);
        personService.savePerson(person, msg);
    }

	/**
	 * Saves User and Person in case the passwords are equal
	 * @param username
	 * @param user
	 * @param person
	 * @param pw1
	 * @param pw2
	 * @return status
	 */
	public String saveUserIfPasswordsAreEqual(String username, User user, Person person, String pw1, String pw2){
		if (!pw1.equals(pw2))
			return "PasswordNotEqual";

		Optional<User> optionalUser = userRepository.findByUsername(username);
		if(!optionalUser.isPresent()){
			return "UserNotFound";
		} else {
			User dbUser = optionalUser.get();
			Person dbPerson = dbUser.getPerson();
			dbPerson.setFirstName(person.getFirstName());
			dbPerson.setLastName(person.getLastName());
			dbPerson.setAddress(person.getAddress());
			dbUser.setPerson(person);
			dbUser.setEmail(user.getEmail());
			dbUser.setPassword(pw1);
			this.saveUser(dbUser, "Save");
			personService.savePerson(person, "Save");
			return "Success";
		}

	}


	/**
	 * Searches database for user by username. If user cannot be found, throw an exception. Else,
	 * return user.
	 *
	 * @param username String by which database gets searched.
	 * @return User object from database.
	 * @throws Exception Thrown, if no user can be found with user.username == username.
	 */
	public User findUserByUsername(String username) throws Exception {
		Optional<User> optionalUser = userRepository.findByUsername(username);

		if (!optionalUser.isPresent()) {
			LOGGER.warn("Couldn't find user %s in UserRepository.", username);
			throw new Exception("Couldn't find current principal in UserRepository.");
		}

		return optionalUser.get();
	}

	public User findUserByPrincipal(Principal principal) {
		User user;

		try {
			user = findUserByUsername(principal.getName());
		} catch (Exception e) {
			user = new User();
			user.setRole("");
			user.setUsername("");
		}

		return user;
	}

	public boolean isCurrentUser(String username, String currentPrincipalName) {
		if (username.equals(currentPrincipalName)) {
			return true;
		} else {
			LOGGER.warn("Unauthorized access to 'editProfile' for user %s by user %s", username, currentPrincipalName);
			LOGGER.info("Logging out user %s", currentPrincipalName);
			return false;
		}
	}
}
