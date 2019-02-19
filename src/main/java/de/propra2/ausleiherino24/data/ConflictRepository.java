package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Conflict;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConflictRepository extends CrudRepository<Conflict, Long> {

	@Override
	List<Conflict> findAll();
}
