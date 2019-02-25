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

    /**
     * Returns a list of all articles, that are currently being offered (active == true) by the
     * user, regardless of reservation status.
     *
     * @param user User object, whose articles are being returned.
     * @return ArrayList of Article objects
     */
    @Query("SELECT a FROM Article a WHERE a.owner = :user and a.active = true")
    ArrayList<Article> findAllActiveByUser(@Param("user") User user);

    @Query("SELECT a FROM Article a WHERE a.active = true")
    ArrayList<Article> findAllActive();

    List<Article> findByNameContainsIgnoreCase(String searchString);
}
