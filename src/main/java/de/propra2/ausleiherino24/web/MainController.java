package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.RoleService;
import de.propra2.ausleiherino24.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 	MainController manages all actions that are available to every visitor of the platform.
 * 	This includes basic browsing, and signup/login.
 */
@Controller
public class MainController {

	private final UserService userService;
	private final ArticleService articleService;

	@Autowired
	public MainController(UserService userService, ArticleService articleService) {
		this.userService = userService;
		this.articleService = articleService;
	}
	
	@GetMapping("/")
	public ModelAndView index(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("index");
		mav.addObject("all", articleService.getAllNonReservedArticles());
		mav.addObject("role", RoleService.getUserRole(request));
		mav.addObject("categories", Category.getAllCategories());
		return mav;
	}
	
	/** LOGIN && SIGNUP **/
	@GetMapping("/login")
	public ModelAndView getLogin(){
		return new ModelAndView("login");
	}

	@RequestMapping("/default")
	public String defaultAfterLogin(HttpServletRequest request) {
		if (request.isUserInRole("ROLE_admin"))
			return "redirect:/accessed/admin/index";
		else
			return "redirect:/accessed/user/index";
	}
	
	@GetMapping("/signup")
	public ModelAndView getRegistration(){
		ModelAndView mav = new ModelAndView("registration");
		mav.addObject("user", new User());
		mav.addObject("person", new Person());
		return mav;
	}
	
	@PostMapping("/registerNewUser")
	public ModelAndView registerNewUser(@ModelAttribute @Valid User user, @ModelAttribute @Valid Person person){
		userService.saveUserWithProfile(user,person, "Created");
		
		return new ModelAndView("redirect:/login");
	}
}
