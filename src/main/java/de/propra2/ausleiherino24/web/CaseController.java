package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.UserService;
import de.propra2.ausleiherino24.service.ImageStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

/**
 * Manages all requests regarding creating/editing/deleting articles/cases and after-sales.
 * Possible features: transaction rating (karma/voting), chatting
 */
@Controller
public class CaseController {
	
	private final ArticleService articleService;
	private final UserService userService;
	private final ArticleRepository articleRepository;
	private final UserRepository userRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(CaseController.class);
	private ImageStoreService imageStoreService;

	@Autowired
	public CaseController(ArticleRepository articleRepository, UserRepository userRepository,
						  ArticleService articleService, UserService userService, ImageStoreService imageStoreService) {
		this.articleRepository = articleRepository;
		this.userRepository = userRepository;
		this.articleService = articleService;
		this.userService = userService;
		this.imageStoreService = imageStoreService;
	}

	@GetMapping("/article")
	public ModelAndView displayArticle(@RequestParam("id") Long id, Principal principal) throws Exception {
		Optional<Article> article = articleRepository.findById(id);
		if (!article.isPresent()) {
			throw new Exception("Article not found!");
		}
		ModelAndView mav = new ModelAndView("/accessed/user/shopitem");
		mav.addObject("article", article.get());
		mav.addObject("categories", Category.getAllCategories());
		mav.addObject("user", userService.findUserByPrincipal(principal));
		return mav;
	}

	@GetMapping("/newArticle")
	public ModelAndView createNewCaseAndArticle() {
		ModelAndView mav = new ModelAndView("/accessed/user/shopitem");
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

		ModelAndView mav = new ModelAndView("/accessed/user/shopitem");
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
