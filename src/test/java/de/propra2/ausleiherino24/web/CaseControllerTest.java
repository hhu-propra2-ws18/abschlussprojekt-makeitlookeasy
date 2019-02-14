package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CustomUserDetailsService;
import de.propra2.ausleiherino24.service.ImageStoreService;
import de.propra2.ausleiherino24.service.UserService;
import org.assertj.core.api.Assertions;
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

import java.util.ArrayList;
import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles(profiles = "test")
public class CaseControllerTest {
	
	@Autowired private MockMvc mvc;
	
	@MockBean private ArticleRepository articles;
	@MockBean private UserRepository users;
	@MockBean private PersonRepository persons;
	@MockBean private CaseRepository cases;

	@MockBean private ImageStoreService imageStoreService;
	@MockBean UserService us;
	@MockBean private ArticleService as;
	@MockBean private CustomUserDetailsService userDetailsService;

	@Ignore
	@Test
	public void displayArticleStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/article")).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	@Ignore
	@Test
	public void displayArticleStatusTest2() throws Exception {
		Mockito.when(articles.findById(1L)).thenReturn(Optional.of(new Article()));
		mvc.perform(MockMvcRequestBuilders.get("/article?id=1")).andExpect(MockMvcResultMatchers.status().isOk());
	}
	@Ignore
	@Test
	public void displayArticleViewTest() throws Exception {
		Mockito.when(articles.findById(1L)).thenReturn(Optional.of(new Article()));
		mvc.perform(MockMvcRequestBuilders.get("/article?id=1"))
				.andExpect(MockMvcResultMatchers.view().name("article"));
	}
	@Ignore
	@Test
	public void createNewCaseAndArticleStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/newArticle")).andExpect(MockMvcResultMatchers.status().isOk());
	}
	@Ignore
	@Test
	public void createNewCaseAndArticleViewTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/newArticle")).andExpect(MockMvcResultMatchers.view().name("article"));
	}
	@Ignore
	@Test
	public void createNewCaseAndArticleModelTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/newArticle"))
				.andExpect(MockMvcResultMatchers.model().attribute("article", Matchers.isA(Article.class)));
	}
	@Ignore
	@Test
	public void saveNewCaseAndArticleStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/saveNewArticle")).andExpect(MockMvcResultMatchers.status().isOk());
	}
	@Ignore
	@Test
	public void saveNewCaseAndArticleViewTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/saveNewArticle")).andExpect(MockMvcResultMatchers.view().name("case"));
	}
	@Ignore
	@Test
	public void saveNewCaseAndArticleModelTest() throws Exception {
		Article article = new Article();
		article.setId(1L);
		mvc.perform(MockMvcRequestBuilders.post("/saveNewArticle").flashAttr("article", article))
				.andExpect(MockMvcResultMatchers.model().attribute("case", Matchers.isA(Case.class)))
				.andExpect(MockMvcResultMatchers.model().attribute("case", Matchers.hasProperty("article", Matchers.equalTo(article))));
		Mockito.verify(articles, Mockito.times(1)).save(ArgumentMatchers.refEq(article));
	}
	@Ignore
	@Test
	public void saveEditedCaseAndArticleStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.put("/saveEditedArticle")).andExpect(MockMvcResultMatchers.status().isOk());
	}
	@Ignore
	@Test
	public void saveEditedCaseAndArticleViewTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.put("/saveEditedArticle")).andExpect(MockMvcResultMatchers.view().name("article"));
	}
	@Ignore
	@Test
	public void saveEditedCaseAndArticleModelTest() throws Exception {
		Article article = new Article();
		article.setId(1L);
		mvc.perform(MockMvcRequestBuilders.put("/saveEditedArticle").flashAttr("article", article))
				.andExpect(MockMvcResultMatchers.model().attribute("article", Matchers.equalTo(article)));
		Mockito.verify(articles, Mockito.times(1)).save(ArgumentMatchers.refEq(article));
	}
	@Ignore
	@Test
	public void deactivateArticleStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.put("/deactivateArticle")).andExpect(MockMvcResultMatchers.status().isOk());
	}
	@Ignore
	@Test
	public void deactivateArticleViewTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.put("/deactivateArticle")).andExpect(MockMvcResultMatchers.view().name("index"));
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
		
		Mockito.when(cases.findAll()).thenReturn(cas);
		mvc.perform(MockMvcRequestBuilders.put("/deactivateArticle").flashAttr("article", article));
		Mockito.verify(articles, Mockito.times(1)).save(ArgumentMatchers.refEq(article));
		Mockito.verify(cases, Mockito.times(1)).save(ArgumentMatchers.refEq(c1));
		Assertions.assertThat(c1.active).isFalse();
		Assertions.assertThat(article.getActive()).isFalse();
	}
	
	// TODO: create new article, when logged in
	
	// TODO: edit own articles
	
	// TODO: delete own articles, if not rented, but allow delete when reserved
	
	// TODO: reserve articles, when logged in, that are not your own
	
	// TODO: rent articles, when logged in, that are not your own
	
}
