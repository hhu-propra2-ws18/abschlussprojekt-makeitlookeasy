package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;


@Service
public class UserService {

	private final PersonRepository personRepository;
	private final UserRepository userRepository;

	@Autowired
	public UserService(PersonRepository personRepository, UserRepository userRepository){
		this.personRepository = personRepository;
		this.userRepository = userRepository;
	}

	/**
	 * TODO Was ist die Idee hier?
	 * @param user
	 * @param person
	 */
	public void createUserWithProfile(User user, Person person){
		user.setRole("user");
		person.setUser(user);
		personRepository.save(person);
		userRepository.save(user);
	}

	public User findUserByPrincipal(Principal principal) throws Exception{
		User user;
		if(principal.getName() == null) {
			user = new User();
			user.setRole("");
			user.setUsername("");
		} else {
			if (!userRepository.findByUsername(principal.getName()).isPresent())
				throw new Exception("User " + principal.getName() + " not found");
			user = userRepository.findByUsername(principal.getName()).get();
		}
		return user;
	}
}
