package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MainController {
	/*
	MainController manages all actions that are available to every visitor of the platform.
	This does not include signup/login.
	 */
	@Autowired
	UserRepository userRepository;

	@Autowired
	PersonRepository personRepository;

	@Autowired
	UserService userService;

	private final CaseRepository caseRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

	@Autowired
	public MainController(CaseRepository caseRepository) {
		this.caseRepository = caseRepository;
	}


	@GetMapping("/")
	public ModelAndView index(HttpServletRequest request, Model model) {
		// TODO: Link overview.
		if(request.isUserInRole(HttpServletRequest.BASIC_AUTH))
			model.addAttribute("loggedIn", true);
		else
			model.addAttribute("loggedIn", false);
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}

	@GetMapping("/overview")
	public ModelAndView displayAllArticles() {
		ModelAndView mav = new ModelAndView("overview");
		mav.addObject("all", caseRepository.findAll());
		return mav;
	}

	@GetMapping("/signup")
	public ModelAndView getRegistration(Model model){
		return new ModelAndView("registration");
	}

	@GetMapping("/login")
	public ModelAndView getLogin(){
		return new ModelAndView("login");
	}

	@RequestMapping("/default")
	public String defaultAfterLogin(HttpServletRequest request) {
		if (request.isUserInRole("ROLE_user")) {
			return "redirect:/accessed/user/index";
		} else
			return "redirect:/acessed/admin/index";
	}

	@RequestMapping("/registernewuser")
	public ModelAndView registerNewUser(Person person, User user,Model model){
		userService.creatUserWithProfil(user,person);
		ModelAndView mvw = new ModelAndView("login");
		model.addAttribute("registration");
		return mvw;
	}
}
