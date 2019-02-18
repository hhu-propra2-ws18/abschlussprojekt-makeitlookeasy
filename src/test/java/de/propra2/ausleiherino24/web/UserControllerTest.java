package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.ImageStoreService;
import de.propra2.ausleiherino24.service.SearchUserService;
import de.propra2.ausleiherino24.service.UserService;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest
public class UserControllerTest {

  @MockBean
  UserService us;

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
  private ImageStoreService imageStoreService;

  @MockBean
  private ArticleService as;

  @MockBean
  private SearchUserService userDetailsService;

  @Ignore
  @Test
  public void displayUserProfileStatusTest() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/accessed/user/user?id=1"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
  }

  @Ignore //TODO: fix test
  @Test
  public void displayUserProfileViewTest() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/accessed/user?id=1"))
        .andExpect(MockMvcResultMatchers.view().name("profile"));
  }

  @Ignore //TODO: fix test
  @Test
  public void displayUserProfileModelTest() throws Exception {
    User user = User.builder()
        .id(1L)
        .email("user@mail.com")
        .password("password")
        .username("user1")
        .role("admin")
        .build();

    Mockito.when(users.findByUsername("user1")).thenReturn(Optional.of(user));

    mvc.perform(MockMvcRequestBuilders.get("/accessed/user/profile/user1"))
        .andExpect(MockMvcResultMatchers.model().attribute("user", Matchers.is(Optional.of(user))));

    Mockito.verify(users, Mockito.times(1)).findByUsername("user1");
  }

  @Ignore
  @Test
  public void getIndexStatusTest() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/accessed/user/index"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("http://localhost/login"));
  }

// TODO:
//	@SuppressWarnings("static-access")
//	@Test
//	public void getIndexModelTest() throws Exception {
//		RoleService rs = Mockito.mock(RoleService.class);
//		MockHttpServletRequest req = new MockHttpServletRequest();
//		Mockito.when(rs.getUserRole(Mockito.anyObject())).thenReturn("user");
//		mvc.perform(MockMvcRequestBuilders.get("/accessed/user/index")).andExpect(MockMvcResultMatchers.model().attribute("role", "user"));
//	}
}
