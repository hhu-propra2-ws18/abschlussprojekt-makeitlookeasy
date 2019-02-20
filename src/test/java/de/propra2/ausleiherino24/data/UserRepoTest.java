package de.propra2.ausleiherino24.data;

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

public class UserRepoTest {

    @Autowired
    private UserRepository users;

    private User user1;
    private User user2;

    @Before
    public void init() {
        user1 = new User();
        user1.setUsername("foo");
        user1.setPassword("password");
        user1.setEmail("bar@baz.com");
        users.save(user1);

        user2 = new User();
        user2.setUsername("baz");
        user2.setPassword("secret");
        user2.setEmail("foo@bar.net");
    }

    @Test
    public void databaseShouldSaveEntities() {
        users.saveAll(Arrays.asList(user1, user2));

        List<User> us = users.findAll();
        Assertions.assertThat(us.size()).isEqualTo(2);
        Assertions.assertThat(us.get(0)).isEqualTo(user1);
        Assertions.assertThat(us.get(1)).isEqualTo(user2);
    }

    @Test
    public void databaseShouldRemoveCorrectEntity() {
        users.saveAll(Arrays.asList(user1, user2));

        users.delete(user1);

        List<User> us = users.findAll();
        Assertions.assertThat(us.size()).isOne();
        Assertions.assertThat(us.get(0)).isEqualTo(user2);
    }

    @Test
    public void databaseShouldReturnCountOfTwoIfDatabaseHasTwoEntries() {
        users.saveAll(Arrays.asList(user1, user2));

        List<User> us = users.findAll();
        Assertions.assertThat(users.count()).isEqualTo(2);
        Assertions.assertThat(us.size()).isEqualTo(2);
    }

    @Test
    public void queryGetByIdShouldReturnUserWithCorrespondingId() {
        users.saveAll(Arrays.asList(user1, user2));

        User expectedUser = users.getById(user1.getId()).get();

        Assertions.assertThat(expectedUser).isEqualTo(user1);
    }

    @Test
    public void queryGetByIdShouldReturnEmptyOptional() {
        users.saveAll(Arrays.asList(user1, user2));

        boolean userExists = users.getById(0L).isPresent();

        Assertions.assertThat(userExists).isEqualTo(false);
    }

    @Test
    public void queryFindByUsernameShouldReturnUserWithCorrespondingUsername() {
        users.saveAll(Arrays.asList(user1, user2));

        User expectedUser = users.findByUsername(user2.getUsername()).get();

        Assertions.assertThat(expectedUser).isEqualTo(user2);
    }

    @Test
    public void queryFindByUsernameSouldReturnEmptyOptional() {
        users.saveAll(Arrays.asList(user1, user2));

        boolean userExists = users.findByUsername("affe").isPresent();

        Assertions.assertThat(userExists).isEqualTo(false);
    }

}
