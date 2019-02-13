package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/accessed/user")
public class UserController {
	/*
	UserController manages all requests that are exclusively available to logged-in users of the platform, except article/case handling.
	This includes profile management.
	 */

	private final UserRepository userRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/")
	public ModelAndView displayUserProfile(@RequestParam("id") Long id) {
		User user = userRepository.getById(id);

		ModelAndView mav = new ModelAndView("profile");
		mav.addObject("user", user);
		return mav;
	}

	/*
	@GetMapping("/index")
	public String getIndex() {
		return "/accessed/user/index";
	}*/

	@GetMapping("/index")
	public ModelAndView getIndex() {
		return new ModelAndView("index");
	}
}
