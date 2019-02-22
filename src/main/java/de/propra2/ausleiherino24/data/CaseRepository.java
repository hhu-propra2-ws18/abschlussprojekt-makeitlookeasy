package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CaseRepository extends CrudRepository<Case, Long> {

    /** TODO: Heading.
     * @return ArrayList of all Case objects in database.
     */
    ArrayList<Case> findAll();


    @Query("SELECT c FROM Case c WHERE c.receiver.id = :user")
    ArrayList<Case> findByReceiver(@Param("user") User user);

    /** TODO: Heading.
     * @param user Ausleihender
     * @return alle dem User zugehöhrigen Cases
     */
    @Query("SELECT c FROM Case c WHERE c.receiver = :user")
    ArrayList<Case> findAllByReceiver(@Param("user") User user);

    /** TODO: Heading.
     * @param Owner Verleiher
     * @return alle dem Owner zugehöhrigen Cases
     */
    @Query("SELECT c FROM #{#entityName} c WHERE c.article.owner = :owner")
    ArrayList<Case> findAllByArticleOwner(@Param("owner") User Owner);

    /** TODO: Heading.
     * @param ownerId VerleiherId
     * @return alle dem Owner zugehöhrigen Cases
     */
    @Query("SELECT c FROM #{#entityName} c WHERE c.article.owner.id = :id ORDER BY c.requestStatus ASC")
    ArrayList<Case> findAllByArticleOwnerId(@Param("id") Long ownerId);

    Optional<Case> findByArticle(Article article);

    //Optional<Case> findByArticleOwnerId(Long id);

    //Optional<Case> findByReceiverId(Long id);

    ArrayList<Case> findAllByArticleAndRequestStatus(Article article,int status);
}
