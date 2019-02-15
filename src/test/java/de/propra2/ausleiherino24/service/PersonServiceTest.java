package de.propra2.ausleiherino24.service;

import org.junit.Before;
import org.junit.Ignore;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.model.Person;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PersonService.class, LoggerFactory.class})
public class PersonServiceTest {
	
	private PersonService personService;
	private PersonRepository persons;
	
	@Before
	public void init() {
		persons = Mockito.mock(PersonRepository.class);
		personService = new PersonService(persons);
	}
	@Ignore
	@Test
	public void savePersonShouldSavePerson() throws Exception{
		PowerMockito.mockStatic(LoggerFactory.class);
		Logger logger = PowerMockito.mock(Logger.class);
		PowerMockito.when(LoggerFactory.getLogger(PersonService.class)).thenReturn(logger);
		Person person = new Person();
		person.setId(1L);
		
		personService.savePerson(person, "str");
		
		Mockito.verify(persons, Mockito.times(1)).save(person);
		Mockito.verify(logger, Mockito.times(1)).info("%s person profile [ID=%L]", "str", 1L);
	}
}
