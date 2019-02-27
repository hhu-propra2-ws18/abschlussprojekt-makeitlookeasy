package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.PpTransaction;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public interface PpTransactionRepository extends CrudRepository<PpTransaction, Long> {

    @NonNull
    List<PpTransaction> findAll();
}
