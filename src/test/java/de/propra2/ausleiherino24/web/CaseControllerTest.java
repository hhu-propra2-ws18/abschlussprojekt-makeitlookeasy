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
@WebMvcTest(CaseController.class)
public class CaseControllerTest {
	
	@Autowired MockMvc mvc;
	
	@Test //TODO
	public void getArticleWithId() throws Exception {
		/*
		mvc.perform(MockMvcRequestBuilders
				.get("/article")
				.param("id", String.valueOf(1L))
		).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("profile"));
		*/
	}
	
	@Test
	public void getNewArticleForm() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/accessed/user/index"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("index"));
	}
	
	// TODO: create new article, when logged in
	
	// TODO: edit own articles
	
	// TODO: delete own articles, if not rented, but allow delete when reserved
	
	// TODO: reserve articles, when logged in, that are not your own
	
	// TODO: rent articles, when logged in, that are not your own
	
}
