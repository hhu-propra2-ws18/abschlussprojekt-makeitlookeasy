package de.propra2.ausleiherino24;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
public class Initializer implements ServletContextInitializer{

	@Autowired
	UserRepository userRepository;

	@Override
	public void onStartup(final ServletContext servletContext){
		userRepository.deleteAll();
		User user = new User();
		user.setUsername("user");
		user.setPassword("password");
		user.setEmail("user@mail.com");
		userRepository.save(user);
	}
}
