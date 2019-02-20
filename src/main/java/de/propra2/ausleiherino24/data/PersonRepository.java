package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface PersonRepository extends CrudRepository<Person, Long> {

	ArrayList<Person> findAll();
}
