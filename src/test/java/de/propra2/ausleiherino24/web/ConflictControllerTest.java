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
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
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
import java.util.Arrays;
import java.util.Optional;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles(profiles = "test")
public class ConflictControllerTest {
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

    private User user;
    private User user2;
    private Article art;
    private Case ca;
    private Conflict c1;

    @Before
    public void init(){
        user = new User();
        user2 = new User();
        art = new Article();
        ca = new Case();
        c1 = new Conflict();

        user2.setUsername("user2");
        user.setUsername("user1");
        art.setOwner(user);
        ca.setArticle(art);
        ca.setReceiver(user2);
        c1.setConflictedCase(ca);
        c1.setConflictReporterUsername("user1");
        c1.setConflictDescription("TestDescription");
    }

    @Test
    @WithMockUser(roles = "user")
    public void test() throws Exception {
        Mockito.when(caseRepository.findById(1L)).thenReturn(Optional.of(ca));

        mvc.perform(MockMvcRequestBuilders.post("/accessed/user/openconflict?id=1")
                .flashAttr("conflictDescription", "TestDescription"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/myOverview?returned&openedconflict"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        //Mockito.verify(conflictService, Mockito.times(1)).openConflict(ca, "TestDescription");
    }

    @Test
    @WithMockUser(roles = "user")
    public void test2() throws Exception {
        Mockito.when(userService.findUserByPrincipal(Mockito.any(Principal.class))).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.delete("/accessed/user/deactivateconflict?id=1"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        .andExpect(MockMvcResultMatchers.redirectedUrl("/myOverview?returned&deactivatedconflict"));
        //Mockito.verify(conflictService).deactivateConflict(1L, user);
    }
}
