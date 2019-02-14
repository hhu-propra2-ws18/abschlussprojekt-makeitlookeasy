package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;


/**
 *
 * 	UserController manages all requests that are exclusively available to logged-in users
 * 	of the platform, except article/case handling. This includes profile management.
 */
@Controller
@RequestMapping("/accessed/user")
public class UserController {
	private final UserRepository userRepository;
	private final PersonRepository personRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	public UserController(UserRepository userRepository, PersonRepository personRepository) {
		this.userRepository = userRepository;
		this.personRepository = personRepository;
	}

	@GetMapping("/profile")
	public ModelAndView displayUserProfile(@RequestParam("id") Long id, Principal principal) throws Exception {
		if(!userRepository.getById(id).isPresent()) {
			throw new Exception("User not found");
		}
		User user = userRepository.getById(id).get();
		boolean self = principal.getName().equals(user.getUsername());	// Flag for ThymeLeaf. Enables certain profile editing options.

		ModelAndView mav = new ModelAndView("profile");
		mav.addObject("user", user);
		mav.addObject("self", self);
		return mav;
	}

	@PutMapping("/editProfile")
	public void editUserProfile(@ModelAttribute @Valid User user, @ModelAttribute @Valid Person person) {
		userRepository.save(user);
		LOGGER.info("Updated user profile %s [ID=%L]", user.getUsername(), user.getId());
		personRepository.save(person);
		LOGGER.info("Updated person profile [ID=%L]", person.getId());

		/*
		ModelAndView mav = new ModelAndView("profile");
		mav.addObject("user", user);
		return mav;
		*/
	}

	@GetMapping("/index")
	public ModelAndView getIndex(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("/accessed/user/index");
		mav.addObject("role", RoleService.getUserRole(request));
		return mav;
	}

}
