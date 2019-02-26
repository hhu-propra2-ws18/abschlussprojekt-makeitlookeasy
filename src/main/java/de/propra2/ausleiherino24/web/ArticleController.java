package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.CustomerReview;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CustomerReviewService;
import de.propra2.ausleiherino24.service.ImageService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * 'ArticleController' manages all requests related to article creation, updates, and deactivation.
 */
@Controller
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;
    private final ImageService imageService;
    private final UserService userService;
    private final CustomerReviewService customerReviewService;

    private final List<Category> allCategories = Category.getAllCategories();

    @Autowired
    public ArticleController(final ArticleService articleService, final UserService userService,
            final ImageService imageService, final CustomerReviewService customerReviewService) {
        this.articleService = articleService;
        this.userService = userService;
        this.imageService = imageService;
        this.customerReviewService = customerReviewService;
    }

    /**
     * Displays all information available for requested Article object.
     *
     * @param id ID of Article object, which will be displayed.
     * @param principal Current user.
     * @return Specific article view.
     */
    @GetMapping("")
    public ModelAndView displayArticle(final @RequestParam("id") Long id,
            final Principal principal) {
        final Article article = articleService.findArticleById(id);
        final List<CustomerReview> allReviews = customerReviewService.findAllReviews();

        final ModelAndView mav = new ModelAndView("/shop/item");
        addStandardModelAttributes(mav, principal, article);
        mav.addObject("categories", allCategories);
        mav.addObject("review", allReviews);
        return mav;
    }

    /**
     * Creates a new Article object, feeds it to Model and lets Principal fill-in information about
     * Principal's article, that they want to offer.
     *
     * @param principal Current user.
     * @return Article creation view.
     */
    @GetMapping("/create")
    public ModelAndView createNewArticle(final Principal principal) {
        final Article article = new Article();

        final ModelAndView mav = new ModelAndView("/shop/newItem");
        addStandardModelAttributes(mav, principal, article);
        mav.addObject("categories", allCategories);
        return mav;
    }

    /**
     * Saves newly created and edited Article object to database after setting non-user-adjustable
     * attributes.
     *
     * @param article Article object.
     * @param result must not be deleted, even though there is no obvious use. Otherwise you cannot
     * create an article without a picture
     * @param image Image, that the user uploaded to be displayed.
     * @param principal Current user.
     * @return Redirects the user to the main page aka index.html.
     */
    @PostMapping("/saveNew")
    public ModelAndView saveNewArticle(final @ModelAttribute @Valid Article article,
            BindingResult result, Model model, final @RequestParam("image") MultipartFile image,
            final Principal principal) {
        final User user = userService.findUserByPrincipal(principal);

        article.setActive(true);
        article.setOwner(user);
        article.setImage(imageService.store(image, null));
        article.setForRental(true);
        article.setForSale(false);

        if (image != null) {
            article.setImage(imageService.store(image, null));
        }
        articleService.saveArticle(article, "Created");

        return new ModelAndView("redirect:/");
    }

    /**
     * Mapping for save a new Article which will be sold. If you want to you can try to make on
     * mapping out of this and saveNewArticle
     */
    @PostMapping("/saveNewToSell")
    public ModelAndView saveNewCaseAndSellArticle(final @ModelAttribute @Valid Article article,
            BindingResult result, Model model, final @RequestParam("image") MultipartFile image,
            final Principal principal) {
        final User user = userService.findUserByPrincipal(principal);

        article.setActive(true);
        article.setOwner(user);
        article.setForRental(true);

        if (image != null) {
            article.setImage(imageService.store(image, null));
        }
        article.setForSale(true);

        articleService.saveArticle(article, "Created");
        return new ModelAndView("redirect:/");
    }

    /**
     * Saves changes, authored by Principal, to already persistent Article object.
     *
     * @param article Article object, that has been updated.
     * @return Redirects the user to their personal article overview.
     */
    @PutMapping("/saveUpdated")
    public String updateArticle(final Article article) {
        articleService.saveArticle(article, "Updated");

        return "redirect:/myOverview?articles&updatedArticle";
    }

    /**
     * Deactivates a specific article, that Principal offers, and tells them, that it has been
     * deleted.
     *
     * @param id ID of Article object, which will be deactivated.
     * @return Redirects the user to their personal article overview.
     */
    @PutMapping("/deactivate")
    public String deactivateArticle(final @RequestParam Long id) {
        if (articleService.deactivateArticle(id)) {
            return "redirect:/myOverview?articles&deactivatedArticle";
        } else {
            return "redirect:/myOverview?articles&deactivationFailed";
        }
    }

    /**
     * Extracted method to reduce code duplication.
     *
     * @param mav ModelAndView object.
     * @param principal Current user.
     * @param article Article object.
     */
    private void addStandardModelAttributes(ModelAndView mav, Principal principal,
            Article article) {
        final User currentUser = userService.findUserByPrincipal(principal);

        mav.addObject("user", currentUser);
        mav.addObject("article", article);
    }

}
