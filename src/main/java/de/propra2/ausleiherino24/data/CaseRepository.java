package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

public interface CaseRepository extends CrudRepository<Case, Long> {
	ArrayList<Case> findAll();

	@Query("SELECT c FROM #{#entityName} c WHERE c.article.owner = :owner")
	ArrayList<Case> findByOwner(@Param("owner") User Owner);

	ArrayList<Case> findByReceiver(User Receiver);

	Optional<Case> findByArticle(Article article);
}
