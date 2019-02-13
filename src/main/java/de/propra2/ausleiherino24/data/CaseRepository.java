package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface CaseRepository extends CrudRepository<Case, Long> {
	ArrayList<Case> findAll();

	@Query("SELECT c FROM Case c WHERE c.article.owner.id = :user.id")
	ArrayList<Case> findByOwner(@Param("user") User user);

	@Query("SELECT c FROM Case c WHERE c.receiver.id = :user.id")
	ArrayList<Case> findByReceiver(@Param("user") User user);
}
