package de.propra2.ausleiherino24.service;

import static org.mockito.Mockito.mock;

import de.propra2.ausleiherino24.data.CustomerReviewRepository;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.CustomerReview;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class CustomerReviewServiceTest {

    private CustomerReviewRepository customerReviewRepository;

    private CustomerReviewService customerReviewService;
    private CaseService caseService;
    private List<Case> cases;
    private List<CustomerReview> customerReviews;

    @BeforeEach
    void init() {
        customerReviewRepository = mock(CustomerReviewRepository.class);
        caseService = mock(CaseService.class);
        customerReviewService = new CustomerReviewService(customerReviewRepository, caseService);

        cases = new ArrayList<>();
        final Case case1 = new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null, null);
        final Case case2 = new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null, null);
        final Case case3 = new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null, null);

        customerReviews = new ArrayList<>();
        final CustomerReview customerReview1 = new CustomerReview();
        final CustomerReview customerReview2 = new CustomerReview();
        final CustomerReview customerReview3 = new CustomerReview();

        customerReview1.setAcase(case1);
        customerReview2.setAcase(case2);
        customerReview3.setAcase(case3);

        customerReviews.add(customerReview1);
        customerReviews.add(customerReview2);
        customerReviews.add(customerReview3);

        cases.add(case1);
        cases.add(case2);
        cases.add(case3);
    }

    @Test
    void findAllReviewsByLenderIdFindsZeroReviews() {
        Mockito.when(customerReviewRepository.findAll()).thenReturn(new ArrayList<>());

        final List<CustomerReview> customerReviews = customerReviewService
                .findAllReviewsByLenderId(1L);
        Assertions.assertThat(customerReviews.size()).isEqualTo(0);
    }

    @Test
    void saveCustomerReviewShouldSaveCustomerReview() {
        customerReviewService.saveReview(customerReviews.get(0));
        Mockito.verify(customerReviewRepository, Mockito.times(1)).save(customerReviews.get(0));
    }


}
