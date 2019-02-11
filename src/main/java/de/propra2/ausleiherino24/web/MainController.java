package de.propra2.ausleiherino24.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
	
	// TODO: Add link to repository/service here.
	
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
	
	// TODO: Catch, if case not in database. Add case.
	@GetMapping("/article")
	public ModelAndView displayArticle(@RequestParam("id") Long id) {
		//Case case = caseRepository.getById(id);
		
		ModelAndView mav = new ModelAndView("article");
		//mav.addObject("case", case);
		return mav;
	}
	
	
	// TODO: Catch, if case/article not in database. Add user.
	@GetMapping("/user")
	public ModelAndView displayUserProfile(@RequestParam("id") Long id) {
		//User user = userRepository.getById(id);
		
		ModelAndView mav = new ModelAndView("profile");
		//mav.addObject("user", user);
		return mav;
	}
	
	// TODO : Add case.
	@GetMapping("/newArticle")
	public ModelAndView createNewCaseAndArticle() {
		ModelAndView mav = new ModelAndView("profile");
		//mav.addObject("user", new Case());
		return mav;
	}
	
	// TODO: Specify template to return => adjust returned model. (AFTER .hmtl is complete)
	@PostMapping("/saveNewArticle")
	public ModelAndView saveNewCaseAndArticle() {
		ModelAndView mav = new ModelAndView("profile");
		//mav.addObject("object", object);
		return mav;
	}
	
	// TODO: Specify template to return => adjust returned model. (AFTER .hmtl is complete)
	@PutMapping("/saveEditedArticle")
	public ModelAndView saveEditedCaseAndArticle() {
		ModelAndView mav = new ModelAndView("");
		//mav.addObject("object", object);
		return mav;
	}
	
}
