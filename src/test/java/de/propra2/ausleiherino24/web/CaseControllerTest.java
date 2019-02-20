package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.*;
import de.propra2.ausleiherino24.email.EmailConfig;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import de.propra2.ausleiherino24.propayhandler.ReservationHandler;
import de.propra2.ausleiherino24.service.*;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles(profiles = "test")
public class CaseControllerTest {

	@Autowired private MockMvc mvc;

	@MockBean private Principal principal;

	@MockBean private ArticleRepository articleRepository;
	@MockBean private CaseRepository caseRepository;
	@MockBean private ConflictRepository conflictRepository;
	@MockBean private CustomerReviewRepository customerReviewRepository;
	@MockBean private PersonRepository personRepository;
	@MockBean private PPTransactionRepository ppTransactionRepository;
	@MockBean private UserRepository userRepository;

	@MockBean private ArticleService articleService;
	@MockBean private CaseService caseService;
	@MockBean private ConflictService conflictService;
	@MockBean private CustomerReviewService customerReviewService;
	@MockBean private ImageService imageService;
	@MockBean private PersonService personService;
	@MockBean private RoleService roleService;
	@MockBean private SearchUserService searchUserService;
	@MockBean private UserService userService;

	@MockBean private EmailConfig emailConfig;
	@MockBean private EmailSender emailSender;
	@MockBean private AccountHandler accountHandler;
	@MockBean private ReservationHandler reservationHandler;

	private User user;
	private Article article;

	@Before
	public void setup() {
		user = new User();
		user.setUsername("user");
		user.setRole("user");
		user.setPerson(mock(Person.class));

		article = mock(Article.class);
		article.setId(1L);
	}

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

	@Ignore
	@Test
	@WithMockUser(roles = "user")
	public void saveNewArticleStatusTest() throws Exception {
		MultipartFile file = mock(MultipartFile.class);

		Mockito.when(userService.findUserByPrincipal(any(Principal.class))).thenReturn(user);
		Mockito.when(imageService.store(any(MultipartFile.class), eq(null))).thenReturn("");

		mvc.perform(MockMvcRequestBuilders.multipart("/saveNewArticle?image=" + file))
				.andExpect(MockMvcResultMatchers
						.status().isOk())
				.andExpect(MockMvcResultMatchers
						.view().name("redirect:/"));
	}

	@Ignore
	@Test
	public void saveNewCaseAndArticleModelTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/saveNewArticle").flashAttr("article", article))
				.andExpect(MockMvcResultMatchers
						.model().attribute("case", Matchers.isA(Case.class)))
				.andExpect(MockMvcResultMatchers
						.model().attribute("case",Matchers.hasProperty("article", Matchers.equalTo(article))));
		Mockito.verify(articleService, Mockito.times(1)).saveArticle(ArgumentMatchers.refEq(article), "Created");
	}

	@Ignore
	@Test
	public void saveEditedCaseAndArticleStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.put("/saveEditedArticle"))
				.andExpect(MockMvcResultMatchers
						.status().isOk());
	}

	@Ignore
	@Test
	public void saveEditedCaseAndArticleViewTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.put("/saveEditedArticle"))
				.andExpect(MockMvcResultMatchers
						.view().name("article"));
	}

	@Ignore
	@Test
	public void saveEditedCaseAndArticleModelTest() throws Exception {
		Article article = new Article();
		article.setId(1L);

		mvc.perform(MockMvcRequestBuilders.put("/saveEditedArticle").flashAttr("article", article))
				.andExpect(MockMvcResultMatchers
						.model().attribute("article", Matchers.equalTo(article)));
		Mockito.verify(articleRepository, Mockito.times(1)).save(ArgumentMatchers.refEq(article));
	}

	@Ignore
	@Test
	public void deactivateArticleStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.put("/deactivateArticle"))
				.andExpect(MockMvcResultMatchers
						.status().isOk());
	}

	@Ignore
	@Test
	public void deactivateArticleViewTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.put("/deactivateArticle"))
				.andExpect(MockMvcResultMatchers
						.view().name("index"));
	}

	@Ignore
	@Test
	public void deactivateArticleModelTest() throws Exception {
		Article article = new Article();
		article.setId(1L);
		Article article2 = new Article();
		article2.setId(2L);
		Article article3 = new Article();
		article3.setId(3L);
		Case c1 = new Case();
		c1.setArticle(article);
		Case c2 = new Case();
		c2.setArticle(article2);
		Case c3 = new Case();
		c3.setArticle(article3);
		ArrayList<Case> cas = new ArrayList<>();
		cas.add(c1);
		cas.add(c2);
		cas.add(c3);

		Mockito.when(caseRepository.findAll()).thenReturn(cas);
		mvc.perform(MockMvcRequestBuilders.put("/deactivateArticle").flashAttr("article", article));
		Mockito.verify(articleRepository, Mockito.times(1)).save(ArgumentMatchers.refEq(article));
		Mockito.verify(caseRepository, Mockito.times(1)).save(ArgumentMatchers.refEq(c1));
		Assertions.assertThat(c1.getActive()).isFalse();
		Assertions.assertThat(article.isActive()).isFalse();
	}

}
