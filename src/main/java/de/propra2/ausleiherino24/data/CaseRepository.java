package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Case;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface CaseRepository extends CrudRepository<Case, Long> {
	ArrayList<Case> findAll();
}
