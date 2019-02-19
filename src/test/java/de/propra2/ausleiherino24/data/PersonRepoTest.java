package de.propra2.ausleiherino24.data;

import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles(profiles = "test")

public class PersonRepoTest {

	@Autowired
	private PersonRepository persons;

	private Person person1;
	private Person person2;

	@Before
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

		List<Person> us = persons.findAll();
		Assertions.assertThat(us.size()).isEqualTo(2);
		Assertions.assertThat(us.get(0)).isEqualTo(person1);
		Assertions.assertThat(us.get(1)).isEqualTo(person2);
	}

	@Test
	public void databaseShouldRemoveCorrectEntity() {
		persons.saveAll(Arrays.asList(person1, person2));

		persons.delete(person2);

		List<Person> us = persons.findAll();
		Assertions.assertThat(us.size()).isOne();
		Assertions.assertThat(us.get(0)).isEqualTo(person1);
	}

	@Test
	public void databaseShouldReturnCountOfTwoIfDatabaseHasTwoEntries() {
		persons.saveAll(Arrays.asList(person1, person2));

		List<Person> us = persons.findAll();
		Assertions.assertThat(persons.count()).isEqualTo(2);
	}


}
