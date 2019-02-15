package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.RoleService;
import de.propra2.ausleiherino24.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

/**
 * 	UserController manages all requests that are exclusively available to logged-in users
 * 	of the platform, except article/case handling. This includes profile management.
 */
@Controller
@RequestMapping("/accessed/user")
public class UserController {
	private final UserService userService;
	private final ArticleService articleService;
	
	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	private final AccountHandler accountHandler;

	@Autowired
	public UserController(UserService userService, ArticleService articleService, AccountHandler accountHandler) {
		this.userService = userService;
		this.articleService = articleService;
		this.accountHandler = accountHandler;
	}
	
	@GetMapping("/index")
	public ModelAndView getIndex(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("/index");
		mav.addObject("all", articleService.getAllNonReservedArticles());
		mav.addObject("role", RoleService.getUserRole(request));
		mav.addObject("categories", Category.getAllCategories());
		mav.addObject("role", RoleService.getUserRole(request));
		return mav;
	}
	
	/**
	 * Show any user profile to logged-in users.
	 * 1.	If visitor is not logged-in and tries to access profile, redirect to login.
	 * 2.	Else, display profile.
	 * 		2.1		If requested profile of user, is own profile, allow editing via 'self' flag.
	 * 		2.2		Else, do not allow editing.
	 *
	 * @param username		Name of requested user, whose profile is requested
	 * @param principal		Current Principal
	 * @return				1. Redirect to view "login" (if not logged-in)
	 * 						2. Display "/accessed/user/profile" view
	 * @throws Exception	Thrown, if username cannot be found in UserRepository
	 */
	@GetMapping("/profile/{username}")
	public ModelAndView displayUserProfile(@PathVariable String username, Principal principal, HttpServletRequest request) throws Exception {
		if (principal == null) {
			// System.out.println("You have to be logged in to see other users' profiles.");
			return new ModelAndView("redirect:/login");
		}

		User user = userService.findUserByUsername(username);
		boolean self = principal.getName().equals(username);	// Flag for ThymeLeaf. Enables certain profile editing options.

		ModelAndView mav = new ModelAndView("/accessed/user/profile");
		mav.addObject("articles",articleService.getAllNonReservedArticlesByUser(user));
		mav.addObject("categories", Category.getAllCategories());
		mav.addObject("user", user);
		mav.addObject("role", RoleService.getUserRole(request));
		mav.addObject("self", self);
		mav.addObject("allArticles", articleService);
		return mav;
	}
	
	/**
	 * Receives HTML form input as @Valid User and Person objects and tries to save those objects to the database.
	 *
	 * @param user			User object that is updated in database.
	 * @param person		Person object that is updated in database.
	 * @param principal		Current principal.
	 * @return				View "/accessed/user/profile". Displays principal's profile.
	 */
	@PutMapping("/editProfile")
	public ModelAndView editUserProfile(@ModelAttribute @Valid User user, @ModelAttribute @Valid Person person, Principal principal) {
		String currentPrincipalName = principal.getName();

		if (user.getUsername().equals(currentPrincipalName)) {
			userService.saveUserWithProfile(user, person, "Updated");
		} else {
			LOGGER.warn("Unauthorized access to 'editProfile' for user %s by user %s", user.getUsername(), currentPrincipalName);
			LOGGER.info("Logging out user %s", currentPrincipalName);
			return new ModelAndView("redirect:/logout");
		}

		ModelAndView mav = new ModelAndView("/accessed/user/profile");
		mav.addObject("propayacc",accountHandler.checkFunds(user.getUsername()));
		mav.addObject("user", user);
		return mav;
	}

}
