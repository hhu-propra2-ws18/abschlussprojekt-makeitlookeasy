package de.propra2.ausleiherino24.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.CustomUserDetails;
import de.propra2.ausleiherino24.model.User;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class SearchUserServiceTest {

    private UserRepository users;
    private SearchUserService searchUserService;
    private User user1;

    @BeforeEach
    void init() {
        users = Mockito.mock(UserRepository.class);
        user1 = new User();
        user1.setUsername("user1");
        searchUserService = new SearchUserService(users);
    }

    // TODO: Rename!
    @Test
    void test() {
        Mockito.when(users.findByUsername("user1")).thenReturn(Optional.of(user1));
        final CustomUserDetails expected = new CustomUserDetails(user1);
        Assertions.assertThat(searchUserService.loadUserByUsername("user1")).isEqualTo(expected);
    }

    // TODO: Rename!
    @Test
    void test2() {

        assertThrows(UsernameNotFoundException.class, () -> {
            Mockito.when(users.findByUsername("user1")).thenThrow(UsernameNotFoundException.class);

            searchUserService.loadUserByUsername("user1");
        });

    }
}
