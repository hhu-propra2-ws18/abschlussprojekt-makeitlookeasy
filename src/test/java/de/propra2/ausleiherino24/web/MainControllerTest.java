package de.propra2.ausleiherino24.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MainController.class)
public class MainControllerTest {
	
	@Autowired MockMvc mvc;
	@Autowired ObjectMapper objectMapper;
	
	@Test
	public void getIndex() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("index"));
	}
	
	@Test
	public void getLoginForm() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/login"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("login"));
	}
	
	@Test
	public void getSignupForm() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/signup"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("registration"));
	}
	
	@Test //TODO
	public void postNewlyRegisteredUser() throws Exception {
		/*
		User user = User.builder()
				.id(1L)
				.role("USER")
				.username("OmegaJens")
				.email("jbrizzle@hhu.de")
				.password("Christian<3")
				.build();
		
		Person person = Person.builder()
				.id(1L)
				.user(user)
				.firstName("Jens")
				.lastName("Bendisposto")
				.contact("NRW")
				.build();
		
		mvc.perform(MockMvcRequestBuilders.post("/registerNewUser"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("login"));
		*/
	}
	
	@Test //TODO
	public void cannotPostEmptyForm() {
	}
}
