package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

public interface CaseRepository extends CrudRepository<Case, Long> {

    @NonNull
    List<Case> findAll();

    @Query("SELECT c FROM Case c WHERE c.receiver = :user")
    List<Case> findAllByReceiver(@Param("user") User user);

    @Query("SELECT c FROM #{#entityName} c WHERE c.article.owner = :owner")
    List<Case> findAllByArticleOwner(@Param("owner") User owner);

    @Query("SELECT c FROM #{#entityName} c WHERE c.article.owner.id = :id ORDER BY c.requestStatus ASC")
    List<Case> findAllByArticleOwnerId(@Param("id") Long ownerId);

    Optional<Case> findByArticle(Article article);

    List<Case> findAllByArticleAndRequestStatus(Article article, int status);
}
