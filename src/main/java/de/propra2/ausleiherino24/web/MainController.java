package de.propra2.ausleiherino24.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
	/*
	MainController manages all actions that are available to every visitor of the platform.
	This does not include signup/login.
	 */
	
	// TODO: Add link to repository/service here.
	private CaseRepository caseRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
	
	@GetMapping("/")
	public ModelAndView index() {
		// TODO: Link overview.
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}
	
	@GetMapping("/overview")
	public ModelAndView displayAllArticles() {
		// TODO: Add all cases.
		ModelAndView mav = new ModelAndView("overview");
		return mav;
	}

}
