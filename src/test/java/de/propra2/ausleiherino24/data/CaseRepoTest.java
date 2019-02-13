package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
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

public class CaseRepoTest {
	@Autowired
	private CaseRepository cases;

	private Case case1;
	private Case case2;

	@Before
	public void init(){
		case1 = new Case();
		//case1.setOwner(new User());
		case1.setReceiver(new User());
		case1.setArticle(new Article());
		case1.setPrice(80);
		case1.setDeposit(200);
		case1.setTitle("Car");
		case1.setStartTime(12022019L);
		case1.setEndTime(19022019L);

		case2 = new Case();
		//case2.setOwner(new User());
		case2.setReceiver(new User());
		case2.setArticle(new Article());
		case2.setPrice(60);
		case2.setDeposit(150);
		case2.setTitle("Monitor");
		case2.setStartTime(10022019L);
		case2.setEndTime(15022019L);
	}

	@Test
	public void databaseShouldSaveEntities(){
		cases.saveAll(Arrays.asList(case1, case2));

		List<Case> us = cases.findAll();
		Assertions.assertThat(us.size()).isEqualTo(2);
		Assertions.assertThat(us.get(0)).isEqualTo(case1);
		Assertions.assertThat(us.get(1)).isEqualTo(case2);
	}

	@Test
	public void databaseShouldRemoveCorrectEntity(){
		cases.saveAll(Arrays.asList(case1, case2));

		cases.delete(case2);

		List<Case> us = cases.findAll();
		Assertions.assertThat(us.size()).isOne();
		Assertions.assertThat(us.get(0)).isEqualTo(case1);
	}

	@Test
	public void databaseShouldReturnCountOfTwoIfDatabaseHasTwoEntries(){
		cases.saveAll(Arrays.asList(case1, case2));

		List<Case> us = cases.findAll();
		Assertions.assertThat(cases.count()).isEqualTo(2);
	}

}
