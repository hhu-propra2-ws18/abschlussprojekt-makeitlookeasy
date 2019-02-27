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
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private static final String USER_STRING = "user";
    private static final String CATEGORIES = "categories";
    private final AccountHandler accountHandler;
    private final ArticleService articleService;
    private final CaseService caseService;
    private final UserService userService;
    private final List<Category> allCategories = Category.getAllCategories();

    @Autowired
    public UserController(final UserService userService, final ArticleService articleService,
            final AccountHandler accountHandler, final CaseService caseService) {
        this.accountHandler = accountHandler;
        this.articleService = articleService;
        this.caseService = caseService;
        this.userService = userService;
    }

    @GetMapping("/profile/{username}")
    public ModelAndView getUserProfile(final @PathVariable String username,
            final Principal principal) {

        final User visitedUser = userService.findUserByUsername(username);
        final User currentUser = userService.findUserByPrincipal(principal);
        final boolean self = principal.getName()
                .equals(username);  // Flag for ThymeLeaf. Enables certain profile editing options.

        final ModelAndView mav = new ModelAndView();
        if (self) {
            mav.setViewName("/user/profileEdit");
        } else {
            mav.setViewName("/user/profile");
        }
        mav.addObject("myArticles", articleService.findAllActiveByUser(visitedUser));
        mav.addObject("visitedUser", visitedUser);
        mav.addObject(USER_STRING, currentUser);
        mav.addObject(CATEGORIES, allCategories);
        return mav;
    }

    @PutMapping("/editProfile")
    public ModelAndView editUserProfile(final @ModelAttribute @Valid User user,
            final @ModelAttribute @Valid Person person, final Principal principal) {
        final String username = user.getUsername();
        final String currentPrincipalName = principal.getName();

        if (userService.isCurrentUser(username, currentPrincipalName)) {
            userService.saveUserWithProfile(user, person, "Updated");

            final ModelAndView mav = new ModelAndView("/user/profile");
            mav.addObject("proPayAcc", accountHandler.checkFunds(currentPrincipalName));
            mav.addObject(USER_STRING, user);
            return mav;
        } else {
            LOGGER.warn("Unauthorized access to 'editProfile' for user {} by user {}.", username,
                    currentPrincipalName);
            LOGGER.info("Logging out user {}.", currentPrincipalName);
            return new ModelAndView("redirect:/logout");
        }
    }

    @GetMapping("/myOverview")
    public ModelAndView getMyArticlePage(final Principal principal) {
        final User currentUser = userService.findUserByPrincipal(principal);
        final List<Article> myRentalArticles = articleService.findAllActiveForRental(currentUser);
        final List<Article> mySaleArticles = articleService.findAllActiveForSale(currentUser);
        final List<Case> borrowedArticles = caseService
                .getLendCasesFromPersonReceiver(currentUser.getPerson().getId());
        final List<Case> requestedArticles = caseService
                .findAllRequestedCasesByUserId(currentUser.getId());
        final List<Case> returnedArticles = caseService
                .findAllExpiredCasesByUserId(currentUser.getId());
        final List<Case> soldItems = caseService.findAllSoldItemsByUserId(currentUser.getId());

        final ModelAndView mav = new ModelAndView("/user/myOverview");
        mav.addObject(USER_STRING, currentUser);
        mav.addObject(CATEGORIES, allCategories);
        mav.addObject("myRentalArticles", myRentalArticles);
        mav.addObject("mySaleArticles", mySaleArticles);
        mav.addObject("borrowed", borrowedArticles);
        mav.addObject("returned", returnedArticles);
        mav.addObject("requested", requestedArticles);
        mav.addObject("sold", soldItems);
        return mav;
    }

    @GetMapping("/newItem")
    public ModelAndView getNewItemPage(final Principal principal) {
        final User currentUser = userService.findUserByPrincipal(principal);

        final ModelAndView mav = new ModelAndView("/shop/newItem");
        mav.addObject(CATEGORIES, allCategories);
        mav.addObject(USER_STRING, currentUser);
        mav.addObject("allArticles", articleService);
        return mav;
    }

    @GetMapping("/bankAccount")
    public ModelAndView getBankAccountPage(final Principal principal) {
        final ModelAndView mav = new ModelAndView("/user/bankAccount");
        mav.addObject(CATEGORIES, Category.getAllCategories());
        mav.addObject("transactions", caseService.findAllTransactionsFromPersonReceiver(
                userService.findUserByPrincipal(principal).getPerson().getId()));
        mav.addObject("pp", accountHandler.checkFunds(principal.getName()));
        mav.addObject("user", userService.findUserByPrincipal(principal));
        mav.addObject(USER_STRING, userService.findUserByPrincipal(principal));
        mav.addObject("allArticles", articleService);
        return mav;
    }

    @PostMapping("accessed/user/saveProfile")
    public String saveEditedUserProfile(final Principal principal, final User user,
            final Person person,
            final String password, final String confirmPass) {
        final String url = "redirect:/profile/" + principal.getName();
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

    @PostMapping("/addMoney")
    public String addMoneyToUserAccount(final Principal principal, final double money) {
        accountHandler.addFunds(principal.getName(), money);
        return "redirect:/bankAccount?success";
    }
}
