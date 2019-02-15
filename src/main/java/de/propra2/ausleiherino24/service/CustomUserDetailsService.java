package de.propra2.ausleiherino24.service;


import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.CustomUserDetails;
import de.propra2.ausleiherino24.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private UserRepository userRepository;

	@Autowired
	public CustomUserDetailsService(UserRepository userRepository){
		this.userRepository = userRepository;
	}

	//Service for login, when user is not found throws exception
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optionalUser = userRepository.findByUsername(username) ;

		optionalUser
				.orElseThrow(()-> new UsernameNotFoundException("Username not found"));
		return optionalUser
				.map(CustomUserDetails::new).get();
	}
}
