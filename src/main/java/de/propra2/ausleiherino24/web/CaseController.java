package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
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

	private final ArticleRepository articleRepository;
	private final UserRepository userRepository;
	private final ArticleService articleService;
	private final UserService userService;
	private final Logger LOGGER = LoggerFactory.getLogger(CaseController.class);
	private ImageStoreService imageStorageService;

	@Autowired
	public CaseController(ArticleRepository articleRepository, UserRepository userRepository,
						  ArticleService articleService, UserService userService, ImageStoreService imageStorageService) {
		this.articleRepository = articleRepository;
		this.userRepository = userRepository;
		this.articleService = articleService;
		this.userService = userService;
		this.imageStorageService = imageStorageService;
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
		ModelAndView mav = new ModelAndView("newarticle");
		mav.addObject("article", new Article());
		return mav;
	}

	@PostMapping("/saveNewArticle")
	public ModelAndView saveNewCaseAndArticle(
			@ModelAttribute @Valid Article article,
			BindingResult result,
			Model model,
			@RequestParam("image") MultipartFile image
	) {
		article.setImage(imageStorageService.store(image, null));
		articleRepository.save(article);
		LOGGER.info("Created article %s [ID=%L]", article.getName(), article.getId());

		ModelAndView mav = new ModelAndView("case");
		Case c = new Case();
		c.setArticle(article);
		mav.addObject("case", c);
		return mav;
	}

	@PutMapping("/saveEditedArticle")
	public ModelAndView saveEditedCaseAndArticle(
			@ModelAttribute @Valid Article article,
			BindingResult result,
			Model model,
			@RequestParam("image") MultipartFile image
	) {
		article.setImage(imageStorageService.store(image, null));
		articleRepository.save(article);
		LOGGER.info("Edited article %s [ID=%L]", article.getName(), article.getId());

		ModelAndView mav = new ModelAndView("article");
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
		
		Optional<User> optionalUser = userRepository.findByUsername(currentPrincipalName);
		if (!optionalUser.isPresent()) {
			LOGGER.warn("Couldn't find user %s in UserRepository.", currentPrincipalName);
			throw new Exception("Couldn't find current principal in UserRepository.");
		}
		User user = optionalUser.get();
		
		if (!articleService.deactivateArticle(id)) {
			// TODO: Display error msg, when article deactivation fails.
		}
		
		ModelAndView mav = new ModelAndView("/accessed/user/profile");
		mav.addObject("user", user);
		mav.addObject("allArticles", articleRepository.findAllActiveByUser(user));
		return mav;
	}
}
