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
	@GetMapping
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView("index");
		return mav;
	}
	
	// TODO: Add all cases.
	@GetMapping
	public ModelAndView displayAllArticles() {
		ModelAndView mav = new ModelAndView("overview");
		return mav;
	}
	
	// TODO: Catch, if case/article not in database. Add article.
	@GetMapping
	public ModelAndView displayArticle(@RequestParam Long id) {
		//Article article = caseRepository.getById(id).getArticle();
		
		ModelAndView mav = new ModelAndView("article");
		//mav.addObject("article", article);
		return mav;
	}
	
	
	// TODO: Catch, if case/article not in database. Add user.
	@GetMapping
	public ModelAndView displayUserProfile(@RequestParam Long id) {
		//User user = userRepository.getById(id);
		
		ModelAndView mav = new ModelAndView("profile");
		//mav.addObject("user", user);
		return mav;
	}
	
	// TODO : Add case.
	@GetMapping
	public ModelAndView createNewCase() {
		ModelAndView mav = new ModelAndView("profile");
		//mav.addObject("user", new Case());
		return mav;
	}
	
	// TODO: Specify template to return => adjust returned model. (AFTER .hmtl is complete)
	@PostMapping
	public ModelAndView saveNewCase() {
		ModelAndView mav = new ModelAndView("profile");
		//mav.addObject("object", object);
		return mav;
	}
	
	// TODO: Specify template to return => adjust returned model. (AFTER .hmtl is complete)
	@PutMapping
	public ModelAndView saveEditedCase() {
		ModelAndView mav = new ModelAndView("");
		//mav.addObject("object", object);
		return mav;
	}
	
}
