package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Conflict;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ConflictRepository extends CrudRepository<Conflict, Long> {

	@Override
	List<Conflict> findAll();
}
