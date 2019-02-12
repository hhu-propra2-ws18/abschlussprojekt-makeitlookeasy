package de.propra2.ausleiherino24.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
	
	// TODO: Add link to repository/service here.
	private final Logger LOGGER = LoggerFactory.getLogger(CaseController.class);
	
	// TODO: Catch, if case/article not in database. Add user.
	@GetMapping("/user")
	public ModelAndView displayUserProfile(@RequestParam("id") Long id) {
		//User user = userRepository.getById(id);
		
		ModelAndView mav = new ModelAndView("profile");
		//mav.addObject("user", user);
		return mav;
	}
	
}
