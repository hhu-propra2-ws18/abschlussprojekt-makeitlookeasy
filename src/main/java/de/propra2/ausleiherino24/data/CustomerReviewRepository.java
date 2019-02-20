package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.CustomerReview;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerReviewRepository extends CrudRepository<CustomerReview, Long> {
	@Override
	List<CustomerReview> findAll();
}
