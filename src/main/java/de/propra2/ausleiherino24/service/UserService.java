package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import java.security.Principal;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final PersonService personService;
    private final UserRepository userRepository;

    @Autowired
    public UserService(PersonService personService, UserRepository userRepository) {
        this.personService = personService;
        this.userRepository = userRepository;
    }

    private void saveUser(User user, String msg) {
        userRepository.save(user);
        logger.info("%s user profile %s [ID=%L]", msg, user.getUsername(), user.getId());
    }

    /**
     * Saves newly created/updated user and person data to database.
     *
     * @param user User object to be saved to database.
     * @param person Person object to be saved to database.
     * @param msg String to be displayed in the Logger.
     */
    public void saveUserWithProfile(User user, Person person, String msg) {
        user.setRole("user");
        saveUser(user, msg);

        person.setUser(user);
        personService.savePerson(person, msg);
    }

    /**
     * Saves User and Person in case the passwords are equal.
     *
     * @return status
     */
    public String saveUserIfPasswordsAreEqual(String username, User user, Person person, String pw1,
            String pw2) {
        if (!pw1.equals(pw2)) {
            return "PasswordNotEqual";
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
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

    public User findUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (!optionalUser.isPresent()) {
            logger.warn("Couldn't find user {} in UserRepository.", username);
            throw new NullPointerException("Couldn't find current principal in UserRepository.");
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

    User findUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public boolean isCurrentUser(String username, String currentPrincipalName) {
        return username.equals(currentPrincipalName);
    }

}
