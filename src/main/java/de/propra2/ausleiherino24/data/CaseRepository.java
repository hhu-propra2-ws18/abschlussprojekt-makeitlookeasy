package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseRepository extends CrudRepository<Case, Long> {

    @NonNull
    List<Case> findAll();

    @Query("SELECT c FROM Case c WHERE c.receiver = :user")
    List<Case> findAllByReceiver(@Param("user") User user);

    @Query("SELECT c FROM #{#entityName} c WHERE c.article.owner = :owner")
    List<Case> findAllByArticleOwner(@Param("owner") User owner);

    @Query("SELECT c FROM #{#entityName} c WHERE c.article.owner.id = :id "
            + "ORDER BY c.requestStatus ASC")
    List<Case> findAllByArticleOwnerId(@Param("id") Long ownerId);

    ArrayList<Case> findAllByArticle(Article article);

    List<Case> findAllByArticleAndRequestStatus(Article article, int status);

    @Query("SELECT c FROM #{#entityName} c "
            + "WHERE c.article.forSale = false "
            + "AND c.article.owner.id = :id "
            + "AND c.endTime < :today "
            + "AND c.requestStatus in (7, 8, 10, 14)")
    List<Case> findAllExpiredCasesByUserId(@Param("id") Long id, @Param("today") long today);

    @Query("SELECT c FROM #{#entityName} c "
            + "WHERE c.article.forSale = false "
            + "AND c.article.owner.id = :id "
            + "AND c.requestStatus in (1, 2, 4, 12)")
    List<Case> findAllRequestedCasesByUserId(@Param("id") Long id);

    //TODO: test
    @Query("SELECT c FROM Case c "
            + "WHERE c.receiver.person.id = :id "
            + "AND c.article.forSale = false")
    List<Case> getLendCasesFromPersonReceiver(@Param("id") Long personId);

    //TODO: test
    @Query("SELECT c FROM Case c "
            + "WHERE c.article.forSale = true "
            + "AND c.article.owner.id = :id "
            + "AND c.requestStatus = 14")
    List<Case> findAllSoldItemsByUserId(@Param("id") Long id);

    @Query("SELECT c FROM #{#entityName} c "
            + "WHERE c.article.forSale = false "
            + "AND c.receiver.id = :id "
            + "AND c.endTime between :today AND :tomorrow")
    List<Case> findAllOutrunningCasesByUserId(
            @Param("id") Long id,
            @Param("today") long today,
            @Param("tomorrow") long tomorrow);
}
