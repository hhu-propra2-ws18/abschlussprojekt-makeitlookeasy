package de.propra2.ausleiherino24.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.data.CustomerReviewRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.PpTransactionRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.email.EmailConfig;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Person;
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
import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ActiveProfiles(profiles = "test")
public class CaseControllerTest {

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
    private PpTransactionRepository ppTransactionRepository;
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CalendarEventService calendarEventService;
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

    private User user;
    private Article article;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUsername("user");
        user.setRole("user");
        user.setPerson(mock(Person.class));

        article = mock(Article.class);
        article.setId(1L);
    }

    @Disabled
    @Test
    @WithMockUser(roles = "user")
    public void successfulDisplayArticleStatusTest() throws Exception {
        Mockito.when(article.getOwner()).thenReturn(user);
        Mockito.when(userService.findUserByPrincipal(any(Principal.class))).thenReturn(user);
        Mockito.when(articleService.findArticleById(1L)).thenReturn(article);

        mvc.perform(MockMvcRequestBuilders.get("/article?id=1"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers
                        .view().name("/shop/item"))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("article", article))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", user));
    }

    @Disabled
    @Test
    @WithMockUser(roles = "user")
    public void createNewArticleStatusTest() throws Exception {
        Mockito.when(userService.findUserByPrincipal(any(Principal.class))).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/newArticle"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers
                        .view().name("/shop/newItem"))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("article", Matchers.isA(Article.class)));
    }

    @Disabled
    @Test
    @WithMockUser(roles = "user")
    public void saveNewArticleStatusTest() throws Exception {
        final MultipartFile file = mock(MultipartFile.class);

        Mockito.when(userService.findUserByPrincipal(any(Principal.class))).thenReturn(user);
        Mockito.when(imageService.store(any(MultipartFile.class), eq(null))).thenReturn("");

        mvc.perform(MockMvcRequestBuilders.multipart("/saveNewArticle?image=" + file))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers
                        .view().name("redirect:/"));
    }

    @Disabled
    @Test
    public void saveNewCaseAndArticleModelTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/saveNewArticle").flashAttr("article", article))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("case", Matchers.isA(Case.class)))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("case",
                                Matchers.hasProperty("article", Matchers.equalTo(article))));
        Mockito.verify(articleService, Mockito.times(1))
                .saveArticle(ArgumentMatchers.refEq(article), "Created");
    }

    @Disabled
    @Test
    public void saveEditedCaseAndArticleStatusTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/saveEditedArticle"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk());
    }

    @Disabled
    @Test
    public void saveEditedCaseAndArticleViewTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/saveEditedArticle"))
                .andExpect(MockMvcResultMatchers
                        .view().name("article"));
    }

    @Disabled
    @Test
    public void saveEditedCaseAndArticleModelTest() throws Exception {
        final Article article = new Article();
        article.setId(1L);

        mvc.perform(MockMvcRequestBuilders.put("/saveEditedArticle").flashAttr("article", article))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("article", Matchers.equalTo(article)));
        Mockito.verify(articleRepository, Mockito.times(1)).save(ArgumentMatchers.refEq(article));
    }

    @Disabled
    @Test
    public void deactivateArticleStatusTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/deactivateArticle"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk());
    }

    @Disabled
    @Test
    public void deactivateArticleViewTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/deactivateArticle"))
                .andExpect(MockMvcResultMatchers
                        .view().name("index"));
    }

    @Disabled
    @Test
    public void deactivateArticleModelTest() throws Exception {
        final Article article = new Article();
        article.setId(1L);
        final Article article2 = new Article();
        article2.setId(2L);
        final Article article3 = new Article();
        article3.setId(3L);
        final Case c1 = new Case();
        c1.setArticle(article);
        final Case c2 = new Case();
        c2.setArticle(article2);
        final Case c3 = new Case();
        c3.setArticle(article3);
        final ArrayList<Case> cas = new ArrayList<>();
        cas.add(c1);
        cas.add(c2);
        cas.add(c3);

        Mockito.when(caseRepository.findAll()).thenReturn(cas);
        mvc.perform(MockMvcRequestBuilders.put("/deactivateArticle").flashAttr("article", article));
        Mockito.verify(articleRepository, Mockito.times(1)).save(ArgumentMatchers.refEq(article));
        Mockito.verify(caseRepository, Mockito.times(1)).save(ArgumentMatchers.refEq(c1));
        Assertions.assertThat(c1.isActive()).isFalse();
        Assertions.assertThat(article.isActive()).isFalse();
    }

}
