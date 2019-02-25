package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
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
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    private final Logger logger = LoggerFactory.getLogger(CaseController.class);

    private final ArticleService articleService;
    private final ImageService imageService;
    private final UserService userService;
    private final CaseService caseService;

    private static final String ARTICLE_STRING = "article";
    private final List<Category> allCategories = Category.getAllCategories();

    @Autowired
    public CaseController(ArticleService articleService,
            UserService userService,
            ImageService imageService,
            CaseService caseService) {
        this.articleService = articleService;
        this.userService = userService;
        this.imageService = imageService;
        this.caseService = caseService;
    }

    @GetMapping("/article")
    public ModelAndView displayArticle(@RequestParam("id") Long id, Principal principal) {
        Article article = articleService.findArticleById(id);
        User currentUser = userService.findUserByPrincipal(principal);

        ModelAndView mav = new ModelAndView("/shop/item");
        mav.addObject(ARTICLE_STRING, article);
        mav.addObject("user", currentUser);
        mav.addObject("categories", allCategories);
        return mav;
    }

    @GetMapping("/newArticle")
    public ModelAndView createNewCaseAndArticle(Principal principal) {
        Article article = new Article();
        User currentUser = userService.findUserByPrincipal(principal);

        ModelAndView mav = new ModelAndView("/shop/newItem");
        mav.addObject(ARTICLE_STRING, article);
        mav.addObject("user", currentUser);
        mav.addObject("categories", allCategories);
        return mav;
    }

    @PostMapping("/saveNewArticle")
    public ModelAndView saveNewCaseAndArticle(@ModelAttribute @Valid Article article,
            @RequestParam("image") MultipartFile image, Principal principal) {
        User user = userService.findUserByPrincipal(principal);
        article.setActive(true);
        article.setOwner(user);
        article.setImage(imageService.store(image, null));
        article.setForRental(true);
        article.setActive(true);
        articleService.saveArticle(article, "Created");

        return new ModelAndView("redirect:/");
    }

    @PutMapping("/saveEditedArticle")
    public ModelAndView saveEditedCaseAndArticle(@ModelAttribute @Valid Article article,
            @RequestParam("image") MultipartFile image, Principal principal) {

        article.setImage(imageService.store(image, null));
        articleService.saveArticle(article, "Updated");

        User currentUser = userService.findUserByPrincipal(principal);

        ModelAndView mav = new ModelAndView("/shop/item");
        mav.addObject(ARTICLE_STRING, article);
        mav.addObject("user", currentUser);
        return mav;
    }

    @RequestMapping("/updateArticle")
    public String updateArticle(@RequestParam Long id, Article article) {
        articleService.updateArticle(id, article);
        return "redirect:/myOverview?articles&updatedarticle";
    }

    @RequestMapping("/deleteArticle")
    public String deleteArticle(@RequestParam Long id) {
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
    public String bookArticle(@RequestParam Long id, String startDate, String endDate,
            Principal principal) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {
            caseService.requestArticle(
                    id,
                    simpleDateFormat.parse(startDate).getTime(),
                    simpleDateFormat.parse(endDate).getTime(),
                    principal.getName());
        } catch (ParseException e) {
            logger.error("Could not book article {}.", id, e);
        }

        // TODO: Show the user, whether the request was successful or not.
        return "redirect:/article?id=" + id;
    }

    @PostMapping("/accessed/user/acceptCase")
    public String acceptCase(@RequestParam Long id) {
        if (caseService.acceptArticleRequest(id)) {
            return "redirect:/myOverview?requests";
        } else {
            return "redirect:/myOverview?requests&declined";
        }
    }

    @PostMapping("/accessed/user/declineCase")
    public String declineCase(@RequestParam Long id) {
        caseService.declineArticleRequest(id);
        return "redirect:/myOverview?requests";
    }

    @PostMapping("/accessed/user/acceptCaseReturn")
    public String acceptCaseReturn(@RequestParam Long id) {
        caseService.acceptCaseReturn(id);
        return "redirect:/myOverview?returned&successfullyreturned";
    }

    /** TODO: Englisch? Neuschrieben!
     * Liefert einen Methode für Springboot um das Feld Article.category korrekt zu empfangen und zu
     * verknüpfen.
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
