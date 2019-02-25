package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

// TODO: Extract duplicate code. Fix!

/**
 * MainController manages all actions that are available to every visitor of the platform. This
 * includes basic browsing, and sign-up/login.
 */
@Controller
public class MainController {

    private final ArticleService articleService;
    private final UserService userService;

    private static final String ALL_STRING = "all";
    private static final String USER_STRING = "user";
    private static final String INDEX_STRING = "index";
    private static final String CATEGORIES_STRING = "categories";
    private static final String CATEGORY_STRING = "category";
    private final List<Category> allCategories = Category.getAllCategories();

    @Autowired
    public MainController(final UserService userService, final ArticleService articleService) {
        this.userService = userService;
        this.articleService = articleService;
    }

    @GetMapping(value = {"/", "/index"})
    public ModelAndView getIndex(final Principal principal) {
        final List<Article> allArticles = articleService.getAllActiveAndForRentalArticles();
        final User currentUser = userService.findUserByPrincipal(principal);

        final ModelAndView mav = new ModelAndView(INDEX_STRING);
        mav.addObject(ALL_STRING, allArticles);
        mav.addObject(USER_STRING, currentUser);
        mav.addObject(CATEGORIES_STRING, allCategories);
        mav.addObject(CATEGORY_STRING, ALL_STRING);
        return mav;
    }

    @GetMapping("/categories")
    public ModelAndView getIndexByCategory(final @RequestParam String category,
            final Principal principal) {
        final List<Article> allArticlesInCategory = articleService
                .getAllArticlesByCategory(Category.valueOf(category.toUpperCase(Locale.ENGLISH)));
        final User currentUser = userService.findUserByPrincipal(principal);

        final ModelAndView mav = new ModelAndView(INDEX_STRING);
        mav.addObject(ALL_STRING, allArticlesInCategory);
        mav.addObject(USER_STRING, currentUser);
        mav.addObject(CATEGORIES_STRING, allCategories);
        mav.addObject(CATEGORY_STRING, category);
        return mav;
    }

    @GetMapping("/login")
    public ModelAndView getLogin() {
        return new ModelAndView("login");
    }

    @GetMapping("/signup")
    public ModelAndView getRegistration() {
        final User user = new User();
        final Person person = new Person();

        final ModelAndView mav = new ModelAndView("registration");
        mav.addObject(USER_STRING, user);
        mav.addObject("person", person);
        return mav;
    }

    @GetMapping("/search")
    public ModelAndView getIndexBySearchString(final @RequestParam String searchString,
            final Principal principal) {
        final List<Article> allArticlesWithNameLikeSearchStr = articleService
                .getAllArticlesByName(searchString);
        final User currentUser = userService.findUserByPrincipal(principal);

        final ModelAndView mav = new ModelAndView(INDEX_STRING);
        mav.addObject(ALL_STRING, allArticlesWithNameLikeSearchStr);
        mav.addObject(USER_STRING, currentUser);
        mav.addObject(CATEGORIES_STRING, allCategories);
        mav.addObject(CATEGORY_STRING, "");
        return mav;
    }

    @PostMapping("/registerNewUser")
    public ModelAndView registerNewUser(final @ModelAttribute @Valid User user,
            final @ModelAttribute @Valid Person person) {
        userService.saveUserWithProfile(user, person, "Created");

        return new ModelAndView("redirect:/login");
    }
}
