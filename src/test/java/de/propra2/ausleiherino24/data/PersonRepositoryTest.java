package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")

public class PersonRepositoryTest {

    @Autowired
    private PersonRepository persons;

    private Person person1;
    private Person person2;

    @BeforeEach
    public void init() {
        person1 = new Person();
        person1.setUser(new User());
        person1.setFirstName("Max");
        person1.setLastName("Mustermann");

        person2 = new Person();
        person2.setUser(new User());
        person2.setFirstName("Hans");
        person2.setLastName("Wurst");
    }

    @Test
    public void databaseShouldSaveEntities() {
        persons.saveAll(Arrays.asList(person1, person2));

        final List<Person> us = persons.findAll();
        Assertions.assertThat(us.size()).isEqualTo(2);
        Assertions.assertThat(us.get(0)).isEqualTo(person1);
        Assertions.assertThat(us.get(1)).isEqualTo(person2);
    }

    @Test
    public void databaseShouldRemoveCorrectEntity() {
        persons.saveAll(Arrays.asList(person1, person2));

        persons.delete(person2);

        final List<Person> us = persons.findAll();
        Assertions.assertThat(us.size()).isOne();
        Assertions.assertThat(us.get(0)).isEqualTo(person1);
    }

    @Test
    public void databaseShouldReturnCountOfTwoIfDatabaseHasTwoEntries() {
        persons.saveAll(Arrays.asList(person1, person2));

        final List<Person> us = persons.findAll();
        Assertions.assertThat(us.size()).isEqualTo(2);
    }


}
