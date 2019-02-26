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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("/article")
    public ModelAndView displayArticle(final @RequestParam("id") Long id,
            final Principal principal) {
        final Article article = articleService.findArticleById(id);
        final User currentUser = userService.findUserByPrincipal(principal);
        final ModelAndView mav = new ModelAndView("/shop/item");
        mav.addObject("review",customerReviewRepository.findAll());
        mav.addObject(ARTICLE_STRING, article);
        mav.addObject("user", currentUser);
        mav.addObject("categories", allCategories);
        return mav;
    }

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

    @PostMapping("/saveNewArticle")
    public ModelAndView saveNewCaseAndArticle(final @ModelAttribute @Valid Article article,
            BindingResult result, Model model,
            final @RequestParam("image") MultipartFile image, final Principal principal) {
        final User user = userService.findUserByPrincipal(principal);
        article.setActive(true);
        article.setOwner(user);
        article.setImage(imageService.store(image, null));
        article.setForRental(true);
        article.setActive(true);
        articleService.saveArticle(article, "Created");

        return new ModelAndView("redirect:/");
    }

    @PutMapping("/saveEditedArticle")
    public ModelAndView saveEditedArticle(final @ModelAttribute @Valid Article article,
            final @RequestParam("image") MultipartFile image, final Principal principal) {

        article.setImage(imageService.store(image, null));
        articleService.saveArticle(article, "Updated");

        final User currentUser = userService.findUserByPrincipal(principal);

        final ModelAndView mav = new ModelAndView("/shop/item");
        mav.addObject(ARTICLE_STRING, article);
        mav.addObject("user", currentUser);
        return mav;
    }

    // TODO: Warum wurde hierfür eine neue ArticleService-Methode geschrieben? Außerdem, GetMapping?
    // TODO: Warum wurde die Methode 'saveEditedArticle' nicht verwendet und angepasst?
    @RequestMapping("/updateArticle")
    public String updateArticle(final @RequestParam Long id, final Article article) {
        articleService.updateArticle(id, article);
        return "redirect:/myOverview?articles&updatedarticle";
    }

    // TODO: Warum GetMapping? RequestMapping defaulted zu GetMapping.
    @RequestMapping("/deleteArticle")
    public String deleteArticle(final @RequestParam Long id) {
        if (articleService.deactivateArticle(id)) {
            return "redirect:/myOverview?articles&deletedarticle";
        } else {
            return "redirect:/myOverview?articles&deletionfailed";
        }
    }

    //NEED FOR JS DEVE PLS DO NOT DELETE TODO: WHY IS THIS NEEDED?
    @RequestMapping("/api/events")
    @ResponseBody
    public List<LocalDate> test() {
        return caseService.findAllReservedDaysByArticle((long) 3);
    }

    @PostMapping("/bookArticle")
    public String bookArticle(final @RequestParam Long id, final String startDate,
            final String endDate,
            final Principal principal) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {
            if (caseService.requestArticle(
                    id,
                    simpleDateFormat.parse(startDate).getTime(),
                    simpleDateFormat.parse(endDate).getTime(),
                    principal.getName())) {
                return "redirect:/article?id=" + id + "&success";
            } else {
                return "redirect:/article?id=" + id + "&failed";
            }
        } catch (ParseException e) {
            LOGGER.error("Could not book article {}.", id, e);
        }

        // TODO: Show the user, whether the request was successful or not.
        return "redirect:/article?id=" + id;
    }

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

    @PostMapping("/writeReview")
    public String writeReview(final @RequestParam Long id, final CustomerReview review) {
        review.setTimestamp(new Date().getTime());
        Case opt = caseService.findCaseById(id);
        review.setAcase(opt);
        customerReviewRepository.save(review);
        caseRepository.save(review.getAcase());

        System.out.println(review);
        return "redirect:/myOverview?borrowed";
    }

    /**
     * /** TODO: Englisch? Neuschrieben! Liefert einen Methode für Springboot um das Feld
     * Article.category korrekt zu empfangen und zu verknüpfen.
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
