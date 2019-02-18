package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

/**
 * MainController manages all actions that are available to every visitor of the platform. This
 * includes basic browsing, and signup/login.
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

    /**
     * TODO Javadoc
     */
    @GetMapping("/")
    public ModelAndView index(Principal principal) throws Exception {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("all", articleService.getAllNonReservedArticles());
        mav.addObject("user", userService.findUserByPrincipal(principal));
        mav.addObject("categories", Category.getAllCategories());
        return mav;
    }

    /**
     * Returns view with a filtered set of Articles.
     */
    @GetMapping("/categories")
    public ModelAndView indexByCategory(@RequestParam String category, Principal principal)
            throws Exception {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("all", articleService
                .getAllNonReservedArticlesByCategory(Category.valueOf(category.toUpperCase())));
        mav.addObject("user", userService.findUserByPrincipal(principal));
        mav.addObject("categories", Category.getAllCategories());
        return mav;
    }

    /**
     * OGIN and SIGNUP.
     */
    @GetMapping("/login")
    public ModelAndView getLogin() {
        return new ModelAndView("login");
    }

    /**
     * TODO Javadoc
     */
    @RequestMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_admin")) {
            return "redirect:/accessed/admin/index";
        } else {
            return "redirect:/accessed/user/index";
        }
    }

    /**
     * TODO Javadoc
     */
    @GetMapping("/signup")
    public ModelAndView getRegistration() {
        ModelAndView mav = new ModelAndView("registration");
        mav.addObject("user", new User());
        mav.addObject("person", new Person());
        return mav;
    }

    /**
     * TODO Javadoc
     */
    @PostMapping("/registerNewUser")
    public ModelAndView registerNewUser(@ModelAttribute @Valid User user,
            @ModelAttribute @Valid Person person) {
        userService.saveUserWithProfile(user, person, "Created");

        return new ModelAndView("redirect:/login");
    }
}
