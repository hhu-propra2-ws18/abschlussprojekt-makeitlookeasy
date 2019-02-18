package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CustomerReviewRepository;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.CustomerReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerReviewService {

	public final CustomerReviewRepository customerReviewRepository;

	@Autowired
	public CustomerReviewService(CustomerReviewRepository customerReviewRepository){
		this.customerReviewRepository = customerReviewRepository;
	}

	public void addCustomerReview(CustomerReview customerReview){
		customerReviewRepository.save(customerReview);
	}



}
