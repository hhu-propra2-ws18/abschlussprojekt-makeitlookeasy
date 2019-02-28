package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.data.CustomerReviewRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.PpTransactionRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.email.EmailConfig;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import de.propra2.ausleiherino24.propayhandler.ReservationHandler;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CalendarEventService;
import de.propra2.ausleiherino24.service.CaseService;
import de.propra2.ausleiherino24.service.ConflictService;
import de.propra2.ausleiherino24.service.CustomerReviewService;
import de.propra2.ausleiherino24.service.ImageService;
import de.propra2.ausleiherino24.service.PersonService;
import de.propra2.ausleiherino24.service.SearchUserService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Principal principal;
    @MockBean
    private CalendarEventService calendarEventService;

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
    private PpTransactionRepository ppTransactionRepository;
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

    @Disabled
    @Test
    public void displayUserProfileStatusTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/accessed/user/user?id=1"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection());
    }

    @Disabled //TODO: fix test
    @Test
    public void displayUserProfileViewTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/accessed/user?id=1"))
                .andExpect(MockMvcResultMatchers
                        .view().name("profile"));
    }

    @Disabled //TODO: fix test
    @Test
    public void displayUserProfileModelTest() throws Exception {
        final User user = User.builder()
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

    @Disabled
    @Test
    public void getIndexStatusTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/accessed/user/index"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers
                        .redirectedUrl("http://localhost/login"));
    }

}
