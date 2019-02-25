package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.List;
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

    private final ArticleService articleService;
    private final UserService userService;

    private static final String INDEX_STRING = "index";
    private static final String CATEGORIES_STRING = "categories";
    private static final String CATEGORY_STRING = "category";
    private final List<Category> allCategories = Category.getAllCategories();

    @Autowired
    public MainController(UserService userService, ArticleService articleService) {
        this.userService = userService;
        this.articleService = articleService;
    }

    @SuppressWarnings("Duplicates") // TODO Duplicate code
    @GetMapping(value = {"/", "/index"})
    public ModelAndView getIndex(Principal principal) {
        List<Article> allArticles = articleService.getAllActiveAndForRentalArticles();
        User currentUser = userService.findUserByPrincipal(principal);

        ModelAndView mav = new ModelAndView(INDEX_STRING);
        mav.addObject("all", allArticles);
        mav.addObject("user", currentUser);
        mav.addObject(CATEGORIES_STRING, allCategories);
        mav.addObject(CATEGORY_STRING, "all");
        return mav;
    }

    @SuppressWarnings("Duplicates") // TODO Duplicate code
    @GetMapping("/categories")
    public ModelAndView getIndexByCategory(@RequestParam String category, Principal principal) {
        List<Article> allArticlesInCategory = articleService
                .getAllArticlesByCategory(Category.valueOf(category.toUpperCase()));
        User currentUser = userService.findUserByPrincipal(principal);

        ModelAndView mav = new ModelAndView(INDEX_STRING);
        mav.addObject("all", allArticlesInCategory);
        mav.addObject("user", currentUser);
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
        User user = new User();
        Person person = new Person();

        ModelAndView mav = new ModelAndView("registration");
        mav.addObject("user", user);
        mav.addObject("person", person);
        return mav;
    }

    @GetMapping("/search")
    public ModelAndView getIndexBySearchString(@RequestParam String searchstr, Principal principal) {
        List<Article> allArticlesWithNameLikeSearchStr = articleService.getAllArticlesByName(searchstr);
        User currentUser = userService.findUserByPrincipal(principal);

        ModelAndView mav = new ModelAndView(INDEX_STRING);
        mav.addObject("all", allArticlesWithNameLikeSearchStr);
        mav.addObject("user", currentUser);
        mav.addObject(CATEGORIES_STRING, allCategories);
        mav.addObject(CATEGORY_STRING, "");
        return mav;
    }

    @PostMapping("/registerNewUser")
    public ModelAndView registerNewUser(@ModelAttribute @Valid User user,
            @ModelAttribute @Valid Person person) {
        userService.saveUserWithProfile(user, person, "Created");

        return new ModelAndView("redirect:/login");
    }
}
