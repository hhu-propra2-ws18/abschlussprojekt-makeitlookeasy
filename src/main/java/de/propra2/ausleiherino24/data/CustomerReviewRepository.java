package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.features.reviews.CustomerReview;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerReviewRepository extends CrudRepository<CustomerReview, Long> {

    @NonNull
    List<CustomerReview> findAll();
}
