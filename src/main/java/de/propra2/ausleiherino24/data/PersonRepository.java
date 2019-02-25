package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Person;
import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public interface PersonRepository extends CrudRepository<Person, Long> {

    @NonNull
    ArrayList<Person> findAll();
}
