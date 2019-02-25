package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.CustomerReview;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public interface CustomerReviewRepository extends CrudRepository<CustomerReview, Long> {

    @NonNull
    List<CustomerReview> findAll();
}
