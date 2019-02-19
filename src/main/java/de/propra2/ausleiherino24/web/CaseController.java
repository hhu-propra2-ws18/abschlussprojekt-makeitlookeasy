package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.ImageStoreService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Date;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
 * Manages all requests regarding creating/editing/deleting articles/cases and after-sales. Possible
 * features: transaction rating (karma/voting), chatting
 */
@Controller
public class CaseController {

	private final ArticleService articleService;
	private final UserService userService;
	private ImageStoreService imageStoreService;

    @Autowired
    public CaseController(ArticleService articleService, UserService userService, ImageStoreService imageStoreService) {
        this.articleService = articleService;
        this.userService = userService;
        this.imageStoreService = imageStoreService;
    }

    @GetMapping("/article")
    public ModelAndView displayArticle(@RequestParam("id") Long id, Principal principal)
            throws Exception {
        Article article = articleService.findArticleById(id);

        ModelAndView mav = new ModelAndView("/shop/item");
        mav.addObject("article", article);
        mav.addObject("categories", Category.getAllCategories());
        mav.addObject("user", userService.findUserByPrincipal(principal));
        return mav;
    }

    /**
     * TODO Javadoc
     */
    @GetMapping("/newArticle")
    public ModelAndView createNewCaseAndArticle(Principal principal) {
        ModelAndView mav = new ModelAndView("/shop/newItem");
        mav.addObject("article", new Article());
        mav.addObject("user", userService.findUserByPrincipal(principal));
        mav.addObject("categories", Category.getAllCategories());
        return mav;
    }

    @PostMapping("/bookArticle")
	public String bookArticle(@RequestParam Long id,
			@DateTimeFormat(pattern = "dd.MM.yyyy") Date startTime,
			@DateTimeFormat(pattern = "dd.MM.yyyy") Date endTime){
		System.out.println(startTime);
		System.out.println(endTime);
		articleService.findArticleById(id).getC
    	return "redirect:/article?id="+id;
	}

    /**
     * Creates new article in database and returns this.article's details view.
     *
     * @param article Article object from HTML form input.
     * @return Article details view.
     */
    @PostMapping("/saveNewArticle")
    public ModelAndView saveNewCaseAndArticle(
            @ModelAttribute @Valid Article article,
            BindingResult result,
            Model model,
            @RequestParam("image") MultipartFile image, Principal principal) throws Exception {

    	String currentPrincipalName = principal.getName();
    	User user = userService.findUserByUsername(currentPrincipalName);

		article.setActive(true);
    	article.setReserved(false);
    	article.setOwner(user);
        article.setImage(imageStoreService.store(image, null));
        articleService.saveArticle(article, "Created");

        //ModelAndView mav = new ModelAndView("redirect:/");
        //mav.addObject("all", articleService.getAllNonReservedArticles());
		//mav.addObject("user", userService.findUserByPrincipal(principal));
		//mav.addObject("categories", Category.getAllCategories());
        return new ModelAndView("redirect:/");
    }

	/**
	@Autowired
	public CaseController(ArticleRepository articleRepository, UserRepository userRepository,
			ArticleService articleService, UserService userService,
			ImageStoreService imageStoreService) {
		this.articleRepository = articleRepository;
		this.userRepository = userRepository;
		this.articleService = articleService;
		this.userService = userService;
		this.imageStoreService = imageStoreService;
	}

	@GetMapping("/article")
	public ModelAndView displayArticle(@RequestParam("id") Long id, Principal principal)
			throws Exception {
		Optional<Article> article = articleRepository.findById(id);
		if (!article.isPresent()) {
			throw new Exception("Article not found!");
		}
		ModelAndView mav = new ModelAndView("/accessed/user/shopitem");
		mav.addObject("article", article.get());
		mav.addObject("categories", Category.getAllCategories());
		mav.addObject("user", userService.findUserByPrincipal(principal));
		return mav;
	}


	/**
	 * Updates edited article in database and returns this.article's view.
	 *
	 * @param article Article object from HTML form input.
	 * @return Article details view.
	 */
	@PutMapping("/saveEditedArticle")
	public ModelAndView saveEditedCaseAndArticle(
			@ModelAttribute @Valid Article article,
			BindingResult result,
			Model model,
			@RequestParam("image") MultipartFile image) {

		article.setImage(imageStoreService.store(image, null));
		articleService.saveArticle(article, "Updated");

        ModelAndView mav = new ModelAndView("/shop/item");
        mav.addObject("article", article);
        return mav;
    }

	/**
	 * Deactivates a single article.
	 *
	 * @param id ID of article to be deactivated
	 * @param principal Current user
	 * @throws Exception 1. Thrown, if article couldn't be found in ArticleRepository 2. Thrown, if
	 * principal couldn't be found in UserRepository
	 * @return View "myArticles", displaying all active articles of principal
	 */
	@PutMapping("/deactivateArticle")
	public ModelAndView deactivateArticle(@RequestParam Long id, Principal principal) throws Exception {

		String currentPrincipalName = principal.getName();
		User user = userService.findUserByUsername(currentPrincipalName);

		if (!articleService.deactivateArticle(id)) {
			// TODO: Display error msg, when article deactivation fails.
		}
		ModelAndView mav = new ModelAndView("/user/myArticles");
		mav.addObject("user", user);
		mav.addObject("myArticles", articleService.findAllActiveByUser(user));
		return mav;
	}
}
