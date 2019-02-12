package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import org.springframework.stereotype.Service;


@Service
public class UserService {

	private final PersonRepository personRepository;
	private final UserRepository userRepository;

	public UserService(PersonRepository personRepository, UserRepository userRepository){
		this.personRepository = personRepository;
		this.userRepository = userRepository;
	}

	public void creatUserWithProfil(User user, Person person){
		Person personactual = person;
		User useractual = user;
		person.setUser(user);
		user.setRole("user");
		personRepository.save(personactual);
		userRepository.save(useractual);
	}
}
