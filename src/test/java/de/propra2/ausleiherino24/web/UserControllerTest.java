package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.data.CustomerReviewRepository;
import de.propra2.ausleiherino24.data.PPTransactionRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.email.EmailConfig;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import de.propra2.ausleiherino24.propayhandler.ReservationHandler;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CaseService;
import de.propra2.ausleiherino24.service.ConflictService;
import de.propra2.ausleiherino24.service.CustomerReviewService;
import de.propra2.ausleiherino24.service.ImageService;
import de.propra2.ausleiherino24.service.PersonService;
import de.propra2.ausleiherino24.service.RoleService;
import de.propra2.ausleiherino24.service.SearchUserService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
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


//@PowerMockIgnore("javax.security.*") TODO: Replace PowerMock with JMockit
@RunWith(SpringRunner.class)
@WebMvcTest
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Principal principal;

    @MockBean
    private ArticleRepository articleRepository;
    @MockBean
    private CaseRepository caseRepository;
    @MockBean
    private ConflictRepository conflictRepository;
    @MockBean
    private CustomerReviewRepository customerReviewRepository;
    @MockBean
    private PersonRepository personRepository;
    @MockBean
    private PPTransactionRepository ppTransactionRepository;
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ArticleService articleService;
    @MockBean
    private CaseService caseService;
    @MockBean
    private ConflictService conflictService;
    @MockBean
    private CustomerReviewService customerReviewService;
    @MockBean
    private ImageService imageService;
    @MockBean
    private PersonService personService;
    @MockBean
    private RoleService roleService;
    @MockBean
    private SearchUserService searchUserService;
    @MockBean
    private UserService userService;

    @MockBean
    private EmailConfig emailConfig;
    @MockBean
    private EmailSender emailSender;
    @MockBean
    private AccountHandler accountHandler;
    @MockBean
    private ReservationHandler reservationHandler;

    @Ignore
    @Test
    public void displayUserProfileStatusTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/accessed/user/user?id=1"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection());
    }

    @Ignore //TODO: fix test
    @Test
    public void displayUserProfileViewTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/accessed/user?id=1"))
                .andExpect(MockMvcResultMatchers
                        .view().name("profile"));
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

        Mockito.when(userService.findUserByUsername("user1")).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/accessed/user/profile/user1"))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", Matchers.is(Optional.of(user))));
        Mockito.verify(userService, Mockito.times(1)).findUserByUsername("user1");
    }

    @Ignore
    @Test
    public void getIndexStatusTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/accessed/user/index"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers
                        .redirectedUrl("http://localhost/login"));
    }

// TODO: Dead?
//	@SuppressWarnings("static-access")
//	@Test
//	public void getIndexModelTest() throws Exception {
//		RoleService rs = Mockito.control(RoleService.class);
//		MockHttpServletRequest req = new MockHttpServletRequest();
//		Mockito.when(rs.getUserRole(Mockito.anyObject())).thenReturn("user");
//		mvc.perform(MockMvcRequestBuilders.get("/accessed/user/index")).andExpect(MockMvcResultMatchers.model().attribute("role", "user"));
//	}
}
