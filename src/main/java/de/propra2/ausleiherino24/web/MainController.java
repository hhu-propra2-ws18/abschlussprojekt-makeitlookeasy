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

/**
 * MainController manages all actions that are available to every visitor of the platform. This
 * includes basic browsing, and sign-up/login.
 */
@Controller
public class MainController {

    private static final String INDEX_STRING = "index";
    private final ArticleService articleService;
    private final UserService userService;

    @Autowired
    public MainController(final UserService userService, final ArticleService articleService) {
        this.userService = userService;
        this.articleService = articleService;
    }

    /**
     * Displays the main page. Here you can see all articles offered by users.
     *
     * @param principal Current user.
     * @return Main page / "index.html".
     */
    @GetMapping(value = {"/", "/index"})
    public ModelAndView getIndex(final Principal principal) {
        final List<Article> allArticles = articleService.findAllActiveAndForRentalArticles();

        final ModelAndView mav = new ModelAndView(INDEX_STRING);
        addStandardModelAttributes(mav, principal, allArticles, "all");
        return mav;
    }

    /**
     * Returns the login prompt and form for visitors.
     *
     * @return Spring Security login view.
     */
    @GetMapping("/login")
    public ModelAndView getLogin() {
        return new ModelAndView("login");
    }

    /**
     * Displays user registration form. Creates new User and Person objects, that will be filled
     * with required information about the user.
     */
    @GetMapping("/signUp")
    public ModelAndView getRegistration() {
        final User user = new User();
        final Person person = new Person();

        final ModelAndView mav = new ModelAndView("registration");
        mav.addObject("user", user);
        mav.addObject("person", person);
        return mav;
    }

    /**
     * Saves new User and Person object in database after user has completed the registration
     * process. They will then be redirected to the login page.
     *
     * @param user User object with attributes parsed from HTML form.
     * @param person Person object with attributes parsed fro HTML form.
     * @return Spring security login form.
     *
     * TODO: Automate login after registration.
     */
    @PostMapping("/registerNewUser")
    public ModelAndView registerNewUser(final @ModelAttribute @Valid User user,
            final @ModelAttribute @Valid Person person) {
        userService.saveUserWithProfile(user, person, "Created");

        return new ModelAndView("redirect:/login");
    }

    /**
     * Visitors can browse all articles in database by a simple search query.
     *
     * @param searchString Article name, that the current visitor is looking for.
     * @param principal Current user.
     * @return View of all query-related articles.
     */
    @GetMapping("/search")
    public ModelAndView getIndexBySearchString(final @RequestParam String searchString,
            final Principal principal) {
        final List<Article> allArticlesWithNameLikeSearchStr = articleService
                .findAllArticlesByName(searchString);

        final ModelAndView mav = new ModelAndView(INDEX_STRING);
        addStandardModelAttributes(mav, principal, allArticlesWithNameLikeSearchStr, "");
        return mav;
    }

    /**
     * Visitors can browse all articles by category.
     *
     * @param category Category selected by the user.
     * @param principal Current user.
     * @return View of all category-related articles.
     */
    @GetMapping("/categories")
    public ModelAndView getIndexByCategory(final @RequestParam String category,
            final Principal principal) {
        final List<Article> allArticlesInCategory = articleService
                .findAllArticlesByCategory(Category.valueOf(category.toUpperCase(Locale.ENGLISH)));

        final ModelAndView mav = new ModelAndView(INDEX_STRING);
        addStandardModelAttributes(mav, principal, allArticlesInCategory, category);
        return mav;
    }

    /**
     * Extracted method to reduce code duplication.
     *
     * @param mav ModelAndView object.
     * @param principal Current user.
     * @param allArticles List of all articles queried by request.
     * @param category Category to be displayed.
     */
    private void addStandardModelAttributes(ModelAndView mav, Principal principal,
            List<Article> allArticles, String category) {
        final User currentUser = userService.findUserByPrincipal(principal);
        final List<Category> allCategories = Category.getAllCategories();

        mav.addObject("user", currentUser);
        mav.addObject("all", allArticles);
        mav.addObject("categories", allCategories);
        mav.addObject("category", category);
    }
}
