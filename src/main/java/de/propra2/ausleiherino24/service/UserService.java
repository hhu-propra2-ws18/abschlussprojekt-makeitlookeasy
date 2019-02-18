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
}
