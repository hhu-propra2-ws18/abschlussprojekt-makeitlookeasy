package de.propra2.ausleiherino24.service;

import static org.mockito.Mockito.mock;

import de.propra2.ausleiherino24.data.CustomerReviewRepository;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.CustomerReview;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

public class CustomerReviewServiceTest {

	private CustomerReviewRepository customerReviewRepository;

	private CustomerReviewService customerReviewService;
	private CaseService caseService;
	private List<Case> cases;
	private List<CustomerReview> customerReviews;

	@Before
	public void init(){
		customerReviewRepository = mock(CustomerReviewRepository.class);
		caseService = mock(CaseService.class);
		customerReviewService = new CustomerReviewService(customerReviewRepository,caseService);

		cases = new ArrayList<>();
		Case case1 = new Case(0L, null, null, 0, 0, null,null,null, null, false);
		Case case2 = new Case(0L, null, null, 0, 0, null,null, null,null, false);
		Case case3 = new Case(0L, null, null, 0, 0, null,null, null, null,false);

		customerReviews = new ArrayList<>();
		CustomerReview customerReview1 = new CustomerReview();
		CustomerReview customerReview2 = new CustomerReview();
		CustomerReview customerReview3 = new CustomerReview();

		customerReview1.setACase(case1);
		customerReview2.setACase(case2);
		customerReview3.setACase(case3);

		customerReviews.add(customerReview1);
		customerReviews.add(customerReview2);
		customerReviews.add(customerReview3);

		cases.add(case1);
		cases.add(case2);
		cases.add(case3);
	}

	@Test
	public void findAllReviewsByLenderIdFindsAllReviews(){

		Mockito.when(customerReviewRepository.findAll()).thenReturn(customerReviews);
		Mockito.when(caseService.getAllCasesFromPersonOwner(1L)).thenReturn(cases);

		List<CustomerReview> crvws = customerReviewService.findAllReviewsByLenderId(1L);
		Assertions.assertThat(crvws.size()).isEqualTo(3);
		Assertions.assertThat(crvws.get(0)).isEqualTo(customerReviews.get(0));
	}

	@Test
	public void saveCustomerReviewShouldSaveCustomerReview(){

		customerReviewService.addCustomerReview(customerReviews.get(0));
		Mockito.verify(customerReviewRepository,Mockito.times(1)).save(customerReviews.get(0));
	}



}
