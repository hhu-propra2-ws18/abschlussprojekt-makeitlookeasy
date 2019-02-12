package de.propra2.ausleiherino24.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
	
	// TODO: Add link to repository/service here.
	private final Logger LOGGER = LoggerFactory.getLogger(CaseController.class);
	
	// TODO: Link overview.
	@GetMapping("/")
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}
	
	// TODO: Add all cases.
	@GetMapping("/overview")
	public ModelAndView displayAllArticles() {
		ModelAndView mav = new ModelAndView("overview");
		return mav;
	}
	
}
