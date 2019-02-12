package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/accessed/user")
public class UserController {
<<<<<<< HEAD
	/*
	UserController manages all requests that are exclusively available to logged-in users of the platform, except article/case handling.
	This includes signup, login, and profile management.
	 */
	
	private final UserRepository userRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@GetMapping("/user")
	public ModelAndView displayUserProfile(@RequestParam("id") Long id) {
		// TODO: Add user.
		User user = userRepository.getById(id);
		
=======

	// TODO: Add link to repository/service here.
	private final Logger LOGGER = LoggerFactory.getLogger(CaseController.class);

	// TODO: Catch, if case/article not in database. Add user.
	@GetMapping("/user")
	public ModelAndView displayUserProfile(@RequestParam("id") Long id) {
		//User user = userRepository.getById(id);

>>>>>>> frontDev
		ModelAndView mav = new ModelAndView("profile");
		mav.addObject("user", user);
		return mav;
	}
<<<<<<< HEAD
	
	@GetMapping("/signup")
	public ModelAndView signupView() {
		return new ModelAndView("signup");
	}
	
	@GetMapping("/login")
	public ModelAndView loginView() {
		return new ModelAndView("login");
	}
	
=======

	@GetMapping("/index")
	public String getIndex() {
		return "/accessed/user/index";
	}

>>>>>>> frontDev
}
