package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class CaseController {
	/*
	CaseController manages all requests regarding creating/editing/deleting articles/cases and after-sales.
	Features to come: transaction rating (karma/voting), chatting
	 */

	private final ArticleRepository articleRepository;
	private final CaseRepository caseRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(CaseController.class);

	@Autowired
	public CaseController(ArticleRepository articleRepository, CaseRepository caseRepository) {
		this.articleRepository = articleRepository;
		this.caseRepository = caseRepository;
	}

	@GetMapping("/article")
	public ModelAndView displayArticle(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
		Optional<Article> article = articleRepository.findById(id);
		if (!article.isPresent()) {
			throw new Exception("Article not found!");
		}

		ModelAndView mav = new ModelAndView("accessed/user/shopitem");
		mav.addObject("article", article);
		mav.addObject("role", RoleService.getUserRole(request));
		return mav;
	}

	@GetMapping("/newArticle")
	public ModelAndView createNewCaseAndArticle() {
		ModelAndView mav = new ModelAndView("article");
		mav.addObject("article", new Article());
		return mav;
	}

	@PostMapping("/saveNewArticle")
	public ModelAndView saveNewCaseAndArticle(@ModelAttribute @Valid Article article) {
		articleRepository.save(article);
		LOGGER.info("Created article %s [ID=%L]", article.getName(), article.getId());

		ModelAndView mav = new ModelAndView("case");
		Case c = new Case();
		c.setArticle(article);
		mav.addObject("case", c);
		return mav;
	}

	@PutMapping("/saveEditedArticle")
	public ModelAndView saveEditedCaseAndArticle(@ModelAttribute @Valid Article article) {
		articleRepository.save(article);
		LOGGER.info("Edited article %s [ID=%L]", article.getName(), article.getId());

		ModelAndView mav = new ModelAndView("article");
		mav.addObject("article", article);
		return mav;
	}

	@PutMapping("/deactivateArticle")
	public ModelAndView deactivateArticle(@ModelAttribute @Valid Article article) {
		LOGGER.warn("Deactivating cases offering %s [ID=%L]", article.getName(), article.getId());
		Iterable<Case> allCases = caseRepository.findAll();
		for (Case c : allCases) {
			if (c.getArticle().equals(article)) {
				c.setActive(false);
				caseRepository.save(c);
				LOGGER.info("Deactivated case with ID %L", c.getId());
			}
		}

		article.setActive(false);
		articleRepository.save(article);
		LOGGER.info("Deactivated article %s [ID=%L]", article.getName(), article.getId());

		ModelAndView mav = new ModelAndView("index");
		// TODO: Add object user to model, and display profile with userId
		return mav;
	}

}
