package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Case;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;

public interface CaseRepository extends CrudRepository<Case, Long> {
	ArrayList<Case> findAll();

	@Query("SELECT c FROM Case c WHERE c.article.owner.id = :id")
	ArrayList<Case> findByOwner(@Param("id") Long id);

	@Query("SELECT c FROM Case c WHERE c.receiver.id = :id")
	ArrayList<Case> findByReceiver(@Param("id") Long id);
}
