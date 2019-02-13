package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CaseService;
import de.propra2.ausleiherino24.service.RoleService;
import de.propra2.ausleiherino24.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {
	/*
	MainController manages all actions that are available to every visitor of the platform.
	This includes basic browsing, and signup/login.
	 */
	
	private final UserService userService;
	private final CaseService caseService;
	private final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

	@Autowired
	public MainController(UserService userService, CaseService caseService) {
		this.caseService = caseService;
		this.userService = userService;
	}
	
	// Display main page and check for authenticated user.
	@GetMapping("/")
	public ModelAndView index(HttpServletRequest request) {
		// TODO: Link overview.
		ModelAndView mav = new ModelAndView("index");
		mav.addObject("allCases", caseService.findAllCasesWithNonActiveArticles());
		mav.addObject("role", RoleService.getUserRole(request));
		return mav;
	}
	
	/** LOGIN && SIGNUP **/
	@GetMapping("/login")
	public ModelAndView getLogin(){
		return new ModelAndView("login");
	}

	@RequestMapping("/default")
	public String defaultAfterLogin(HttpServletRequest request) {
		if (request.isUserInRole("ROLE_user")) {
			return "redirect:/accessed/user/index";
		} else
			return "redirect:/accessed/admin/index";
	}
	
	@GetMapping("/signup")
	public ModelAndView getRegistration(){
		ModelAndView mav = new ModelAndView("registration");
		mav.addObject("user", new User());
		mav.addObject("person", new Person());
		return mav;
	}

	@PostMapping("/registerNewUser")
	public ModelAndView registerNewUser(Person person, User user){
		userService.createUserWithProfile(user,person);
		LOGGER.info("Created new person [ID=%L] and user %s [ROLE=%s, ID=%L]", person.getId(), user.getUsername(), user.getRole(), user.getId());
		
		return new ModelAndView("login");
	}
}
