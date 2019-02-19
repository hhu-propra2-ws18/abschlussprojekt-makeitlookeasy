package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.CustomerReview;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
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

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")

public class CustomerReviewRepoTest {
	@Autowired
	private CustomerReviewRepository customerReviews;

	private CustomerReview customerReview1;
	private CustomerReview customerReview2;
	private Case case1;

	@Before
	public void init() {
		case1 = new Case();
		customerReview1 = new CustomerReview();
		customerReview2 = new CustomerReview();
		customerReview1.setId(1L);
		customerReview1.setDescription("test1");
		customerReview1.setTimestamp(10012019L);
		customerReview1.setStars(3);
		customerReview1.setACase(case1);
		customerReview2.setId(2L);
		customerReview2.setDescription("test2");
		customerReview2.setTimestamp(11012019L);
		customerReview2.setStars(5);
		customerReview2.setACase(case1);
	}

	@Test
	public void databaseShouldSaveEntities() {
		customerReviews.saveAll(Arrays.asList(customerReview1,customerReview2));

		List<CustomerReview> crvws = customerReviews.findAll();
		Assertions.assertThat(crvws.size()).isEqualTo(2);
		Assertions.assertThat(crvws.get(0)).isEqualTo(customerReview1);
		Assertions.assertThat(crvws.get(1)).isEqualTo(customerReview2);
	}


}
