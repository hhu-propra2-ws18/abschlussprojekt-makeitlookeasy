package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CustomerReviewRepository;
import de.propra2.ausleiherino24.model.CustomerReview;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerReviewService {

	private final CustomerReviewRepository customerReviewRepository;
	private CaseService caseService;

	@Autowired
	public CustomerReviewService(CustomerReviewRepository customerReviewRepository,CaseService caseService){
		this.customerReviewRepository = customerReviewRepository;
		this.caseService = caseService;
	}

	public void addCustomerReview(CustomerReview customerReview){
		customerReviewRepository.save(customerReview);
	}

	public List<CustomerReview> findAllReviewsByLenderId(Long id){
		List<CustomerReview> userReviews = new ArrayList<>();
		List<CustomerReview> reviews = customerReviewRepository.findAll();
		for(CustomerReview c : reviews){
			if(caseService.getAllCasesFromPersonOwner(id).contains(c.getACase())){
				userReviews.add(c);
			}
		}
		return userReviews;
	}



}
