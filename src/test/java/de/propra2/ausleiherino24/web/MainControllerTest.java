package de.propra2.ausleiherino24.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.propra2.ausleiherino24.Ausleiherino24Application;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "test")
@SpringBootTest(classes = Ausleiherino24Application.class)
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("user");
        user.setRole("user");
        user.setPerson(mock(Person.class));
    }

    @Test
    @WithMockUser(roles = "user")
    void getMainPageTest() throws Exception {
        when(userService.findUserByPrincipal(any(Principal.class))).thenReturn(user);
        mvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers
                        .view().name("index"));
    }

    @Test
    void getLoginFormTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers
                        .view().name("login"));
    }

    @Test
    void getRegistrationFormTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/signUp"))
                .andExpect(MockMvcResultMatchers
                        .status().isOk())
                .andExpect(MockMvcResultMatchers
                        .view().name("registration"))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", Matchers.instanceOf(User.class)))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("person", Matchers.instanceOf(Person.class)));
    }

    @Test
    void registerNewUserModelTest() throws Exception {
        final Person person = new Person();
        final User user = new User();
        final Map<String, Object> map = new HashMap<>();

        person.setId(1L);
        user.setId(1L);
        map.put("person", person);
        map.put("user", user);

        mvc.perform(MockMvcRequestBuilders.get("/signUp").flashAttrs(map))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("user", Matchers.instanceOf(User.class)))
                .andExpect(MockMvcResultMatchers
                        .model().attribute("person", Matchers.instanceOf(Person.class)));
    }

    @Test
    void registerNewUserStatusTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/registerNewUser")).andExpect(MockMvcResultMatchers
                .status().is3xxRedirection());
        Mockito.verify(userService, Mockito.times(1))
                .saveUserWithProfile(
                        new User(), new Person(), "Created");
    }

}
