package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.User;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

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
		article1.setDescription("used condition");
		article1.setName("Mountain-Bike");
		article1.setActive(true);

		article2 = new Article();
		article2.setOwner(new User());
		article2.setName("Chainsaw");
		article2.setDescription("bloody");

		articles.saveAll(Arrays.asList(article1, article2));
	}

	@Test
	public void databaseShouldSaveEntities() {
		List<Article> us = articles.findAll();
		Assertions.assertThat(us.size()).isEqualTo(2);
		Assertions.assertThat(us.get(0)).isEqualTo(article1);
		Assertions.assertThat(us.get(1)).isEqualTo(article2);
	}

	@Test
	public void databaseShouldRemoveCorrectEntity() {
		articles.delete(article1);

		List<Article> us = articles.findAll();
		Assertions.assertThat(us.size()).isOne();
		Assertions.assertThat(us.get(0)).isEqualTo(article2);
	}

	@Test
	public void databaseShouldReturnCountOfTwoIfDatabaseHasTwoEntries() {
		Assertions.assertThat(articles.count()).isEqualTo(2);
	}
	
	@Test
	public void customQueryFindAllActiveByUserShouldReturnActiveArticleWithCorrespondingOwner() {
		List<Article> us = articles.findAllActiveByUser(article1.getOwner());
		Assertions.assertThat(us.size()).isEqualTo(1);
		Assertions.assertThat(us.get(0)).isEqualTo(article1);
	}
}
