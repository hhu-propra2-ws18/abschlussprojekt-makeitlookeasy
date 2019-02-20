package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * MainController manages all actions that are available to every visitor of the platform. This
 * includes basic browsing, and signup/login.
 */
@Controller
public class MainController {

	private final ArticleService articleService;
	private final UserService userService;

	private final List<Category> allCategories = Category.getAllCategories();

	@Autowired
	public MainController(UserService userService, ArticleService articleService) {
		this.userService = userService;
		this.articleService = articleService;
	}

	/**
	 * TODO JavaDoc
	 *
	 * @param principal
	 * @return
	 */
	@GetMapping(value = {"/", "/index"})
	public ModelAndView getIndex(Principal principal) {
		List<Article> allArticles = articleService.getAllActiveArticles();
		User currentUser = userService.findUserByPrincipal(principal);

		ModelAndView mav = new ModelAndView("index");
		mav.addObject("all", allArticles);
		mav.addObject("user", currentUser);
		mav.addObject("categories", allCategories);
		return mav;
	}

	/**
	 * TODO JavaDoc
	 *
	 * @param category
	 * @param principal
	 * @return
	 */
	@GetMapping("/categories")
	public ModelAndView getIndexByCategory(@RequestParam String category, Principal principal) {
		List<Article> allArticlesInCategory = articleService.getAllArticlesByCategory(Category.valueOf(category.toUpperCase()));
		User currentUser = userService.findUserByPrincipal(principal);

		ModelAndView mav = new ModelAndView("index");
		mav.addObject("all", allArticlesInCategory);
		mav.addObject("user", currentUser);
		mav.addObject("categories", allCategories);
		return mav;
	}

	/**
	 * TODO JavaDoc
	 *
	 * @return
	 */
	@GetMapping("/login")
	public ModelAndView getLogin() {
		return new ModelAndView("login");
	}

	/**
	 * TODO JavaDoc
	 *
	 * @return
	 */
	@GetMapping("/signup")
	public ModelAndView getRegistration() {
		User user = new User();
		Person person = new Person();

		ModelAndView mav = new ModelAndView("registration");
		mav.addObject("user", user);
		mav.addObject("person", person);
		return mav;
	}

	/**
	 * TODO JavaDoc
	 *
	 * @param user
	 * @param person
	 * @return
	 */
	@PostMapping("/registerNewUser")
	public ModelAndView registerNewUser(@ModelAttribute @Valid User user, @ModelAttribute @Valid Person person) {
		userService.saveUserWithProfile(user, person, "Created");

		return new ModelAndView("redirect:/login");
	}
}
