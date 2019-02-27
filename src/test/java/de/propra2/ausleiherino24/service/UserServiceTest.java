package de.propra2.ausleiherino24.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import java.security.Principal;
import java.util.NoSuchElementException;
import java.util.Optional;
import mockit.Mocked;
import mockit.Verifications;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @Mocked
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private UserRepository users;
    private PersonService personService;
    private UserService userService;
    private User user;

    @BeforeEach
    public void setup() {
        users = Mockito.mock(UserRepository.class);
        personService = Mockito.mock(PersonService.class);
        user = new User();
        user.setUsername("user1");
        user.setId(1L);
        Mockito.when(users.findByUsername("user1")).thenReturn(Optional.of(user));
        userService = new UserService(personService, users);
    }

    @Test
    public void findUserByUsernameTest() {
        Assertions.assertThat(userService.findUserByUsername("user1")).isEqualTo(user);
    }

    @Test
    public void findUserByUsernameTest2() {

        assertThrows(NoSuchElementException.class, () -> {
            userService.findUserByUsername("user2");

            new Verifications() {{
                LOGGER.warn("Couldn't find user {} in UserRepository.", "user2");
                times = 1;
            }};
        });

    }

    @Test
    public void saveUserWithProfileTest() {
        final Person person = new Person();
        person.setId(1L);
        userService.saveUserWithProfile(user, person, "str");

        Assertions.assertThat(user.getRole()).isEqualTo("user");
        Assertions.assertThat(person.getUser()).isEqualTo(user);

        Mockito.verify(users, Mockito.times(1)).save(user);
        Mockito.verify(personService, Mockito.times(1)).savePerson(person, "str");
    }

    @Test
    public void findUserByPrincipalTest() {
        final Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("");
        final User expected = new User();
        expected.setUsername("");
        expected.setRole("");
        Mockito.when(users.findByUsername("")).thenReturn(Optional.of(expected));

        Assertions.assertThat(userService.findUserByPrincipal(principal)).isEqualTo(expected);
    }

    @Test
    public void findUserByPrincipalTest2() {
        final Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn(null);
        final User expected = new User();
        expected.setUsername("");
        expected.setRole("");

        Assertions.assertThat(userService.findUserByPrincipal(principal)).isEqualTo(expected);
    }

    @Test
    public void findUserByPrincipalTest3() {
        final User expected = new User();
        expected.setUsername("");
        expected.setRole("");

        Assertions.assertThat(userService.findUserByPrincipal(null)).isEqualTo(expected);
    }

    @Test
    public void saveUserUnequalPasswords() {
        Assertions.assertThat(
                userService.saveUserIfPasswordsAreEqual("", new User(), new Person(), "1", "2"))
                .isEqualTo("PasswordNotEqual");
    }

    @Test
    public void saveNotExistingUserWithEqualPasswords() {
        Mockito.when(users.findByUsername("")).thenReturn(Optional.empty());

        Assertions.assertThat(
                userService.saveUserIfPasswordsAreEqual("", new User(), new Person(), "1", "1"))
                .isEqualTo("UserNotFound");
    }

    @Test
    public void saveExistingUserWithEqualPasswords() {
        final String pw = "1";
        final Person person = new Person();
        final User user = new User();
        user.setPerson(person);
        user.setPassword(pw);
        user.setEmail("test@mail.de");
        Mockito.when(users.findByUsername("")).thenReturn(Optional.of(user));
        final ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);

        Assertions.assertThat(userService.saveUserIfPasswordsAreEqual("", user, person, pw, pw))
                .isEqualTo("Success");
        Mockito.verify(personService).savePerson(Mockito.eq(person), Mockito.eq("Save"));
        Mockito.verify(users).save(argument.capture());
        Assertions.assertThat(argument.getValue().getPassword()).isEqualTo(pw);
        Assertions.assertThat(argument.getValue().getEmail()).isEqualTo("test@mail.de");
        Assertions.assertThat(argument.getValue().getPerson()).isEqualTo(person);
    }
}

