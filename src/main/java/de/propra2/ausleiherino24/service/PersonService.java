package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.model.Person;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(final PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Saves created/updated Person object to database.
     *
     * @param person Person object that gets stored in databased.
     * @param msg String with message for LOGGER. Either "Created" or "Updated".
     */
    void savePerson(final Person person, final String msg) {
        personRepository.save(person);
        LOGGER.info("{} person profile [ID={}]", msg, person.getId());
    }

    /**
     * returns person with given id.
     *
     * @param id personId
     */
    Person findPersonById(final Long id) {
        final Optional<Person> optionalPerson = personRepository.findById(id);

        if (!optionalPerson.isPresent()) {
            LOGGER.warn("Couldn't find person {} in database.", id);
            throw new NoSuchElementException();
        }

        return optionalPerson.get();
    }
}
