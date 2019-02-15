package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.ImageStoreService;
import de.propra2.ausleiherino24.service.RoleService;
import de.propra2.ausleiherino24.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

/**
 * Manages all requests regarding creating/editing/deleting articles/cases and after-sales.
 * Possible features: transaction rating (karma/voting), chatting
 */
@Controller
public class CaseController {
	
	private final ArticleService articleService;
	private final ImageStoreService imageStoreService;
	private final UserService userService;

	@Autowired
	public CaseController(ArticleService articleService, ImageStoreService imageStoreService, UserService userService) {
		this.articleService = articleService;
		this.imageStoreService = imageStoreService;
		this.userService = userService;
	}

	@GetMapping("/article")
	public ModelAndView displayArticle(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView("shopitem");
		mav.addObject("article", articleService.findArticleById(id));
		mav.addObject("role", RoleService.getUserRole(request));
		return mav;
	}

	@GetMapping("/newArticle")
	public ModelAndView createNewCaseAndArticle() {
		ModelAndView mav = new ModelAndView("shopitem");
		mav.addObject("shopitem", new Article());
		return mav;
	}
	
	/**
	 * Creates new article in database and returns this.article's details view.
	 *
	 * @param article		Article object from HTML form input.
	 * @return				Article details view.
	 */
	@PostMapping("/saveNewArticle")
	public ModelAndView saveNewCaseAndArticle(
			@ModelAttribute @Valid Article article,
			BindingResult result,
			Model model,
			@RequestParam("image") MultipartFile image) {
		
		article.setImage(imageStoreService.store(image, null));
		articleService.saveArticle(article, "Created");

		ModelAndView mav = new ModelAndView("newarticle");
		mav.addObject("article", article);
		return mav;
	}
	
	/**
	 * Updates edited article in database and returns this.article's view.
	 *
	 * @param article		Article object from HTML form input.
	 * @return				Article details view.
	 */
	@PutMapping("/saveEditedArticle")
	public ModelAndView saveEditedCaseAndArticle(
				@ModelAttribute @Valid Article article,
				BindingResult result,
				Model model,
				@RequestParam("image") MultipartFile image) {
		
		article.setImage(imageStoreService.store(image, null));
		articleService.saveArticle(article, "Updated");

		ModelAndView mav = new ModelAndView("shopitem");
		mav.addObject("article", article);
		return mav;
	}

	/**
	 * Deactivates a single article.
	 *
	 * @param id			ID of article to be deactivated
	 * @param principal		Current user
	 * @return				View "myArticles", displaying all active articles of principal
	 * @throws Exception	1. Thrown, if article couldn't be found in ArticleRepository
	 * 						2. Thrown, if principal couldn't be found in UserRepository
	 */
	@PutMapping("/deactivateArticle")
	public ModelAndView deactivateArticle(@RequestParam Long id, Principal principal) throws Exception {
		String currentPrincipalName = principal.getName();
		User user = userService.findUserByUsername(currentPrincipalName);
		
		if (!articleService.deactivateArticle(id)) {
			// TODO: Display error msg, when article deactivation fails.
		}
		
		ModelAndView mav = new ModelAndView("/accessed/user/profile");
		mav.addObject("user", user);
		mav.addObject("allArticles", articleService.findAllActiveByUser(user));
		return mav;
	}
}
