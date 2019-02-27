package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.CustomUserDetails;
import de.propra2.ausleiherino24.model.User;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SearchUserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public SearchUserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Service for login, when user is not found throws exception.
     */
    @Override
    public UserDetails loadUserByUsername(final String username) {
        final Optional<User> optionalUser = userRepository.findByUsername(username);

        return optionalUser
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
