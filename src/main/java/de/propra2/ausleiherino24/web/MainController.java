package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.CaseRepository;
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

	private final CaseRepository caseRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

	@Autowired
	public MainController(CaseRepository caseRepository) {
		this.caseRepository = caseRepository;
	}

	// TODO: Link overview.
	@GetMapping("/")
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}

	// TODO: Add all cases
	@GetMapping("/overview")
	public ModelAndView displayAllArticles() {
		ModelAndView mav = new ModelAndView("overview");
		mav.addObject("all", caseRepository.findAll());
		return mav;
	}

	@GetMapping("/login")
	public ModelAndView getLogin(Model model){
		ModelAndView mav = new ModelAndView("login");
		return mav;
	}

	@RequestMapping("/default")
	public String defaultAfterLogin(HttpServletRequest request) {
		if (request.isUserInRole("ROLE_user")) {
			return "redirect:/accessed/user/index";
		} else
				return "redirect:/acessed/admin/index";
	}

	@GetMapping("/signup")
	public ModelAndView getRegistration(Model model){
		ModelAndView mav = new ModelAndView("registration");
		return mav;
	}
}
