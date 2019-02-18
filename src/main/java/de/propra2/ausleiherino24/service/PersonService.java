package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

  private final PersonRepository personRepository;

  private final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

  @Autowired
  public PersonService(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  /**
   * Saves created/updated Person object to database.
   *
   * @param person Person object that gets stored in databased.
   * @param msg String with message for logger. Either "Created" or "Updated".
   */
  void savePerson(Person person, String msg) {
    personRepository.save(person);
    LOGGER.info("%s person profile [ID=%L]", msg, person.getId());
  }

}
