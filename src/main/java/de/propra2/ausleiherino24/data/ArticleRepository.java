package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface ArticleRepository extends CrudRepository<Article, Long> {

    @NonNull
    ArrayList<Article> findAll();

    @Query("SELECT a FROM Article a WHERE a.owner = :user and a.active = true")
    ArrayList<Article> findAllActiveByUser(@Param("user") User user);

    @Query("SELECT a FROM Article a WHERE a.active = true")
    ArrayList<Article> findAllActive();

    List<Article> findByNameContainsIgnoreCase(String searchString);
}
