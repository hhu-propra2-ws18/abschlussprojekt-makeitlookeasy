package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArticleServiceTest {
	private ArticleService articleService;
	private ArticleRepository articleRepositoryMock;
	private ArrayList<Article> articles;

	@Before
	public void setUp(){
		articleRepositoryMock = mock(ArticleRepository.class);
		articleService = new ArticleService(articleRepositoryMock);
		articles = new ArrayList<>();
	}

	@Test
	public void threeActiveArticles(){
		articles.add(new Article(0L, "", "", true, false, null,""));
		articles.add(new Article(1L, "", "", true, false, null,""));
		articles.add(new Article(2L, "", "", true, false, null,""));

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		assertEquals(articles, articleService.getAllNonReservedArticles());
	}

	@Test
	public void OneActiveOneReservedArticle(){
		articles.add(new Article(0L, "", "", true, false, null,""));
		articles.add(new Article(1L, "", "", true, true, null,""));

		when(articleRepositoryMock.findAll()).thenReturn(articles);
		articles.remove(1);

		assertEquals(articles, articleService.getAllNonReservedArticles());
	}

	@Test
	public void threeInactiveArticles(){
		articles.add(new Article(0L, "", "", false, false, null,""));
		articles.add(new Article(1L, "", "", false, false, null,""));
		articles.add(new Article(2L, "", "", false, false, null,""));

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		assertTrue(articleService.getAllNonReservedArticles().isEmpty());
	}

	@Test
	public void doubleArticle(){
		articles.add(new Article(0L, "", "", true, false, null,""));
		articles.add(new Article(0L, "", "", true, false, null,""));
		articles.add(new Article(0L, "", "", true, false, null,""));

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		articles.remove(1);
		articles.remove(1);

		assertEquals(articles, articleService.getAllNonReservedArticles());
	}

	@Test
	public void threeToys(){
		articles.add(new Article(0L, true, false, Category.TOYS));
		articles.add(new Article(1L, true, false, Category.TOYS));
		articles.add(new Article(2L, true, false, Category.TOYS));

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		assertEquals(articles, articleService.getAllNonReservedArticlesByCategory(Category.TOYS));
	}

	@Test
	public void threeToys2(){
		articles.add(new Article(0L, true, false, Category.TOYS));
		articles.add(new Article(1L, true, false, Category.TOYS));
		articles.add(new Article(2L, true, false, Category.TOYS));

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		assertTrue(articleService.getAllNonReservedArticlesByCategory(Category.TOOLS).isEmpty());
	}

	@Test
	public void threeToyskOneReserved(){
		articles.add(new Article(0L, true, false, Category.TOYS));
		articles.add(new Article(1L, true, false, Category.TOYS));
		articles.add(new Article(2L, true, true, Category.TOYS));

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		articles.remove(2);

		assertEquals(articles, articleService.getAllNonReservedArticlesByCategory(Category.TOYS));
	}

	@Test
	public void twoToysTwoTools(){
		articles.add(new Article(0L, true, false, Category.TOYS));
		articles.add(new Article(1L, true, false, Category.TOOLS));
		articles.add(new Article(2L, true, false, Category.TOYS));
		articles.add(new Article(3L, true, false, Category.TOOLS));

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		articles.remove(3);
		articles.remove(1);

		assertEquals(articles, articleService.getAllNonReservedArticlesByCategory(Category.TOYS));
	}

	@Test
	public void twoToysTwoTools2(){
		articles.add(new Article(0L, true, false, Category.TOYS));
		articles.add(new Article(1L, true, false, Category.TOOLS));
		articles.add(new Article(2L, true, false, Category.TOYS));
		articles.add(new Article(3L, true, false, Category.TOOLS));

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		articles.remove(2);
		articles.remove(0);

		assertEquals(articles, articleService.getAllNonReservedArticlesByCategory(Category.TOOLS));
	}

	@Test
	public void deactivateArticle() throws Exception {
		Optional<Article> op = Optional.of(new Article(0L, true, false, null));
		when(articleRepositoryMock.findById(0L)).thenReturn(op);

		ArgumentCaptor<Article> argument = ArgumentCaptor.forClass(Article.class);

		assertTrue(articleService.deactivateArticle(0L));
		verify(articleRepositoryMock).save(argument.capture());
		assertFalse(argument.getValue().getActive());
	}

	@Test
	public void deactiveReservedArticle() throws Exception {
		Optional<Article> op = Optional.of(new Article(0L, true, true, null));
		when(articleRepositoryMock.findById(0L)).thenReturn(op);

		assertFalse(articleService.deactivateArticle(0L));
	}
}
