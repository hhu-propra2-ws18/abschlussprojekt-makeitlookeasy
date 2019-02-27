package de.propra2.ausleiherino24.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Category;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
public class ArticleServiceTest {

    private ArticleService articleService;
    private ArticleRepository articleRepositoryMock;
    private List<Article> articles;

    private Article article01;
    private Article article02;
    private Article article03;
    private Article article04;

    @BeforeEach
    public void setUp() {
        articleRepositoryMock = mock(ArticleRepository.class);
        articleService = new ArticleService(articleRepositoryMock, mock(ImageService.class));

        articles = new ArrayList<>();
        article01 = new Article(0L, "", "", "", 0D, 0D, "",
                false, true, true, null, Category.TOYS, null);
        article02 = new Article(1L, "", "", "", 0D, 0D,
                "", false, true, true, null, Category.TOYS, null);
        article03 = new Article(2L, "", "", "", 0D, 0D,
                "", false, true, true, null, Category.TOYS, null);
        article04 = new Article(3L, "", "", "", 0D, 0D,
                "", false, true, true, null, Category.TOYS, null);
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

        assertEquals(articles, articleService.findAllActiveArticles());
    }

    @Test
    public void twoArticlesForRental() {
        final Case c = new Case();
        c.setRequestStatus(Case.RUNNING);  //requestStatus = RUNNING
        article03.setCases(Arrays.asList(c));

        articles.add(article01);
        articles.add(article02);
        articles.add(article03);

        when(articleRepositoryMock.findAllActive()).thenReturn(articles);

        articles.remove(2);

        assertEquals(articles, articleService.findAllActiveAndForRentalArticles());
    }

    @Test
    public void threeToys() {
        articles.add(article01);
        articles.add(article02);
        articles.add(article03);

        when(articleRepositoryMock.findAllActive()).thenReturn(articles);

        assertEquals(articles, articleService.findAllArticlesByCategory(Category.TOYS));
    }

    @Test
    public void threeToys2() {
        articles.add(article01);
        articles.add(article02);
        articles.add(article03);

        when(articleRepositoryMock.findAllActive()).thenReturn(articles);

        assertTrue(articleService.findAllArticlesByCategory(Category.TOOLS).isEmpty());
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

        assertEquals(articles, articleService.findAllArticlesByCategory(Category.TOYS));
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

        assertEquals(articles, articleService.findAllArticlesByCategory(Category.TOOLS));
    }

    @Test
    public void deactivateArticle() {
        final Optional<Article> op = Optional.of(article01);
        when(articleRepositoryMock.findById(0L)).thenReturn(op);

        final ArgumentCaptor<Article> argument = ArgumentCaptor.forClass(Article.class);

        assertTrue(articleService.deactivateArticle(0L));
        verify(articleRepositoryMock).save(argument.capture());
        assertFalse(argument.getValue().isActive());
    }

    @Test
    public void deactivateLendArticle() {
        final Case c = new Case();
        c.setRequestStatus(Case.RUNNING);  //requestStatus = RUNNING
        article01.setCases(Arrays.asList(c));
        final Optional<Article> op = Optional.of(article01);
        when(articleRepositoryMock.findById(0L)).thenReturn(op);

        assertFalse(articleService.deactivateArticle(0L));
        // verify(articleRepositoryMock, times(0)).save(any());
    }

    @Test
    public void deactivateArticleWithConflict() {
        final Case c = new Case();
        c.setRequestStatus(Case.OPEN_CONFLICT);  //requestStatus = OPEN_CONFLICT
        article01.setCases(Arrays.asList(c));
        final Optional<Article> op = Optional.of(article01);
        when(articleRepositoryMock.findById(0L)).thenReturn(op);

        assertFalse(articleService.deactivateArticle(0L));
        //verify(articleRepositoryMock, times(0)).save(any());
    }

    @Test
    public void deactivateFinishedArticle() {
        final Case c = new Case();
        c.setRequestStatus(Case.FINISHED);  //requestStatus = FINISHED
        article01.setCases(Arrays.asList(c));
        final Optional<Article> op = Optional.of(article01);
        when(articleRepositoryMock.findById(0L)).thenReturn(op);

        final ArgumentCaptor<Article> argument = ArgumentCaptor.forClass(Article.class);

        assertTrue(articleService.deactivateArticle(0L));
        verify(articleRepositoryMock).save(argument.capture());
        assertFalse(argument.getValue().isActive());
    }

    @Test
    public void deactivateNotExistingArticle() {

        assertThrows(NoSuchElementException.class, () -> {
            when(articleRepositoryMock.findById(0L)).thenReturn(Optional.empty());
            articleService.deactivateArticle(0L);
        });

    }

    @Test
    public void updateArticle() {
        final Article article = new Article();
        article.setForRental(true);
        article.setDeposit(0D);
        article.setCostPerDay(0D);
        article.setCategory(Category.TOOLS);
        article.setDescription("");
        article.setName("");
        when(articleRepositoryMock.findById(0L)).thenReturn(Optional.of(article));
        final ArgumentCaptor<Article> argument = ArgumentCaptor.forClass(Article.class);

        articleService.updateArticle(0L, article, new MultipartFile() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getOriginalFilename() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        });

        verify(articleRepositoryMock).save(argument.capture());
        assertEquals(article, argument.getValue());
    }

    @Test
    public void setForSaleToFalse() {
        when(articleRepositoryMock.findById(0L)).thenReturn(Optional.of(new Article()));
        final ArgumentCaptor<Article> argument = ArgumentCaptor.forClass(Article.class);

        articleService.setSellStatusFromArticle(0L, false);

        verify(articleRepositoryMock).save(argument.capture());
        assertFalse(argument.getValue().isForSale());
    }
}
