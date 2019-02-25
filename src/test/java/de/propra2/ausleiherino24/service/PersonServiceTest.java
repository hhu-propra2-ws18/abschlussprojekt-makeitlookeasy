package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.model.Person;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

// TODO: Replace Powermock with JMockit
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({PersonService.class})
public class PersonServiceTest {

    private PersonService personService;
    private PersonRepository persons;

    @Before
    public void init() {

        persons = Mockito.mock(PersonRepository.class);
        personService = new PersonService(persons);
    }

    @Test
    public void savePersonShouldSavePerson() throws Exception {
        final Person person = new Person();
        person.setId(1L);

        persons = Mockito.mock(PersonRepository.class);
        personService = new PersonService(persons);

        personService.savePerson(person, "str");

        Mockito.verify(persons, Mockito.times(1)).save(person);
    }
}
