package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Conflict;
import de.propra2.ausleiherino24.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ConflictRepository extends CrudRepository<Conflict, Long> {

    @NonNull
    List<Conflict> findAll();

    @NonNull
    Optional<Conflict> findById(@NonNull Long conflictId);

    @Query("SELECT c FROM Conflict c WHERE c.conflictedCase.receiver = :user")
    List<Conflict> findAllByReceiver(@Param("user") User user);

    @Query("SELECT c FROM #{#entityName} c WHERE c.conflictedCase.article.owner = :owner")
    List<Conflict> findAllByArticleOwner(@Param("owner") User owner);
}
