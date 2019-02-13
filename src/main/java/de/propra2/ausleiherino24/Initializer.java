package de.propra2.ausleiherino24;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
public class Initializer implements ServletContextInitializer{

	private final UserRepository userRepository;
	
	@Autowired
	public Initializer(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public void onStartup(final ServletContext servletContext) {
		userRepository.deleteAll();
		
		User user = new User();
		user.setUsername("user");
		user.setPassword("password");
		user.setEmail("user@mail.com");
		user.setRole("user");
		
		User user2 = new User();
		user2.setUsername("admin");
		user2.setPassword("password");
		user2.setEmail("useradmin@mail.com");
		user2.setRole("admin");
		
		userRepository.save(user2);
		userRepository.save(user);
	}
}
