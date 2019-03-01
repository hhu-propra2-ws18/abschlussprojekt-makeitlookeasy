package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class PersonServiceTest {

    private PersonService personService;
    private PersonRepository persons;

    @BeforeEach
    void init() {

        persons = Mockito.mock(PersonRepository.class);
        personService = new PersonService(persons);
    }

    @Test
    void savePersonShouldSavePerson() {
        final Person person = new Person();
        person.setId(1L);

        persons = Mockito.mock(PersonRepository.class);
        personService = new PersonService(persons);

        personService.savePerson(person, "str");

        Mockito.verify(persons, Mockito.times(1)).save(person);
    }
}
