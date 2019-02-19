package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.ImageStoreService;
import de.propra2.ausleiherino24.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * Manages all requests regarding creating/editing/deleting articles/cases and after-sales.
 * Possible features: transaction rating (karma/voting), chatting
 */
@Controller
public class CaseController {

	private final ArticleService articleService;
	private final ImageStoreService imageStoreService;
	private final UserService userService;

	private final List<Category> allCategories = Category.getAllCategories();

	@Autowired
	public CaseController(ArticleService articleService, UserService userService, ImageStoreService imageStoreService) {
		this.articleService = articleService;
		this.userService = userService;
		this.imageStoreService = imageStoreService;
	}

	/**
	 * TODO JavaDoc
	 *
	 * @param id
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/article")
	public ModelAndView displayArticle(@RequestParam("id") Long id, Principal principal) throws Exception {
		Article article = articleService.findArticleById(id);
		User currentUser = userService.findUserByPrincipal(principal);

		ModelAndView mav = new ModelAndView("/shop/item");
		mav.addObject("article", article);
		mav.addObject("user", currentUser);
		mav.addObject("categories", allCategories);

		return mav;
	}

	/**
	 * TODO JavaDoc
	 *
	 * @param principal
	 * @return
	 */
	@GetMapping("/newArticle")
	public ModelAndView createNewCaseAndArticle(Principal principal) {
		Article article = new Article();
		User currentUser = userService.findUserByPrincipal(principal);

		ModelAndView mav = new ModelAndView("/shop/newItem");
		mav.addObject("article", article);
		mav.addObject("user", currentUser);
		mav.addObject("categories", allCategories);
		return mav;
	}

	/**
	 * TODO JavaDoc
	 *
	 * @param article
	 * @param result
	 * @param model
	 * @param image
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/saveNewArticle")
	public ModelAndView saveNewCaseAndArticle(@ModelAttribute @Valid Article article, BindingResult result, Model model,
											  @RequestParam("image") MultipartFile image, Principal principal) {
		User user = userService.findUserByPrincipal(principal);

		article.setActive(true);
		article.setReserved(false);
		article.setOwner(user);
		article.setImage(imageStoreService.store(image, null));
		articleService.saveArticle(article, "Created");

		return new ModelAndView("redirect:/");
	}

	/**
	 * TODO JavaDoc
	 *
	 * @param article
	 * @param result
	 * @param model
	 * @param image
	 * @param principal
	 * @return
	 */
	@PutMapping("/saveEditedArticle")
	public ModelAndView saveEditedCaseAndArticle(@ModelAttribute @Valid Article article, BindingResult result, Model model,
												 @RequestParam("image") MultipartFile image, Principal principal) {

		article.setImage(imageStoreService.store(image, null));
		articleService.saveArticle(article, "Updated");

		User currentUser = userService.findUserByPrincipal(principal);

		ModelAndView mav = new ModelAndView("/shop/item");
		mav.addObject("article", article);
		mav.addObject("user", currentUser);
		return mav;
	}

	/**
	 * TODO JavaDoc
	 *
	 * @param id
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@PutMapping("/deactivateArticle")
	public ModelAndView deactivateArticle(@RequestParam Long id, Principal principal) throws Exception {
		if (!articleService.deactivateArticle(id)) {
			// TODO: Display error msg, when article deactivation fails.
		}

		User currentUser = userService.findUserByPrincipal(principal);
		List<Article> myArticles = articleService.findAllActiveByUser(currentUser);

		ModelAndView mav = new ModelAndView("/user/myArticles");
		mav.addObject("myArticles", myArticles);
		mav.addObject("user", currentUser);
		return mav;
	}
}
