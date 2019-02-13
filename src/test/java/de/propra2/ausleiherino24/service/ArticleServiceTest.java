package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.model.Article;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
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
		articles.add(new Article(0L, "", "", true, false, null));
		articles.add(new Article(1L, "", "", true, false, null));
		articles.add(new Article(2L, "", "", true, false, null));

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		assertEquals(articles, articleService.getAllNonReservedArticles());
	}

	@Test
	public void OneActiveOneReservedArticle(){
		articles.add(new Article(0L, "", "", true, false, null));
		articles.add(new Article(1L, "", "", true, true, null));

		when(articleRepositoryMock.findAll()).thenReturn(articles);
		articles.remove(1);

		assertEquals(articles, articleService.getAllNonReservedArticles());
	}

	@Test
	public void threeInactiveArticles(){
		articles.add(new Article(0L, "", "", false, false, null));
		articles.add(new Article(1L, "", "", false, false, null));
		articles.add(new Article(2L, "", "", false, false, null));

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		assertTrue(articleService.getAllNonReservedArticles().isEmpty());
	}

	@Test
	public void doubleArticle(){
		articles.add(new Article(0L, "", "", true, false, null));
		articles.add(new Article(0L, "", "", true, false, null));
		articles.add(new Article(0L, "", "", true, false, null));

		when(articleRepositoryMock.findAll()).thenReturn(articles);

		articles.remove(1);
		articles.remove(1);

		assertEquals(articles, articleService.getAllNonReservedArticles());
	}
}
