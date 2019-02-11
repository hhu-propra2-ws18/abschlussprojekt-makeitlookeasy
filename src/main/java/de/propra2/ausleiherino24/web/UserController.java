package de.propra2.ausleiherino24.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
	
	// TODO: Add link to repository/service here.
	
	// TODO: Catch, if case/article not in database. Add user.
	@GetMapping("/user")
	public ModelAndView displayUserProfile(@RequestParam("id") Long id) {
		//User user = userRepository.getById(id);
		
		ModelAndView mav = new ModelAndView("profile");
		//mav.addObject("user", user);
		return mav;
	}
	
}
