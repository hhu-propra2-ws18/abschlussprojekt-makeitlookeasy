package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Case;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface CaseRepository extends CrudRepository<Case, Long> {
	ArrayList<Case> findAll();

	ArrayList<Case> findByOwner(Long id);

	ArrayList<Case> findByReceiver(Long id);
}
