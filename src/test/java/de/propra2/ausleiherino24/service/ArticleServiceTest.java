package de.propra2.ausleiherino24.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Category;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

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
        article01 = new Article(0L, "", "", "", 0, 0,
                "", true, true, null, Category.TOYS, null);
        article02 = new Article(1L, "", "", "", 0, 0,
                "", true, true, null, Category.TOYS, null);
        article03 = new Article(2L, "", "", "", 0, 0,
                "", true, true, null, Category.TOYS, null);
        article04 = new Article(3L, "", "", "", 0, 0,
                "", true, true, null, Category.TOYS, null);
    }

    @Test
    public void saveNewArticle() {
        articleService.saveArticle(new Article(), "");

        verify(articleRepositoryMock).save(new Article());
    }

    @Test
    public void tripleArticle() {
        article02.setActive(false);
        article03.setActive(false);

        articles.add(article01);
        articles.add(article01);
        articles.add(article01);

        when(articleRepositoryMock.findAllActive()).thenReturn(articles);

        articles.remove(1);
        articles.remove(1);

        assertEquals(articles, articleService.getAllActiveArticles());
    }

    @Test
    public void threeToys() {
        articles.add(article01);
        articles.add(article02);
        articles.add(article03);

        when(articleRepositoryMock.findAllActive()).thenReturn(articles);

        assertEquals(articles, articleService.getAllArticlesByCategory(Category.TOYS));
    }

    @Test
    public void threeToys2() {
        articles.add(article01);
        articles.add(article02);
        articles.add(article03);

        when(articleRepositoryMock.findAllActive()).thenReturn(articles);

        assertTrue(articleService.getAllArticlesByCategory(Category.TOOLS).isEmpty());
    }

    @Test
    public void twoToysTwoTools() {
        article02.setCategory(Category.TOOLS);
        article04.setCategory(Category.TOOLS);

        articles.add(article01);
        articles.add(article02);
        articles.add(article03);
        articles.add(article04);

        when(articleRepositoryMock.findAllActive()).thenReturn(articles);

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

        when(articleRepositoryMock.findAllActive()).thenReturn(articles);

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
        assertFalse(argument.getValue().isActive());
    }

    @Test
    public void deactivateLendArticle() throws Exception {
        Case c = new Case();
        c.setRequestStatus(7);  //requestStatus = RUNNING
        article01.setCases(Arrays.asList(c));
        Optional<Article> op = Optional.of(article01);
        when(articleRepositoryMock.findById(0L)).thenReturn(op);

        assertFalse(articleService.deactivateArticle(0L));
        verify(articleRepositoryMock, times(0)).save(any());
    }

    @Test
    public void deactivateArticleWithConflict() throws Exception {
        Case c = new Case();
        c.setRequestStatus(10);  //requestStatus = OPEN_CONFLICT
        article01.setCases(Arrays.asList(c));
        Optional<Article> op = Optional.of(article01);
        when(articleRepositoryMock.findById(0L)).thenReturn(op);

        assertFalse(articleService.deactivateArticle(0L));
        verify(articleRepositoryMock, times(0)).save(any());
    }

    @Test
    public void deactivateFinishedArticle() throws Exception {
        Case c = new Case();
        c.setRequestStatus(14);  //requestStatus = FINISHED
        article01.setCases(Arrays.asList(c));
        Optional<Article> op = Optional.of(article01);
        when(articleRepositoryMock.findById(0L)).thenReturn(op);

        ArgumentCaptor<Article> argument = ArgumentCaptor.forClass(Article.class);

        assertTrue(articleService.deactivateArticle(0L));
        verify(articleRepositoryMock).save(argument.capture());
        assertFalse(argument.getValue().isActive());
    }

    @Test(expected = Exception.class)
    public void deactivateNotExistingArticle() throws Exception {
        when(articleRepositoryMock.findById(0L)).thenReturn(Optional.empty());

        articleService.deactivateArticle(0L);
    }
}
