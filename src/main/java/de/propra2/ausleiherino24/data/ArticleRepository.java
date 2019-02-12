package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Article;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ArticleRepository extends CrudRepository<Article, Long> {
	ArrayList<Article> findAll();
	
	Article getById(Long id);
}
