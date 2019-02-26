package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.CustomerReviewRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.CustomerReview;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CaseService;
import de.propra2.ausleiherino24.service.ImageService;
import de.propra2.ausleiherino24.service.UserService;
import java.beans.PropertyEditorSupport;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

// TODO: Exatrct duplicate code. Fix!

@Controller
public class CaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseController.class);

    private final ArticleService articleService;
    private final ImageService imageService;
    private final UserService userService;
    private final CaseService caseService;
    private final CustomerReviewRepository customerReviewRepository;
    private final CaseRepository caseRepository;

    private static final String ARTICLE_STRING = "article";
    private final List<Category> allCategories = Category.getAllCategories();

    /**
     * Autowired constructor.
     */
    @Autowired
    public CaseController(final ArticleService articleService,
            final UserService userService,
            final ImageService imageService,
            final CaseService caseService,
            final CustomerReviewRepository customerReviewRepository,
            final CaseRepository caseRepository) {
        this.articleService = articleService;
        this.userService = userService;
        this.imageService = imageService;
        this.caseService = caseService;
        this.customerReviewRepository = customerReviewRepository;
        this.caseRepository = caseRepository;
    }

    /**
     * Mapping for article view.
     *
     * @param id articleId
     */
    @GetMapping("/article")
    public ModelAndView displayArticle(final @RequestParam("id") Long id,
            final Principal principal) {
        final Article article = articleService.findArticleById(id);
        final User currentUser = userService.findUserByPrincipal(principal);
        final ModelAndView mav = new ModelAndView("/shop/item");
        mav.addObject("review", customerReviewRepository.findAll());
        mav.addObject(ARTICLE_STRING, article);
        mav.addObject("user", currentUser);
        mav.addObject("categories", allCategories);
        return mav;
    }

    /**
     * Mapping for creating a new article.
     */
    @GetMapping("/newArticle")
    public ModelAndView createNewCaseAndArticle(final Principal principal) {
        final Article article = new Article();
        final User currentUser = userService.findUserByPrincipal(principal);

        final ModelAndView mav = new ModelAndView("/shop/newItem");
        mav.addObject(ARTICLE_STRING, article);
        mav.addObject("user", currentUser);
        mav.addObject("categories", allCategories);
        return mav;
    }

    /**
     * Saves a new article. Therefor the imageService stores the image and all article parameters
     * are set fitting
     *
     * @param article article to save
     * @param image image of the article
     * @param result must not be deleted, even though there is no obvious use. Otherwise you cannot
     *     create an article without a picture
     */
    @PostMapping("/saveNewArticle")
    public ModelAndView saveNewCaseAndArticle(final @ModelAttribute @Valid Article article,
            BindingResult result, final @RequestParam("image") MultipartFile image,
            final Principal principal) {
        final User user = userService.findUserByPrincipal(principal);
        article.setActive(true);
        article.setOwner(user);
        if (image != null) {
            article.setImage(imageService.store(image, null));
        }
        article.setForRental(true);
        article.setActive(true);
        articleService.saveArticle(article, "Created");

        return new ModelAndView("redirect:/");
    }

    /**
     * Updates an article.
     * @return redirect: /myOverview
     */
    @PostMapping("/updateArticle")
    public String saveEditedArticle(final @RequestParam Long id,
            final @ModelAttribute @Valid Article article, BindingResult result,
            final @RequestParam("image") MultipartFile image) {

        articleService.updateArticle(id, article, image);
        return "redirect:/myOverview?articles&updatedarticle";
    }

    /**
     * Deletes an article.
     * @param id articleId
     * @return redirect: /myOverview
     */
    @PostMapping("/deleteArticle")
    public String deleteArticle(final @RequestParam Long id) {
        if (articleService.deactivateArticle(id)) {
            return "redirect:/myOverview?articles&deletedarticle";
        } else {
            return "redirect:/myOverview?articles&deletionfailed";
        }
    }

    /**
     * Method is needed, so that the calender shows, on which days the article is already reserved.
     */
    @RequestMapping("/api/events")
    @ResponseBody
    public List<LocalDate> test() {
        return caseService.findAllReservedDaysByArticle((long) 3);
    }

    /**
     * Books an article. Calls caseService.requestArticle
     * @return redirect: /article?id
     */
    @PostMapping("/bookArticle")
    public String bookArticle(final @RequestParam Long id, final String startDate,
            final String endDate,
            final Principal principal) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String url = "redirect:/article?id=" + id;

        try {
            if (caseService.requestArticle(
                    id,
                    simpleDateFormat.parse(startDate).getTime(),
                    simpleDateFormat.parse(endDate).getTime(),
                    principal.getName())) {
                return url + "&success";
            } else {
                return url + "&failed";
            }
        } catch (ParseException e) {
            LOGGER.error("Could not book article {}.", id, e);
        }

        // TODO: Show the user, whether the request was successful or not.
        return url;
    }

    /**
     * Mapping for user to accept an request.
     * @param id caseId
     * @return Redirects to myOverview with fitting warning.
     */
    @PostMapping("/acceptCase")
    public String acceptCase(final @RequestParam Long id) {
        switch (caseService.acceptArticleRequest(id)) {
            case 1:
                return "redirect:/myOverview?requests";
            case 2:
                return "redirect:/myOverview?requests&alreadyrented";
            case 3:
                return "redirect:/myOverview?requests&receiveroutofmoney";
            default:
                return "redirect:/myOverview?requests&error";
        }
    }

    @PostMapping("/declineCase")
    public String declineCase(final @RequestParam Long id) {
        caseService.declineArticleRequest(id);
        return "redirect:/myOverview?requests&declined";
    }

    @PostMapping("/acceptCaseReturn")
    public String acceptCaseReturn(final @RequestParam Long id) {
        caseService.acceptCaseReturn(id);
        return "redirect:/myOverview?returned&successfullyreturned";
    }

    /**
     * Mapping for creating an review.
     * @param id caseId
     * @param review object
     * @return redirect: /myOverview with fitting param
     */
    @PostMapping("/writeReview")
    public String writeReview(final @RequestParam Long id, final CustomerReview review) {
        review.setTimestamp(new Date().getTime());
        Case opt = caseService.findCaseById(id);
        review.setAcase(opt);
        customerReviewRepository.save(review);
        caseRepository.save(review.getAcase());
        return "redirect:/myOverview?borrowed";
    }

    /**
     * Provides a for springboot method to correctly receive and connect Article.category.
     */
    @InitBinder
    public void initBinder(final WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(Category.class, new CategoryConverter());
    }

    private static class CategoryConverter extends PropertyEditorSupport {

        @Override
        public void setAsText(final String text) {
            setValue(Category.fromValue(text));
        }
    }
}
