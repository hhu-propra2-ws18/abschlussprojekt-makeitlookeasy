package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CaseService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final AccountHandler accountHandler;
    private final ArticleService articleService;
    private final CaseService caseService;
    private final UserService userService;

    private static final String CATEGORIES = "categories";
    private final List<Category> allCategories = Category.getAllCategories();

    @Autowired
    public UserController(UserService userService, ArticleService articleService,
            AccountHandler accountHandler, CaseService caseService) {
        this.accountHandler = accountHandler;
        this.articleService = articleService;
        this.caseService = caseService;
        this.userService = userService;
    }

    @GetMapping("/profile/{username}")
    public ModelAndView getUserProfile(@PathVariable String username, Principal principal) {

        User visitedUser = userService.findUserByUsername(username);
        User currentUser = userService.findUserByPrincipal(principal);
        boolean self = principal.getName()
                .equals(username);  // Flag for ThymeLeaf. Enables certain profile editing options.

        ModelAndView mav = new ModelAndView();
        if (self) {
            mav.setViewName("/user/profileEdit");
        } else {
            mav.setViewName("/user/profile");
        }
        mav.addObject("myArticles", articleService.findAllActiveByUser(visitedUser));
        mav.addObject("visitedUser", visitedUser);
        mav.addObject("user", currentUser);
        mav.addObject(CATEGORIES, allCategories);
        return mav;
    }

    @PutMapping("/editProfile")
    public ModelAndView editUserProfile(@ModelAttribute @Valid User user,
            @ModelAttribute @Valid Person person, Principal principal) {
        String username = user.getUsername();
        String currentPrincipalName = principal.getName();

        if (userService.isCurrentUser(username, currentPrincipalName)) {
            userService.saveUserWithProfile(user, person, "Updated");

            ModelAndView mav = new ModelAndView("/user/profile");
            mav.addObject("proPayAcc", accountHandler.checkFunds(currentPrincipalName));
            mav.addObject("user", user);
            return mav;
        } else {
            logger.warn("Unauthorized access to 'editProfile' for user {} by user {}.", username,
                    currentPrincipalName);
            logger.info("Logging out user {}.", currentPrincipalName);
            return new ModelAndView("redirect:/logout");
        }
    }

    @RequestMapping("/myOverview")
    public ModelAndView getMyArticlePage(Principal principal) {
        User currentUser = userService.findUserByPrincipal(principal);
        List<Article> myArticles = articleService.findAllActiveByUser(currentUser);
        List<Case> borrowedArticles = caseService
                .getLendCasesFromPersonReceiver(currentUser.getPerson().getId());
        List<Case> requestedArticles = caseService
                .findAllRequestedCasesbyUserId(currentUser.getId());
        List<Case> returnedArticles = caseService.findAllExpiredCasesbyUserId(currentUser.getId());

        ModelAndView mav = new ModelAndView("/user/myOverview");
        mav.addObject("user", currentUser);
        mav.addObject(CATEGORIES, allCategories);
        mav.addObject("myArticles", myArticles);
        mav.addObject("borrowed", borrowedArticles);
        mav.addObject("returned", returnedArticles);
        mav.addObject("requested", requestedArticles);
        return mav;
    }

    @GetMapping("/newItem")
    public ModelAndView getNewItemPage(Principal principal) {
        User currentUser = userService.findUserByPrincipal(principal);

        ModelAndView mav = new ModelAndView("/shop/newItem");
        mav.addObject(CATEGORIES, allCategories);
        mav.addObject("user", currentUser);
        mav.addObject("allArticles", articleService);
        return mav;
    }

    @GetMapping("/bankAccount")
    public ModelAndView getBankAccountPage(Principal principal) {
        ModelAndView mav = new ModelAndView("/user/bankAccount");
        mav.addObject(CATEGORIES, Category.getAllCategories());
        mav.addObject("pp", accountHandler.checkFunds(principal.getName()));
        mav.addObject("user", userService.findUserByPrincipal(principal));
        mav.addObject("allArticles", articleService);
        return mav;
    }

    @PostMapping("accessed/user/saveProfile")
    public String saveEditedUserProfile(Principal principal, User user, Person person,
            String password, String confirmPass) {
        String url = "redirect:/profile/" + principal.getName();
        switch (userService.saveUserIfPasswordsAreEqual(principal.getName(), user, person, password,
                confirmPass)) {
            case "PasswordNotEqual":
                return url + "?pwdDoNotMatch";
            case "UserNotFound":
                return url + "?userNotFound";
            case "Success":
                return url + "?success";
            default:
                return url;
        }
    }
}
