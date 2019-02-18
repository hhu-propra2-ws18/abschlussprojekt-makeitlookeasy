package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import de.propra2.ausleiherino24.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest
public class AdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ArticleRepository articles;
    @MockBean
    private UserRepository users;
    @MockBean
    private PersonRepository persons;
    @MockBean
    private CaseRepository cases;

    @MockBean
    private ImageStoreService is;
    @MockBean
    private UserService us;
    @MockBean
    private PersonService ps;
    @MockBean
    private ArticleService as;
    @MockBean
    private SearchUserService uds;
    @MockBean
    private RoleService rs;
    @MockBean
    private AccountHandler ah;

    @Test
    public void getAdminIndexStatusTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/accessed/admin/index"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }
}
