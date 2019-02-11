package de.propra2.ausleiherino24.Respositories;

import de.propra2.ausleiherino24.model.Article;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ArticleRespository extends CrudRepository<Article, Long> {
	ArrayList<Article> findAll();
}
