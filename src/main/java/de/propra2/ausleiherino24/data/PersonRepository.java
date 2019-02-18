package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Person;
import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, Long> {

	ArrayList<Person> findAll();
}
