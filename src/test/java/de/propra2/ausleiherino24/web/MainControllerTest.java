package de.propra2.ausleiherino24.web;

import static org.mockito.Mockito.mock;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.data.CustomerReviewRepository;
import de.propra2.ausleiherino24.data.PPTransactionRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.email.EmailConfig;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.model.Person;
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

    @MockBean
    private ChatController chatController;

    @Ignore
    @Test
    public void getIndex() throws Exception {
        principal = mock(Principal.class);
        userService = mock(UserService.class);
        final User user = new User();//mock(User.class);
        user.setRole("user");
        Mockito.when(principal.getName()).thenReturn("tom");
        Mockito.when(userService.findUserByUsername("tom")).thenReturn(user);
        mvc.perform(MockMvcRequestBuilders.get("/").flashAttr("principal", principal))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers
                        .view().name("index"));
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

    @Ignore
    @Test
    public void getLogin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers
                        .view().name("login"));
    }

    @Ignore
    @Test
    public void getRegistration() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/signup"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers
                        .view().name("registration"));
    }

    @Ignore
    @Test
    public void getRegistrationModelTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/signup"))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", Matchers.instanceOf(User.class)))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("person", Matchers.instanceOf(Person.class)));
    }

    @Ignore
    @Test
    public void registerNewUserStatusTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/registerNewUser"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection());
        Mockito.verify(userService, Mockito.times(1))
                .saveUserWithProfile(
                        ArgumentMatchers.refEq(new User()),
                        ArgumentMatchers.refEq(new Person()),
                        ArgumentMatchers.refEq("Created"));
    }

    @Ignore
    @Test
    public void registerNewUserModelTest() throws Exception {
        final Person person = new Person();
        final User user = new User();
        final Map<String, Object> map = new HashMap<>();

        person.setId(1L);
        user.setId(1L);
        map.put("person", person);
        map.put("user", user);

        mvc.perform(MockMvcRequestBuilders.get("/signup").flashAttrs(map))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", Matchers.instanceOf(User.class)))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("person", Matchers.instanceOf(Person.class)));
        //Mockito.verify(us, Mockito.times(1)).createUserWithProfile(ArgumentMatchers.refEq(user), ArgumentMatchers.refEq(person));
    }

}
