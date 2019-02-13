package de.propra2.ausleiherino24.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
	
	@Autowired MockMvc mvc;
	
	@Test
	public void getUserProfile() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/accessed/user/profile"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("profile"));
	}
	
	@Test
	public void getIndex() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/accessed/user/index"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("index"));
	}
	
	@Test //TODO
	public void putEditedProfile() throws Exception {
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
		
		mvc.perform(MockMvcRequestBuilders.post("/accessed/user/editProfile"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("profile"));
		*/
	}
}
