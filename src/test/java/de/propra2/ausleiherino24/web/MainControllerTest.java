package de.propra2.ausleiherino24.web;

import static org.mockito.Mockito.mock;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.data.CustomerReviewRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.ImageStoreService;
import de.propra2.ausleiherino24.service.PersonService;
import de.propra2.ausleiherino24.service.RoleService;
import de.propra2.ausleiherino24.service.SearchUserService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles(profiles = "test")
public class MainControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ArticleRepository articles;
	@MockBean
	private UserRepository users;
	@MockBean
	private PersonRepository persons;
	@MockBean
	private CaseRepository cases;
	@MockBean
	private ConflictRepository conflicts;

	@MockBean
	private ImageStoreService is;
	@MockBean
	private UserService us;
	@MockBean
	private PersonService ps;
	@MockBean
	private ArticleService as;
	@MockBean
	private SearchUserService uds;
	@MockBean
	private RoleService rs;
	@MockBean
	private AccountHandler ah;

	@MockBean
	private EmailSender es;
	@MockBean
	private Principal principal;

	@Ignore
	@Test
	public void getIndex() throws Exception {
		principal = mock(Principal.class);
		us = mock(UserService.class);
		User user = new User();//mock(User.class);
		user.setRole("user");
		Mockito.when(principal.getName()).thenReturn("tom");
		Mockito.when(us.findUserByUsername("tom")).thenReturn(user);
		mvc.perform(MockMvcRequestBuilders.get("/").flashAttr("principal", principal))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("index"));
	}

//	@Test
//	public void indexModelTest() throws Exception {
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.addUserRole(HttpServletRequest.BASIC_AUTH);
//		System.err.println(request);
//		// Mockito.when(request.isUserInRole(Mockito.any())).thenReturn(true);
//
//		mvc.perform(MockMvcRequestBuilders.get("/").flashAttr("request", request))
//				.andExpect(MockMvcResultMatchers.model().attribute("loggedIn", Matchers.is(true)));
//	}

	@Test
	public void getLogin() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/login"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("login"));
	}

	@Test
	public void getRegistration() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/signup"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("registration"));
	}

	@Test
	public void getRegistrationModelTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/signup"))
				.andExpect(MockMvcResultMatchers.model()
						.attribute("user", Matchers.instanceOf(User.class)))
				.andExpect(
						MockMvcResultMatchers.model()
								.attribute("person", Matchers.instanceOf(Person.class)));
	}

	@Test
	public void defaultAfterLoginStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/default"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/accessed/user/index"));
	}

	@Ignore
	@Test
	public void defaultAfterLoginRedirectTest2() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		//request.addUserRole("ROLE_admin");
		request = mock(MockHttpServletRequest.class);
		Mockito.when(request.isUserInRole("ROLE_admin")).thenReturn(true);

		mvc.perform(MockMvcRequestBuilders.get("/default").flashAttr("request", request))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/accessed/admin/index"));
	}

	@Test
	public void registerNewUserStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/registerNewUser"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
		Mockito.verify(us, Mockito.times(1)).saveUserWithProfile(ArgumentMatchers.refEq(new User()),
				ArgumentMatchers.refEq(new Person()), ArgumentMatchers.refEq("Created"));
	}

	@Test
	public void registerNewUserModelTest() throws Exception {
		Person person = new Person();
		User user = new User();
		Map<String, Object> map = new HashMap<>();

		person.setId(1L);
		user.setId(1L);
		map.put("person", person);
		map.put("user", user);

		mvc.perform(MockMvcRequestBuilders.get("/signup").flashAttrs(map))
				.andExpect(MockMvcResultMatchers.model()
						.attribute("user", Matchers.instanceOf(User.class)))
				.andExpect(
						MockMvcResultMatchers.model()
								.attribute("person", Matchers.instanceOf(Person.class)));
		//Mockito.verify(us, Mockito.times(1)).createUserWithProfile(ArgumentMatchers.refEq(user), ArgumentMatchers.refEq(person));
	}

}
