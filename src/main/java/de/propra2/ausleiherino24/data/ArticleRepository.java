package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ArticleRepository extends CrudRepository<Article, Long> {
	ArrayList<Article> findAll();
	
	@Query("SELECT a FROM Article a WHERE a.owner = :user and a.active = true")		// TODO: Check, if query works.
	ArrayList<Article> findAllActiveByUser(User user);
}
