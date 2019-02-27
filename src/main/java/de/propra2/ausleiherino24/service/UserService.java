package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final PersonService personService;
    private final UserRepository userRepository;

    @Autowired
    public UserService(final PersonService personService, final UserRepository userRepository) {
        this.personService = personService;
        this.userRepository = userRepository;
    }

    /**
     * Saves given user in database.
     */
    private void saveUser(final User user, final String msg) {
        userRepository.save(user);
        LOGGER.info("{} user profile {} [ID={}]", msg, user.getUsername(), user.getId());
    }

    /**
     * Saves newly created/updated user and person data to database.
     *
     * @param user User object to be saved to database.
     * @param person Person object to be saved to database.
     * @param msg String to be displayed in the Logger.
     */
    public void saveUserWithProfile(final User user, final Person person, final String msg) {
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
    public String saveUserIfPasswordsAreEqual(final String username, final User user,
            final Person person, final String pw1,
            final String pw2) {
        if (!pw1.equals(pw2)) {
            return "PasswordNotEqual";
        }

        final Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return "UserNotFound";
        }

        final User dbUser = optionalUser.get();
        final Person dbPerson = dbUser.getPerson();
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

    /**
     * Finds user by its username.
     */
    public User findUserByUsername(final String username) {
        final Optional<User> optionalUser = userRepository.findByUsername(username);

        if (!optionalUser.isPresent()) {
            LOGGER.warn("Couldn't find user {} in UserRepository.", username);
            throw new NoSuchElementException("Couldn't find current principal in UserRepository.");
        }

        return optionalUser.get();
    }

    /**
     * Finds user by its principal.
     */
    public User findUserByPrincipal(final Principal principal) {
        User user;

        if (principal == null) {
            return buildNewUser();
        }

        try {
            user = findUserByUsername(principal.getName());
        } catch (NoSuchElementException e) {
            user = buildNewUser();
        }

        return user;
    }

    private User buildNewUser() {
        User user = new User();
        user.setRole("");
        user.setUsername("");
        return user;
    }

    User findUserById(final Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    /**
     * Checks whether given username equals the given principalname.
     */
    public boolean isCurrentUser(final String username, final String currentPrincipalName) {
        return username.equals(currentPrincipalName);
    }

}
