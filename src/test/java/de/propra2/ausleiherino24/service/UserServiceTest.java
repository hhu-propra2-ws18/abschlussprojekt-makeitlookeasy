package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.User;
import org.junit.Before;
import org.junit.Test;

import java.security.Principal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
	private UserService userService;
	private UserRepository userRepositoryMock;

	@Before
	public void setup(){
		userRepositoryMock = mock(UserRepository.class);
		userService = new UserService(null, userRepositoryMock);
	}

	@Test
	public void oneUserShouldBeMappedToNePerson () {

	}

	@Test
	public void findUserByNullPrincipal() throws Exception {
		User user = userService.findUserByPrincipal(null);

		assertEquals("", user.getRole());
		assertEquals("", user.getUsername());
	}

	@Test
	public void findUserByPrincipalWithoutName() throws Exception {
		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn(null);

		User user = userService.findUserByPrincipal(principal);

		assertEquals("", user.getRole());
		assertEquals("", user.getUsername());
	}

	@Test
	public void findUserByPrinipal() throws Exception {
		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn("");
		Optional<User> op = Optional.of(new User());
		when(userRepositoryMock.findByUsername("")).thenReturn(op);

		User user = userService.findUserByPrincipal(principal);

		assertEquals(op.get(), user);
		verify(userRepositoryMock, times(2)).findByUsername("");
	}
}
