package de.propra2.ausleiherino24.web;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.ui.Model;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CustomUserDetailsService;
import de.propra2.ausleiherino24.service.RoleService;
import de.propra2.ausleiherino24.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest
public class UserControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	UserRepository users;

	@MockBean
	private CaseRepository cases;

	@MockBean
	private CustomUserDetailsService userDetailsService;

	@MockBean
	private ArticleRepository articles;

	@MockBean
	private PersonRepository persons;
	
	@MockBean
	UserService us;
	
	@MockBean
	private ArticleService as;
	
	//@MockBean
	//private RoleService rs;

	@Test
	public void displayUserProfileStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/accessed/user/user?id=1")).andExpect(MockMvcResultMatchers.status().is3xxRedirection());
	}

	@Test
	public void displayUserProfileViewTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/accessed/user?id=1")).andExpect(MockMvcResultMatchers.view().name("profile"));
	}

	@Test
	public void displayUserProfileModelTest() throws Exception {
		User user = new User();
		user.setId(1L);
		user.setEmail("user@mail.com");
		user.setPassword("password");
		user.setUsername("user1");

		Mockito.when(users.getById(1L)).thenReturn(user);
		
		mvc.perform(MockMvcRequestBuilders.get("/accessed/user?id=1"))
				.andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.is(user)));
		Mockito.verify(users, Mockito.times(1)).getById(1L);
	}
	
	@Test
	public void getIndexStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/accessed/user/index")).andExpect(MockMvcResultMatchers.status().is3xxRedirection());
	}
	
	@Test
	public void getIndexRedirectTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/accessed/user/index")).andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login"));
	}
	
//	@SuppressWarnings("static-access")
//	@Test
//	public void getIndexModelTest() throws Exception {
//		RoleService rs = Mockito.mock(RoleService.class);
//		MockHttpServletRequest req = new MockHttpServletRequest();
//		Mockito.when(rs.getUserRole(Mockito.anyObject())).thenReturn("user");
//		mvc.perform(MockMvcRequestBuilders.get("/accessed/user/index")).andExpect(MockMvcResultMatchers.model().attribute("role", "user"));
//	}
	
	
	// TODO: display user profile

	// TODO: edit own profile

}
