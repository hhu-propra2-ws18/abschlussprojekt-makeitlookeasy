package de.propra2.ausleiherino24.data;

import static org.assertj.core.api.Assertions.assertThat;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.User;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
public class ArticleRepoTest {

    @Autowired
    private ArticleRepository articles;

    private Article article1;
    private Article article2;

    @Before
    public void init() {
        article1 = new Article();
        article1.setOwner(new User());
        article1.setName("Mountain-Bike");
        article1.setDescription("Looks like shit");
        article1.setDeposit(500D);
        article1.setCostPerDay(24D);
        article1.setActive(true);

        article2 = new Article();
        article2.setOwner(new User());
        article2.setName("Chainsaw");
        article2.setDescription("bloody");
        article2.setDeposit(1D);
        article2.setCostPerDay(99D);
        article2.setActive(false);

        articles.saveAll(Arrays.asList(article1, article2));
    }

    @Test
    public void databaseShouldSaveEntities() {
        final List<Article> us = articles.findAll();
        assertThat(us.size()).isEqualTo(2);
        assertThat(us.get(0)).isEqualTo(article1);
        assertThat(us.get(1)).isEqualTo(article2);
    }

    @Test
    public void databaseShouldRemoveCorrectEntity() {
        articles.delete(article1);

        final List<Article> us = articles.findAll();
        assertThat(us.size()).isOne();
        assertThat(us.get(0)).isEqualTo(article2);
    }

    @Test
    public void databaseShouldReturnCountOfTwoIfDatabaseHasTwoEntries() {
        assertThat(articles.count()).isEqualTo(2);
    }

    @Test
    public void customQueryFindAllActiveByUserShouldReturnActiveArticleWithCorrespondingOwner() {
        final List<Article> us = articles.findAllActiveByUser(article1.getOwner());
        assertThat(us.size()).isEqualTo(1);
        assertThat(us.get(0)).isEqualTo(article1);
    }

    @Test
    public void customQueryFindAllActiveByUserShouldReturnEmptyList() {
        final List<Article> us = articles.findAllActiveByUser(article2.getOwner());
        assertThat(us.isEmpty());

    }

    @Test
    public void customQueryFindAllActiveByUserShouldReturnAllActiveArticleWithCorrespondingOwner() {
        final Article article3 = new Article();
        article3.setOwner(article1.getOwner());
        article3.setActive(true);

        final List<Article> us = articles.findAllActiveByUser(article1.getOwner());
        assertThat(us.size()).isEqualTo(2);
        assertThat(us.get(0)).isEqualTo(article1);
        assertThat(us.get(1)).isEqualTo(article3);
    }

    @Test
    public void customQueryFindAllActiveByUserShouldReturnNoArticle() {
        final List<Article> us = articles.findAllActiveByUser(article2.getOwner());
        assertThat(us.size()).isEqualTo(0);
    }

    @Test
    public void customQueryFindAllActiveShouldReturnFirstArticle() {
        final List<Article> us = articles.findAllActive();
        assertThat(us.size()).isEqualTo(1);
        assertThat(us.get(0)).isEqualTo(article1);
    }

    @Test
    public void queryShouldFindArticleContainingString() {
        final List<Article> us = articles.findByNameContainsIgnoreCase("saw");
        assertThat(us.size()).isEqualTo(1);
        assertThat(us.get(0)).isEqualTo(article2);
    }

    @Test
    public void queryShouldFindArticleContainingStringIgnoringCase() {
        final List<Article> us = articles.findByNameContainsIgnoreCase("mOUNTAIN");
        assertThat(us.size()).isEqualTo(1);
        assertThat(us.get(0)).isEqualTo(article1);
    }
}
