package de.propra2.ausleiherino24.web;

import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ArticleService;
import de.propra2.ausleiherino24.service.CaseService;
import de.propra2.ausleiherino24.service.ImageService;
import de.propra2.ausleiherino24.service.UserService;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

/**
 * Manages all requests regarding creating/editing/deleting articles/cases and after-sales.
 * Possible features: transaction rating (karma/voting), chatting
 */
@Controller
public class CaseController {

	private final ArticleService articleService;
	private final ImageService imageService;
	private final UserService userService;
	private final CaseService caseService;

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

	/**
	 * TODO JavaDoc
	 *
	 * @param id
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/article")
	public ModelAndView displayArticle(@RequestParam("id") Long id, Principal principal) throws Exception {
		Article article = articleService.findArticleById(id);
		User currentUser = userService.findUserByPrincipal(principal);

		ModelAndView mav = new ModelAndView("/shop/item");
		mav.addObject("article", article);
		mav.addObject("user", currentUser);
		mav.addObject("categories", allCategories);
		return mav;
	}

    /**
	 * TODO JavaDoc
	 *
	 * @param principal
	 * @return
	 */
	@GetMapping("/newArticle")
	public ModelAndView createNewCaseAndArticle(Principal principal) {
		Article article = new Article();
		User currentUser = userService.findUserByPrincipal(principal);

		ModelAndView mav = new ModelAndView("/shop/newItem");
		mav.addObject("article", article);
		mav.addObject("user", currentUser);
		mav.addObject("categories", allCategories);
		return mav;
	}

	/**
	 * TODO JavaDoc
	 *
	 * @param article
	 * @param result
	 * @param model
	 * @param image
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/saveNewArticle")
	public ModelAndView saveNewCaseAndArticle(@ModelAttribute @Valid Article article, BindingResult result, Model model,
			@RequestParam("image") MultipartFile image, Principal principal) {
		User user = userService.findUserByPrincipal(principal);

		article.setOwner(user);
		article.setImage(imageService.store(image, null));
		articleService.saveArticle(article, "Created");

		return new ModelAndView("redirect:/");
	}

	/**
	 * TODO JavaDoc
	 *
	 * @param article
	 * @param result
	 * @param model
	 * @param image
	 * @param principal
	 * @return
	 */
	@PutMapping("/saveEditedArticle")
	public ModelAndView saveEditedCaseAndArticle(@ModelAttribute @Valid Article article, BindingResult result, Model model,
												 @RequestParam("image") MultipartFile image, Principal principal) {

		article.setImage(imageService.store(image, null));
		articleService.saveArticle(article, "Updated");

		User currentUser = userService.findUserByPrincipal(principal);

		ModelAndView mav = new ModelAndView("/shop/item");
		mav.addObject("article", article);
		mav.addObject("user", currentUser);
		return mav;
	}

	@PostMapping("/bookArticle")
	public String bookArticle(@RequestParam Long id, String startTime, String endTime,
							  Principal principal) throws Exception {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		try {
			caseService.requestArticle(
					id,
					simpleDateFormat.parse(startTime).getTime(),
					simpleDateFormat.parse(endTime).getTime(),
					principal.getName());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//TODO: Show the user, whether die request was successful or not
		return "redirect:/article?id="+id;
	}

	@PostMapping("/acceptCase")
	public String acceptCase(){
		return "redirect:/myOverview";
	}

	@PostMapping("/declineCase")
	public String declineCase(){
		return "redirect:/myOverview";
	}
}
