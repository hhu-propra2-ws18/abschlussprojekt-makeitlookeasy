package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.CustomerReview;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
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

	private Case case1;
	private Case case2;
	private List<CustomerReview> customerReviewList;
	@Before
	public void init(){
		CustomerReview customerReview1 = new CustomerReview();
		CustomerReview customerReview2 = new CustomerReview();
		customerReviewList = new ArrayList<>();
		customerReview1.setDescription("test1");
		customerReview1.setTimestamp(10012019L);
		customerReview1.setStars(3);
		customerReview1.setACase(new Case());
		customerReview2.setDescription("test2");
		customerReview2.setTimestamp(11012019L);
		customerReview2.setStars(5);
		customerReview2.setACase(new Case());
		customerReviewList.add(customerReview1);
		customerReviewList.add(customerReview2);
	}

	@Test
	public void databaseShouldSaveEntities() {
		customerReviews.saveAll(customerReviewList);

		List<CustomerReview> crvws = customerReviews.findAll();
		Assertions.assertThat(crvws.size()).isEqualTo(2);
		Assert.assertTrue(crvws.contains(customerReviewList.get(0)));
		Assert.assertTrue(crvws.contains(customerReviewList.get(1)));
	}


}
