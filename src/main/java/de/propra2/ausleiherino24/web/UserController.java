package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CaseService;
import de.propra2.ausleiherino24.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * UserController manages all requests that are exclusively available to logged-in users of the
 * platform, except article/case handling. This includes profile management.
 */
@Controller
public class UserController {

	private final AccountHandler accountHandler;
	private final ArticleService articleService;
	private final CaseService caseService;
	private final UserService userService;

	private final List<Category> allCategories = Category.getAllCategories();

	@Autowired
	public UserController(UserService userService, ArticleService articleService, AccountHandler accountHandler, CaseService caseService) {
		this.accountHandler = accountHandler;
		this.articleService = articleService;
		this.caseService = caseService;
		this.userService = userService;
	}

	/**
	 * Show any user profile to logged-in users. 1.If visitor is not logged-in and tries to access
	 * profile, redirect to login. 2. Else, display profile. 2.1 If requested profile of user, is
	 * own profile, allow editing via 'self' flag. 2.2 Else, do not allow editing.
	 *
	 * @param username  Name of requested user, whose profile is requested
	 * @param principal Current Principal
	 * @return 1. Redirect to view "login" (if not logged-in) 2. Display "/user/profile"
	 * view
	 * @throws Exception Thrown, if username cannot be found in UserRepository
	 */
	@GetMapping("/profile/{username}")
	public ModelAndView getUserProfile(@PathVariable String username, Principal principal,
									   HttpServletRequest request) throws Exception {

		User visitedUser = userService.findUserByUsername(username);
		User currentUser = userService.findUserByPrincipal(principal);
		boolean self = principal.getName().equals(username);  // Flag for ThymeLeaf. Enables certain profile editing options.

		ModelAndView mav = new ModelAndView();
		if (self) {
			mav.setViewName("/user/profileEdit");
		} else {
			mav.setViewName("/user/profile");
		}
		mav.addObject("myArticles", articleService.getAllActiveArticlesbyUser(visitedUser));
		mav.addObject("visitedUser", visitedUser);
		mav.addObject("user", currentUser);
		mav.addObject("categories", allCategories);
		return mav;
		//  mav.addObject("ppAccount",
		//         accountHandler
		//                  .getAccountData(userService.findUserByPrincipal(principal).getUsername()));
	}

	/**
	 * Receives HTML form input as @Valid User and Person objects and tries to save those objects to
	 * the database.
	 *
	 * @param user      User object that is updated in database.
	 * @param person    Person object that is updated in database.
	 * @param principal Current principal.
	 * @return View "/user/profile". Displays principal's profile.
	 */
	@PutMapping("/editProfile")
	public ModelAndView editUserProfile(@ModelAttribute @Valid User user, @ModelAttribute @Valid Person person, Principal principal) {
		String username = user.getUsername();
		String currentPrincipalName = principal.getName();

		if (userService.isCurrentUser(username, currentPrincipalName)) {
			userService.saveUserWithProfile(user, person, "Updated");

			ModelAndView mav = new ModelAndView("/user/profile");
			mav.addObject("propayacc", accountHandler.checkFunds(currentPrincipalName));
			mav.addObject("user", user);
			return mav;
		} else {
			return new ModelAndView("redirect:/logout");
		}
	}


    @GetMapping("/myOverview")
    public ModelAndView getMyArticlePage (Principal principal) {
		User currentUser = userService.findUserByPrincipal(principal);
		List<Article> myArticles = articleService.findAllActiveByUser(currentUser);

		ModelAndView mav = new ModelAndView("/user/myOverview");
		mav.addObject("myArticles", myArticles);
		mav.addObject("user", currentUser);
		mav.addObject("categories", allCategories);
        mav.addObject("borrowed", caseService.getLendCasesFromPersonReceiver(currentUser.getPerson().getId()));
        mav.addObject("returned");
        return mav;
    }

	// TODO: USED ???
	/**
	 * TODO JavaDoc
	 *
	 * @param principal
	 * @return
	 */
	@GetMapping("/newItem")
	public ModelAndView getNewItemPage(Principal principal) {
		User currentUser = userService.findUserByPrincipal(principal);

		ModelAndView mav = new ModelAndView("/shop/newItem");
		mav.addObject("categories", allCategories);
		mav.addObject("user", currentUser);
		mav.addObject("allArticles", articleService);
		return mav;
	}

	/**
	 * TODO JavaDoc
	 * @param principal
	 * @return
	 */
	@GetMapping("/bankAccount")
	public ModelAndView getBankAccountPage (Principal principal) {
		ModelAndView mav = new ModelAndView("/user/bankAccount");
		mav.addObject("categories", Category.getAllCategories());
		mav.addObject("pp",accountHandler.checkFunds(principal.getName()));
		try {
			mav.addObject("user", userService.findUserByPrincipal(principal));
		} catch (Exception e) {
			e.printStackTrace();
		}
		mav.addObject("allArticles", articleService);
		return mav;
	}

	/**
	 * Saves new data if possible and redirects to same page with according param
	 * @param principal
	 * @param user
	 * @param person
	 * @param password
	 * @param confirmpass
	 * @return
	 */
	@PutMapping("/user/saveProfile")
	public String saveEditedUserProfile(Principal principal, User user, Person person,
			String password, String confirmpass){
		String url = "redirect:/profile/"+principal.getName();
		switch(userService.saveUserIfPasswordsAreEqual(principal.getName(), user, person, password, confirmpass)){
			case "PasswordNotEqual":
				return url+"?pwdonotmatch";
			case "UserNotFound":
				return url+"?usernotfound";
			case "Success":
				return url+"?success";
			default:
				return url;
		}
	}
}
