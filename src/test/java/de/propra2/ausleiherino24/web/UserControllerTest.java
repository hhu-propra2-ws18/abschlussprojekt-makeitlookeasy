package de.propra2.ausleiherino24.web;

import static org.mockito.Mockito.mock;

import de.propra2.ausleiherino24.features.category.Category;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.data.AccountHandler;
import de.propra2.ausleiherino24.propayhandler.model.PpTransaction;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CaseService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    @MockBean
    private CaseService caseService;
    @MockBean
    private ArticleService articleService;
    @MockBean
    private AccountHandler accountHandler;

    private User user1;
    private User user2;
    private Article article;
    private Person person1;
    private Case case1;


    @BeforeEach
    public void init() {
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("hans");
        user1.setRole("user1");
        //user1.setPerson(mock(Person.class));

        user2 = new User();
        user2.setUsername("peter");
        user2.setRole("user2");
        user2.setPerson(mock(Person.class));

        person1 = new Person();
        person1.setId(1L);
        person1.setUser(user1);
        person1.setFirstName("hans");
        person1.setLastName("peter");
        user1.setPerson(person1);

        article = new Article();
        article.setId(1L);
        article.setOwner(user1);

        case1 = new Case();
        case1.setArticle(article);
        case1.setReceiver(user2);
        case1.setEndTime(123435L);
        case1.setStartTime(1234L);
        case1.setPrice(300.0);
    }


    @Test
    @WithMockUser(roles = "user", username = "hans")
    void getUserProfileShouldEnableEditingOptionsIfCurrentUserIsVisitedUser() throws Exception {
        Mockito.when(articleService.findAllActiveByUser(user1)).thenReturn(Arrays.asList(article));
        Mockito.when(userService.findUserByPrincipal(Mockito.any(Principal.class))).thenReturn(
                user1);
        Mockito.when(userService.findUserByUsername("hans")).thenReturn(user1);
        mvc.perform(MockMvcRequestBuilders.get("/profile/hans"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/user/profileEdit"))
                .andExpect(MockMvcResultMatchers.model().attribute("myArticles",
                        Matchers.is(Matchers.equalTo(Arrays.asList(article)))))
                .andExpect(MockMvcResultMatchers.model().attribute("visitedUser", user1))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user1))
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("categories", Category.getAllCategories()));
    }

    @Test
    @WithMockUser(roles = "user", username = "hans")
    void getUserProfileShouldNotEnableEditingOptionsIfCurrentUserIsNotVisitedUser()
            throws Exception {
        Mockito.when(articleService.findAllActiveByUser(user2)).thenReturn(Arrays.asList(article));
        Mockito.when(userService.findUserByPrincipal(Mockito.any(Principal.class))).thenReturn(
                user1);
        Mockito.when(userService.findUserByUsername("peter")).thenReturn(user2);
        mvc.perform(MockMvcRequestBuilders.get("/profile/peter"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/user/profile"))
                .andExpect(MockMvcResultMatchers.model().attribute("myArticles",
                        Matchers.is(Matchers.equalTo(Arrays.asList(article)))))
                .andExpect(MockMvcResultMatchers.model().attribute("visitedUser", user2))
                .andExpect(MockMvcResultMatchers.model().attribute("user", user1))
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("categories", Category.getAllCategories()));
    }

    @Test
    @WithMockUser(roles = "user", username = "hans")
    void editUserProfileShouldLogoutIfUserIsNotCurrentUser() throws Exception {
        Mockito.when(userService.isCurrentUser(user1.getUsername(), "hans")).thenReturn(false);
        mvc.perform(MockMvcRequestBuilders.put("/editProfile")
                .flashAttr("user", user1)
                .flashAttr("person", person1))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/logout"));
    }

    @Test
    @WithMockUser(roles = "user", username = "hans")
    void getMyArticlePageShouldReturnCurrentUsersOverview() throws Exception {
        Mockito.when(userService.findUserByPrincipal(Mockito.any(Principal.class)))
                .thenReturn(user1);
        Mockito.when(articleService.findAllActiveForRental(user1))
                .thenReturn(Arrays.asList(article));
        Mockito.when(articleService.findAllActiveForSale(user1)).thenReturn(Arrays.asList(article));
        Mockito.when(caseService
                .getLendCasesFromPersonReceiver(user1.getPerson().getId()))
                .thenReturn(Arrays.asList(case1));
        Mockito.when(caseService
                .findAllRequestedCasesByUserId(user1.getId())).thenReturn(Arrays.asList(case1));
        Mockito.when(caseService
                .findAllExpiredCasesByUserId(user1.getId())).thenReturn(Arrays.asList(case1));
        Mockito.when(caseService.findAllSoldItemsByUserId(user1.getId()))
                .thenReturn(Arrays.asList(case1));
        Mockito.when(caseService
                .findAllOutrunningCasesByUserId(user1.getId())).thenReturn(Arrays.asList(case1));

        List<Article> articleList = Arrays.asList(article);
        List<Case> caseList = Arrays.asList(case1);
        mvc.perform(MockMvcRequestBuilders.get("/myOverview"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/user/myOverview"))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", Matchers.is(Matchers.equalTo(user1))))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("categories",
                                Matchers.is(Matchers.equalTo(Category.getAllCategories()))))
                .andExpect(MockMvcResultMatchers
                        .model()
                        .attribute("myRentalArticles", Matchers.is(Matchers.equalTo(articleList))))
                .andExpect(MockMvcResultMatchers
                        .model()
                        .attribute("mySaleArticles", Matchers.is(Matchers.equalTo(articleList))))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("borrowed", Matchers.is(Matchers.equalTo(caseList))))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("returned", Matchers.is(Matchers.equalTo(caseList))))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("requested", Matchers.is(Matchers.equalTo(caseList))))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("sold", Matchers.is(Matchers.equalTo(caseList))))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("outrunning", Matchers.is(Matchers.equalTo(caseList))));
    }

    @Test
    @WithMockUser(roles = "user", username = "hans")
    void getNewItemPageShouldGetNewItemPage() throws Exception {
        Mockito.when(userService.findUserByPrincipal(Mockito.any(Principal.class)))
                .thenReturn(user1);

        mvc.perform(MockMvcRequestBuilders.get("/newItem"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/shop/newItem"))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", Matchers.is(Matchers.equalTo(user1))))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("categories",
                                Matchers.is(Matchers.equalTo(Category.getAllCategories()))))
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("allArticles", Matchers.is(Matchers.equalTo(articleService))));
    }

    @Test
    @WithMockUser(roles = "user", username = "hans")
    void getBankAccountPageShouldShowBankAccountFoundsIfPropayIsAvailable() throws Exception {
        PpTransaction transaction = new PpTransaction();
        transaction.setAcase(case1);
        case1.setPpTransaction(transaction);
        transaction.setId(4L);
        transaction.setDate(12343L);

        Mockito.when(userService.findUserByPrincipal(Mockito.any(Principal.class)))
                .thenReturn(user1);
        Mockito.when(caseService.findAllTransactionsForPerson(person1.getId()))
                .thenReturn(Arrays.asList(transaction));
        Mockito.when(accountHandler.checkAvailability()).thenReturn(true);
        Mockito.when(accountHandler.checkFunds("hans")).thenReturn(245.0);

        mvc.perform(MockMvcRequestBuilders.get("/bankAccount"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/user/bankAccount"))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", Matchers.is(Matchers.equalTo(user1))))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("categories",
                                Matchers.is(Matchers.equalTo(Category.getAllCategories()))))
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("allArticles", Matchers.is(Matchers.equalTo(articleService))))
                .andExpect(MockMvcResultMatchers.model().attribute("transactions",
                        Matchers.is(Matchers.equalTo(Arrays.asList(transaction)))))
                .andExpect(MockMvcResultMatchers.model().attribute("pp", 245.0))
                .andExpect(MockMvcResultMatchers.model().attribute("propayUnavailable", false));
    }

    @Test
    @WithMockUser(roles = "user", username = "hans")
    void getBankAccountPageShouldNotShowBankAccountFoundsIfPropayIsUnavailable() throws Exception {
        PpTransaction transaction = new PpTransaction();
        transaction.setAcase(case1);
        case1.setPpTransaction(transaction);
        transaction.setId(4L);
        transaction.setDate(12343L);

        Mockito.when(userService.findUserByPrincipal(Mockito.any(Principal.class)))
                .thenReturn(user1);
        Mockito.when(caseService.findAllTransactionsForPerson(person1.getId()))
                .thenReturn(Arrays.asList(transaction));
        Mockito.when(accountHandler.checkAvailability()).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.get("/bankAccount"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/user/bankAccount"))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", Matchers.is(Matchers.equalTo(user1))))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("categories",
                                Matchers.is(Matchers.equalTo(Category.getAllCategories()))))
                .andExpect(MockMvcResultMatchers.model()
                        .attribute("allArticles", Matchers.is(Matchers.equalTo(articleService))))
                .andExpect(MockMvcResultMatchers.model().attribute("transactions",
                        Matchers.is(Matchers.equalTo(Arrays.asList(transaction)))))
                .andExpect(MockMvcResultMatchers.model().attribute("pp", 0D))
                .andExpect(MockMvcResultMatchers.model().attribute("propayUnavailable", true));
    }

    @Test
    @WithMockUser(roles = "user", username = "hans")
    void saveEditedUserProfileShouldRedirectToSuccessViewIfSavingUserIsSuccessful()
            throws Exception {
        Mockito.when(userService
                .saveUserIfPasswordsAreEqual("hans", user1, person1, "password", "password"))
                .thenReturn("Success");
        mvc.perform(MockMvcRequestBuilders.post("/accessed/user/saveProfile")
                .flashAttr("user", user1)
                .flashAttr("person", person1)
                .param("password", "password")
                .param("confirmPass", "password"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/profile/hans?success"));
    }

    @Test
    @WithMockUser(roles = "user", username = "hans")
    void saveEditedUserProfileShouldRedirectToUserNotFoundViewIfUserIsNotFound() throws Exception {
        Mockito.when(userService
                .saveUserIfPasswordsAreEqual("hans", user1, person1, "password", "password"))
                .thenReturn("UserNotFound");
        mvc.perform(MockMvcRequestBuilders.post("/accessed/user/saveProfile")
                .flashAttr("user", user1)
                .flashAttr("person", person1)
                .param("password", "password")
                .param("confirmPass", "password"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/profile/hans?userNotFound"));
    }

    @Test
    @WithMockUser(roles = "user", username = "hans")
    void saveEditedUserProfileShouldRedirectToPwdDoNotMatchViewIfPasswordsDoNotMatch()
            throws Exception {
        Mockito.when(userService
                .saveUserIfPasswordsAreEqual("hans", user1, person1, "password", "password1"))
                .thenReturn("PasswordNotEqual");
        mvc.perform(MockMvcRequestBuilders.post("/accessed/user/saveProfile")
                .flashAttr("user", user1)
                .flashAttr("person", person1)
                .param("password", "password")
                .param("confirmPass", "password1"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/profile/hans?pwdDoNotMatch"));
    }

    @Test
    @WithMockUser(roles = "user", username = "hans")
    void addMoneyToUserAccountShouldAddMoneyToAccountAndRedirectToSuccessViewIfPropayIsAvailable()
            throws Exception {
        Mockito.when(accountHandler.checkAvailability()).thenReturn(true);
        mvc.perform(MockMvcRequestBuilders.post("/addMoney")
                .param("money", "30.0"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/bankAccount?success"));
        Mockito.verify(accountHandler, Mockito.times(1)).addFunds("hans", 30.0);
    }

    @Test
    @WithMockUser(roles = "user", username = "hans")
    void addMoneyToUserAccountShouldNotAddMoneyToAccountAndNotRedirectToSuccessViewIfPropayIsNotAvailable()
            throws Exception {
        Mockito.when(accountHandler.checkAvailability()).thenReturn(false);
        mvc.perform(MockMvcRequestBuilders.post("/addMoney")
                .param("money", "30.0"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/bankAccount?propayUnavailable"));
        Mockito.verify(accountHandler, Mockito.times(0)).addFunds("hans", 30.0);
    }

}
