package de.propra2.ausleiherino24.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CaseController {
	
	// TODO: Add link to repository/service here.
	
	// TODO: Catch, if case not in database. Add case.
	@GetMapping("/article")
	public ModelAndView displayArticle(@RequestParam("id") Long id) {
		//Case case = caseRepository.getById(id);
		
		ModelAndView mav = new ModelAndView("article");
		//mav.addObject("case", case);
		return mav;
	}
	
	// TODO : Add case.
	@GetMapping("/newArticle")
	public ModelAndView createNewCaseAndArticle() {
		ModelAndView mav = new ModelAndView("article");
		//mav.addObject("user", new Case());
		return mav;
	}
	
	// TODO: Specify template to return => adjust returned model. (AFTER .hmtl is complete)
	@PostMapping("/saveNewArticle")
	public ModelAndView saveNewCaseAndArticle() {
		ModelAndView mav = new ModelAndView("article");
		//mav.addObject("object", object);
		return mav;
	}
	
	// TODO: Specify template to return => adjust returned model. (AFTER .hmtl is complete)
	@PutMapping("/saveEditedArticle")
	public ModelAndView saveEditedCaseAndArticle() {
		ModelAndView mav = new ModelAndView("article");
		//mav.addObject("object", object);
		return mav;
	}
	
}
