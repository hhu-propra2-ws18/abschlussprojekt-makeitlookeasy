package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.ConflictRepository;
import de.propra2.ausleiherino24.data.CustomerReviewRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.email.EmailSender;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.ImageStoreService;
import de.propra2.ausleiherino24.service.PersonService;
import de.propra2.ausleiherino24.service.RoleService;
import de.propra2.ausleiherino24.service.SearchUserService;
import de.propra2.ausleiherino24.service.UserService;
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
	private ConflictRepository conflicts;
	@MockBean
	private ArticleRepository articles;
	@MockBean
	private UserRepository users;
	@MockBean
	private PersonRepository persons;
	@MockBean
	private CaseRepository cases;
	@MockBean
	private CustomerReviewRepository customerReviews;

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
	@MockBean
	private EmailSender es;

	@Test
	public void getAdminIndexStatusTest() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/accessed/admin/index"))
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
	}
}
