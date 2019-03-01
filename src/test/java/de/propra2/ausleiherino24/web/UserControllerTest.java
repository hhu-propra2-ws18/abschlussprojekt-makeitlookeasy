package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.UserService;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;

    @Disabled
    @Test
    void displayUserProfileStatusTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/accessed/user/user?id=1"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection());
    }

    @Disabled //TODO: fix test
    @Test
    void displayUserProfileViewTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/accessed/user?id=1"))
                .andExpect(MockMvcResultMatchers
                        .view().name("profile"));
    }

    @Disabled //TODO: fix test
    @Test
    void displayUserProfileModelTest() throws Exception {
        final User user = User.builder()
                .id(1L)
                .email("user@mail.com")
                .password("password")
                .username("user1")
                .role("admin")
                .build();

        Mockito.when(userService.findUserByUsername("user1")).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/accessed/user/profile/user1"))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", Matchers.is(Optional.of(user))));
        Mockito.verify(userService, Mockito.times(1)).findUserByUsername("user1");
    }

    @Disabled
    @Test
    void getIndexStatusTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/accessed/user/index"))
                .andExpect(MockMvcResultMatchers
                        .status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers
                        .redirectedUrl("http://localhost/login"));
    }

}
