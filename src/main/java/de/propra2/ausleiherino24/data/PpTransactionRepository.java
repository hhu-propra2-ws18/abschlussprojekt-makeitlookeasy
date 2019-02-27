package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.PPTransaction;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public interface PpTransactionRepository extends CrudRepository<PPTransaction, Long> {

    @NonNull
    List<PPTransaction> findAll();
}
