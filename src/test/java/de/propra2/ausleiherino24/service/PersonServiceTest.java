package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.model.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PersonService.class, LoggerFactory.class})
public class PersonServiceTest {

  private PersonService personService;
  private PersonRepository persons;
  private Logger logger;

  @Before
  public void init() {
    PowerMockito.mockStatic(LoggerFactory.class);
    logger = PowerMockito.mock(Logger.class);
    PowerMockito.when(LoggerFactory.getLogger(PersonService.class)).thenReturn(logger);

    persons = Mockito.mock(PersonRepository.class);
    personService = new PersonService(persons);
  }

  @Test
  public void savePersonShouldSavePerson() throws Exception {
    Person person = new Person();
    person.setId(1L);

    persons = Mockito.mock(PersonRepository.class);
    personService = new PersonService(persons);

    personService.savePerson(person, "str");

    Mockito.verify(persons, Mockito.times(1)).save(person);
    Mockito.verify(logger, Mockito.times(1)).info("%s person profile [ID=%L]", "str", 1L);
  }
}
