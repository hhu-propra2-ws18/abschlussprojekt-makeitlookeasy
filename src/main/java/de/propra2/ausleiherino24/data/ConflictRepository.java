package de.propra2.ausleiherino24.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.propra2.ausleiherino24.model.Conflict;

public interface ConflictRepository extends CrudRepository<Conflict, Long> {
	@Override
	List<Conflict> findAll();
}
