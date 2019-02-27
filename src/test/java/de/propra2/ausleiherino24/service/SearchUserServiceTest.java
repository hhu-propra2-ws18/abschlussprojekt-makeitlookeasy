package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.CustomUserDetails;
import de.propra2.ausleiherino24.model.User;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SearchUserServiceTest {

    private UserRepository users;
    private SearchUserService searchUserService;
    private User user1;

    @Before
    public void init() {
        users = Mockito.mock(UserRepository.class);
        user1 = new User();
        user1.setUsername("user1");
        searchUserService = new SearchUserService(users);
    }

    @Test
    public void test() {
        Mockito.when(users.findByUsername("user1")).thenReturn(Optional.of(user1));
        final CustomUserDetails expected = new CustomUserDetails(user1);
        Assertions.assertThat(searchUserService.loadUserByUsername("user1")).isEqualTo(expected);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void test2() {
        Mockito.when(users.findByUsername("user1")).thenThrow(UsernameNotFoundException.class);

        searchUserService.loadUserByUsername("user1");
    }
}
