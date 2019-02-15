package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.web.CaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
	
	private final ArticleRepository articleRepository;
	private final Logger LOGGER = LoggerFactory.getLogger(CaseController.class);
	
	@Autowired
	public ArticleService(ArticleRepository articleRepository) {
		this.articleRepository = articleRepository;
	}

	/**
	 * Iterate through list of all articles. If article is not being rented, mark article as available and
	 * @return all available articles as List<>.
	 * (Possibly more efficient, if handled by SQL request (SELECT * FROM article WHERE active == TRUE AND reserved == FALSE)
	 */
	public List<Article> getAllNonReservedArticles() {
		List<Article> availableArticles = new ArrayList<>();
		List<Article> allArticles = articleRepository.findAll().isEmpty() ? new ArrayList<>() : articleRepository.findAll();
		
		for (Article article : allArticles) {
			// no duplicate, active article, not reserved
			if (!availableArticles.contains(article)
					&& article.getActive()
					&& !article.getReserved()) {
				availableArticles.add(article);
			}
		}
		
		return availableArticles;
	}
	
	/**
	 * Deactivates an article by ID to hide it from all users and prohibit further cases.
	 * Checks, if article is present by looking for ID key. If fails, throw Exception.
	 * If article is not being reserved (a.k.a bound to running case) and free to book, deactivate article.
	 * Else, throw Exception.
	 *
	 * @param id                ID of article to be "deleted".
	 * @throws Exception		1. Thrown, if article not present in ArticleRepository.
	 * 							2. Thrown, if article is reserved,
	 */
	public boolean deactivateArticle(Long id) throws Exception {
		Optional<Article> optionalArticle = articleRepository.findById(id);
		if (!optionalArticle.isPresent()) {
			LOGGER.warn("Couldn't find article %L in ArticleRepository.", id);
			throw new Exception("Couldn't find requested article in ArticleRepository.");
		}
		
		Article article = optionalArticle.get();
		
		if (article.getReserved()) {
			LOGGER.warn("Article %L couldn't be deactivated, because it's currently being reserved.", article.getId());
			return false;
		}
		
		article.setActive(false);
		articleRepository.save(article);
		LOGGER.info("Deactivated article %s [ID=%L]", article.getName(), article.getId());
		return true;
	}
	
}
