package de.propra2.ausleiherino24.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CaseController {
	/*
	CaseController manages all requests regarding creating/editing/deleting articles/cases and after-sales.
	Features to come: transaction rating (karma/voting), chatting
	 */
	
	// TODO: Add link to repository/service here.
	// TODO: Log creation/updates.
	private final Logger LOGGER = LoggerFactory.getLogger(CaseController.class);
	
	@GetMapping("/article")
	public ModelAndView displayArticle(@RequestParam("id") Long id) {
		// TODO: Catch, if article not in database. Add article.
		
		//Article article = articleRepository.getById(id);
		
		ModelAndView mav = new ModelAndView("article");
		//mav.addObject("article", article);
		return mav;
	}
	
	@GetMapping("/newArticle")
	public ModelAndView createNewCaseAndArticle() {
		// TODO : Add article.
		
		ModelAndView mav = new ModelAndView("article");
		//mav.addObject("article", new Article());
		return mav;
	}
	
	@PostMapping("/saveNewArticle")
	public ModelAndView saveNewCaseAndArticle() {
		// TODO: Specify template to return => adjust returned model. (AFTER .hmtl is complete)
		
		//articleRepository.save(article);
		//LOGGER.info("New article %s [%d] saved successfully.", article.getName(), article.getId());
		
		ModelAndView mav = new ModelAndView("case");
		//mav.addObject("case", new Case(article));
		return mav;
	}
	
	@PutMapping("/saveEditedArticle")
	public ModelAndView saveEditedCaseAndArticle() {
		// TODO: Specify template to return => adjust returned model. (AFTER .hmtl is complete)
		
		//articleRepository.save(article);
		//LOGGER.info("Article %s edited successfully.", article.toString());
		
		ModelAndView mav = new ModelAndView("article");
		//mav.addObject("article", article);
		return mav;
	}
	
	
	
}
