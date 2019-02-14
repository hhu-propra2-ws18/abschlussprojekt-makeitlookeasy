package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleService {
	
	private final ArticleRepository articleRepository;
	
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
		List<Article> allArticles = articleRepository.findAll().isEmpty() ? articleRepository.findAll() : new ArrayList<>();
		
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

}
