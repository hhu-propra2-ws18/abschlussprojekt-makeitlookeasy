package de.propra2.ausleiherino24.db;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.model.Article;
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
	public void init(){
		article1 = new Article();
		article1.setDescription("used condition");
		article1.setName("Mountain-Bike");

		article2 = new Article();
		article2.setName("Chainsaw");
		article2.setDescription("bloody");
	}

	@Test
	public void databaseShouldSaveEntities(){
		articles.saveAll(Arrays.asList(article1, article2));

		List<Article> us = articles.findAll();
		Assertions.assertThat(us.size()).isEqualTo(2);
		Assertions.assertThat(us.get(0)).isEqualTo(article1);
		Assertions.assertThat(us.get(1)).isEqualTo(article2);
	}

	@Test
	public void databaseShouldRemoveCorrectEntity(){
		articles.saveAll(Arrays.asList(article1, article2));

		articles.delete(article1);

		List<Article> us = articles.findAll();
		Assertions.assertThat(us.size()).isOne();
		Assertions.assertThat(us.get(0)).isEqualTo(article2);
	}

	@Test
	public void databaseShouldReturnCountOfTwoIfDatabaseHasTwoEntries(){
		articles.saveAll(Arrays.asList(article1, article2));

		List<Article> us = articles.findAll();
		Assertions.assertThat(articles.count()).isEqualTo(2);
	}


}
