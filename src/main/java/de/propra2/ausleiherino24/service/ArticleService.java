package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public void saveArticle(Article article, String msg) {
        articleRepository.save(article);
        LOGGER.info("%s article '%s' [ID=%L]", msg, article.getName(), article.getId());
    }

    public Article findArticleById(Long id) throws Exception {
        Optional<Article> article = articleRepository.findById(id);

        if (!article.isPresent()) {
            LOGGER.warn("Couldn't find article %L in UserRepository.", id);
            throw new Exception("Couldn't find article in ArticleRepository.");
        }

        return article.get();
    }

    public ArrayList<Article> findAllActiveByUser(User user) {
        return articleRepository.findAllActiveByUser(user);
    }

    /**
     * Filters articles and checks whether they are included in given category.
     *
     * @return all Articles, which are not reserved and are of given category
     */
    public List<Article> getAllArticlesByCategory(Category category) {
        return getAllActiveAndForRentalArticles().stream()
                .filter(article -> article.getCategory() == category)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Article> getAllActiveArticles() {
        return articleRepository.findAllActive().isEmpty() ? new ArrayList<>()
                : articleRepository.findAllActive();
    }

    //Todo test
    public List<Article> getAllActiveAndForRentalArticles() {
        return getAllActiveArticles()
                .stream()
                .filter(Article::isForRental)
                .collect(Collectors.toList());
    }

    /**
     * Deactivates an article by ID to hide it from all users and prohibit further cases. Checks, if
     * article is present by looking for ID key. If fails, throw Exception. If article is not being
     * reserved (a.k.a bound to running case) and free to book, deactivate article. Else, throw
     * Exception.
     *
     * @param id ID of article to be "deleted".
     * @return boolean True, if succeeded. False, if encountered error while processing request.
     * @throws Exception 1. Thrown, if article not present in ArticleRepository. 2. Thrown, if article is reserved.
     */
    public boolean deactivateArticle(Long id) throws Exception {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        if (!optionalArticle.isPresent()) {
            LOGGER.warn("Couldn't find article %L in ArticleRepository.", id);
            throw new Exception("Couldn't find requested article in ArticleRepository.");
        }

        Article article = optionalArticle.get();

        //only able to deactive if article has only cases where the requeststatus is REQUEST_DECLINED, RENTAL_NOT_POSSIBLE or FINISHED
        if(!article.isForRental()){
            LOGGER.warn("Article %L is still reserved, lent or has an open conflict.", id);
            return false;
        }

        article.setActive(false);
        articleRepository.save(article);
        LOGGER.info("Deactivated article %s [ID=%L]", article.getName(), article.getId());
        return true;
    }

    public void updateArticle(Long id, Article article) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        if(!optionalArticle.isPresent())
            return;
        Article oldArticle = optionalArticle.get();
        oldArticle.setForRental(article.isForRental());
        oldArticle.setDeposit(article.getDeposit());
        oldArticle.setCostPerDay(article.getCostPerDay());
        oldArticle.setCategory(article.getCategory());
        oldArticle.setDescription(article.getDescription());
        oldArticle.setName(article.getName());
        articleRepository.save(oldArticle);
    }
}
