package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ArticleServiceTest {

	private ArticleService articleService;
	private ArticleRepository articleRepositoryMock;
	private ArrayList<Article> articles;

	private Article article01;
	private Article article02;
	private Article article03;
	private Article article04;

	@Before
	public void setUp() {
		articleRepositoryMock = mock(ArticleRepository.class);
		articleService = new ArticleService(articleRepositoryMock);

		articles = new ArrayList<>();
		article01 = new Article(0L, "", "", "", false, 0,
				0, "", null, true, Category.TOYS, null);
		article02 = new Article(1L, "", "", "", false, 0,
				0, "", null, true, Category.TOYS, null);
		article03 = new Article(2L, "", "", "", false, 0,
				0, "", null, true, Category.TOYS, null);
		article04 = new Article(3L, "", "", "", false, 0,
				0, "", null, true, Category.TOYS, null);
	}

	@Test
	public void threeActiveArticles() {
		articles.add(article01);
		articles.add(article02);
		articles.add(article03);

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		assertEquals(articles, articleService.getAllNonReservedArticles());
	}

	@Test
	public void OneActiveOneReservedArticle() {
		article02.setReserved(true);

		articles.add(article01);
		articles.add(article02);

		when(articleRepositoryMock.findAll()).thenReturn(articles);
		articles.remove(1);

		assertEquals(articles, articleService.getAllNonReservedArticles());
	}

	@Test
	public void threeInactiveArticles() {
		article01.setActive(false);
		article02.setActive(false);
		article03.setActive(false);

		articles.add(article01);
		articles.add(article02);
		articles.add(article03);

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		assertTrue(articleService.getAllNonReservedArticles().isEmpty());
	}

	@Test
	public void tripleArticle() {
		articles.add(article01);
		articles.add(article01);
		articles.add(article01);

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		articles.remove(1);
		articles.remove(1);

		assertEquals(articles, articleService.getAllNonReservedArticles());
	}

	@Test
	public void threeToys() {
		articles.add(article01);
		articles.add(article02);
		articles.add(article03);

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		assertEquals(articles, articleService.getAllArticlesByCategory(Category.TOYS));
	}

	@Test
	public void threeToys2() {
		articles.add(article01);
		articles.add(article02);
		articles.add(article03);

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		assertTrue(articleService.getAllArticlesByCategory(Category.TOOLS).isEmpty());
	}

	@Test
	public void threeToysOneReserved() {
		article03.setReserved(true);

		articles.add(article01);
		articles.add(article02);
		articles.add(article03);

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		articles.remove(2);

		assertEquals(articles, articleService.getAllArticlesByCategory(Category.TOYS));
	}

	@Test
	public void twoToysTwoTools() {
		article02.setCategory(Category.TOOLS);
		article04.setCategory(Category.TOOLS);

		articles.add(article01);
		articles.add(article02);
		articles.add(article03);
		articles.add(article04);

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		articles.remove(3);
		articles.remove(1);

		assertEquals(articles, articleService.getAllArticlesByCategory(Category.TOYS));
	}

	@Test
	public void twoToysTwoTools2() {
		article02.setCategory(Category.TOOLS);
		article04.setCategory(Category.TOOLS);

		articles.add(article01);
		articles.add(article02);
		articles.add(article03);
		articles.add(article04);

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		articles.remove(2);
		articles.remove(0);

		assertEquals(articles, articleService.getAllArticlesByCategory(Category.TOOLS));
	}

	@Test
	public void deactivateArticle() throws Exception {
		Optional<Article> op = Optional.of(article01);
		when(articleRepositoryMock.findById(0L)).thenReturn(op);

		ArgumentCaptor<Article> argument = ArgumentCaptor.forClass(Article.class);

		assertTrue(articleService.deactivateArticle(0L));
		verify(articleRepositoryMock).save(argument.capture());
		assertFalse(argument.getValue().getActive());
	}

	@Test
	public void deactivateReservedArticle() throws Exception {
		article01.setReserved(true);

		Optional<Article> op = Optional.of(article01);
		when(articleRepositoryMock.findById(0L)).thenReturn(op);

		assertFalse(articleService.deactivateArticle(0L));
	}
}
