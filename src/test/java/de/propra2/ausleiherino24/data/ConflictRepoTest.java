package de.propra2.ausleiherino24.data;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")
public class ConflictRepoTest {

	@Autowired
	private ConflictRepository conflicts;
	@Autowired
	private CaseRepository cases;

	private User user1;
	private User user2;

	private Case case1;
	private Case case2;

	private Conflict c1;
	private Conflict c2;

	@Before
	public void init() {
		user2 = new User();
		user2.setUsername("user2");
		user1 = new User();
		user1.setUsername("user1");

		Article case1Art = new Article();
		case1Art.setOwner(user2);
		case1 = new Case();
		case1.setReceiver(user1);
		case1.setArticle(case1Art);
		case1.setPrice(80);
		case1.setDeposit(200);
		case1.setStartTime(12022019L);
		case1.setEndTime(19022019L);


		Article case2Art = new Article();
		case2Art.setOwner(user1);
		case2 = new Case();
		case2.setReceiver(user2);
		case2.setArticle(case2Art);
		case2.setPrice(60);
		case2.setDeposit(150);
		case2.setStartTime(10022019L);
		case2.setEndTime(15022019L);

		c1 = new Conflict();
		c1.setConflictedCase(case1);
		c1.setConflictReporterUsername(case1.getReceiver().getUsername());
		c1.setConflictDescription("Article not as described");

		c2 = new Conflict();
		c2.setConflictedCase(case2);
		c2.setConflictReporterUsername(case2.getOwner().getUsername());
		c2.setConflictDescription("Article returned damaged");

		conflicts.saveAll(Arrays.asList(c1, c2));
	}

	@Test
	public void customQueryFindAllByReceiverShouldReturnConflictWithCorrespondingCaseReceiver() {
		List<Conflict> expectedConflict = conflicts.findAllByReceiver(user1);
		Assertions.assertThat(expectedConflict.size()).isOne();
		Assertions.assertThat(expectedConflict.get(0)).isEqualTo(c1);
	}

	@Test
	public void customQueryfindAllByArticleOwnerShouldReturnConflictWithCorrespondingCaseArticleOwner() {
		List<Conflict> expectedConflict = conflicts.findAllByArticleOwner(user2);
		Assertions.assertThat(expectedConflict.size()).isOne();
		Assertions.assertThat(expectedConflict.get(0)).isEqualTo(c1);
	}

}
