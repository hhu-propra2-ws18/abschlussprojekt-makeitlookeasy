package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Optional;

@PowerMockIgnore("javax.security.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({UserService.class, LoggerFactory.class})
public class UserServiceTest {

	private UserRepository users;
	private PersonService personService;
	private UserService userService;
	private Logger logger;
	private User user;

	@Before
	public void setup() {
		users = Mockito.mock(UserRepository.class);
		personService = Mockito.mock(PersonService.class);
		PowerMockito.mockStatic(LoggerFactory.class);
		logger = PowerMockito.mock(Logger.class);
		PowerMockito.when(LoggerFactory.getLogger(UserService.class)).thenReturn(logger);

		user = new User();
		user.setUsername("user1");
		user.setId(1L);
		Mockito.when(users.findByUsername("user1")).thenReturn(Optional.of(user));
		userService = new UserService(personService, users);
	}

	@Test
	public void findUserByUsernameTest() throws Exception {
		Assertions.assertThat(userService.findUserByUsername("user1")).isEqualTo(user);
	}

	@Test(expected = Exception.class)
	public void findUserByUsernameTest2() throws Exception {
		userService.findUserByUsername("user2");
		Mockito.verify(logger).warn("Couldn't find user %s in UserRepository.", "user2");
	}

	@Test
	public void saveUserWithProfileTest() {
		Person person = new Person();
		person.setId(1L);
		userService.saveUserWithProfile(user, person, "str");

		Assertions.assertThat(user.getRole()).isEqualTo("user");
		Assertions.assertThat(person.getUser()).isEqualTo(user);

		Mockito.verify(users, Mockito.times(1)).save(user);
		Mockito.verify(logger).info("%s user profile %s [ID=%L]", "str", "user1", 1L);
		Mockito.verify(personService, Mockito.times(1)).savePerson(person, "str");
	}

	@Test
	public void findUserByPrincipalTest() {
		Principal principal = Mockito.mock(Principal.class);
		Mockito.when(principal.getName()).thenReturn("");
		User expected = new User();
		expected.setUsername("");
		expected.setRole("");
		Mockito.when(users.findByUsername("")).thenReturn(Optional.of(expected));

		Assertions.assertThat(userService.findUserByPrincipal(principal)).isEqualTo(expected);
	}

	@Test
	public void findUserByPrincipalTest2() {
		Principal principal = Mockito.mock(Principal.class);
		Mockito.when(principal.getName()).thenReturn(null);
		User expected = new User();
		expected.setUsername("");
		expected.setRole("");

		Assertions.assertThat(userService.findUserByPrincipal(principal)).isEqualTo(expected);
	}

	@Test
	public void findUserByPrincipalTest3() {
		User expected = new User();
		expected.setUsername("");
		expected.setRole("");

		Assertions.assertThat(userService.findUserByPrincipal(null)).isEqualTo(expected);
	}
}

