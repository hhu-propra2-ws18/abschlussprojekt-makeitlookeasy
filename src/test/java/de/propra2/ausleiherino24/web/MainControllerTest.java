package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.UserService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles(profiles = "test")
public class MainControllerTest {

	@Autowired private MockMvc mvc;
	
	@MockBean UserService us;

	@Test
	public void indexStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/"))
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
	public void getLoginStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/login"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("login"));
	}

	@Test
	public void getRegistrationStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/signup"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("registration"));
	}
	
	@Test
	public void getRegistrationModelTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/signup"))
				.andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.instanceOf(User.class)))
				.andExpect(MockMvcResultMatchers.model().attribute("person", Matchers.instanceOf(Person.class)));
	}

	@Test
	public void defaultAfterLoginStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/default"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/accessed/admin/index"));
	}

//	@Test
//	public void defaultAfterLoginRedirectTest2() throws Exception {
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.addUserRole("ROLE_user");
//
//		mvc.perform(MockMvcRequestBuilders.get("/default").flashAttr("request", request))
//				.andExpect(MockMvcResultMatchers.redirectedUrl("/accessed/user/index"));
//	}
	
	@Test
	public void registerNewUserStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/registerNewUser"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("login"));
	}
	
	@Test
	public void registerNewUserModelTest() throws Exception {
		Person person = new Person();
		User user = new User();
		Map <String, Object> map = new HashMap<>();
		
		person.setId(1L);
		user.setId(1L);
		map.put("person", person);
		map.put("user", user);
		
		mvc.perform(MockMvcRequestBuilders.post("/registerNewUser").flashAttrs(map))
				.andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.instanceOf(User.class)))
				.andExpect(MockMvcResultMatchers.model().attribute("person", Matchers.instanceOf(Person.class)));
		Mockito.verify(us, Mockito.times(1)).createUserWithProfile(ArgumentMatchers.refEq(user), ArgumentMatchers.refEq(person));
	}
	
}
