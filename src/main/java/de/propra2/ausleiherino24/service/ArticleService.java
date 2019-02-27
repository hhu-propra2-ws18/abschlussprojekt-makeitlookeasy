package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleService.class);

    private final ArticleRepository articleRepository;
    private final ImageService imageService;

    @Autowired
    public ArticleService(final ArticleRepository articleRepository,
            final ImageService imageService) {
        this.articleRepository = articleRepository;
        this.imageService = imageService;
    }

    /**
     * Saves the given article in the database.
     */
    public void saveArticle(final Article article, final String msg) {
        articleRepository.save(article);
        LOGGER.info("{} article '{}' {}.", msg, article.getName(), article.getId());
    }

    /**
     * Finds an article by its id. Throws NullPointerException in cases, the article is not present.
     */
    public Article findArticleById(final Long articleId) {
        final Optional<Article> article = articleRepository.findById(articleId);

        if (!article.isPresent()) {
            LOGGER.warn("Couldn't find article {} in UserRepository.", articleId);
            throw new NoSuchElementException("Couldn't find article in ArticleRepository.");
        }

        return article.get();
    }

    public List<Article> findAllActiveByUser(final User user) {
        return articleRepository.findAllActiveByUser(user);
    }

    /**
     * Return all articles which are active and for rental.
     */
    public List<Article> findAllActiveForRental(final User user) {
        return articleRepository.findAllActiveForRentalByUser(user);
    }

    /**
     * Returns all articles which are active and for sell.
     */
    public List<Article> findAllActiveForSale(final User user) {
        return articleRepository.findAllActiveForSaleByUser(user);
    }

    /**
     * Filters articles and checks whether they are included in given category.
     * @return all Articles, which are not reserved and are of given category
     */
    public List<Article> findAllArticlesByCategory(final Category category) {
        return findAllActiveAndForRentalArticles().stream()
                .filter(article -> article.getCategory() == category)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns all active articles.
     */
    List<Article> findAllActiveArticles() {
        return articleRepository.findAllActive().isEmpty() ? new ArrayList<>()
                : articleRepository.findAllActive();
    }

    /**
     * Finds all article which have active=true and forRental=true.
     */
    public List<Article> findAllActiveAndForRentalArticles() {
        return findAllActiveArticles()
                .stream()
                .filter(Article::isForRental)
                .collect(Collectors.toList());
    }

    /**
     * Deactivates an article by ID to hide it from all users and prohibit further cases. Checks, if
     * article is present by looking for ID key. If fails, throw Exception. If article is not being
     * reserved (a.k.a bound to running case) and free to book, deactivate article. Else, throw
     * Exception.
     * @param articleId ID of article to be "deleted".
     * @return boolean True, if succeeded. False, if encountered error while processing request.
     */
    public boolean deactivateArticle(final Long articleId) {

        final Article article = findArticleById(articleId); //optionalArticle.get();

        //only able to deactive if article has only cases where the requeststatus is
        // REQUEST_DECLINED, RENTAL_NOT_POSSIBLE or FINISHED
        if (!article.allCasesClosed()) {
            LOGGER.warn("Article {} is still reserved, lent or has an open conflict.", articleId);
            return false;
        }

        article.setActive(false);
        articleRepository.save(article);
        LOGGER.info("Deactivated article {} [ID={}]", article.getName(), articleId);
        return true;
    }

    /**
     * Updates an article given by the id with the information from given article.
     * @param articleId id for article, that is about to be updated
     * @param article new article
     */
    public void updateArticle(final Long articleId, final Article article,
            final MultipartFile image) {

        final Article originalArticle = findArticleById(articleId); //optionalArticle.get();

        originalArticle.setForRental(article.isForRental());
        originalArticle.setDeposit(article.getDeposit());
        originalArticle.setCostPerDay(article.getCostPerDay());
        originalArticle.setCategory(article.getCategory());
        originalArticle.setDescription(article.getDescription());
        originalArticle.setName(article.getName());
        if (!image.isEmpty()) {
            originalArticle.setImage(imageService.store(image, null));
        }
        saveArticle(originalArticle, "Update");
    }

    /**
     * Returns List of all articles, which name contains the given searchstring. Case of letters is ignored.
     */
    public List<Article> findAllArticlesByName(final String searchString) {
        return articleRepository.findByActiveTrueAndNameContainsIgnoreCase(searchString);
    }

    /**
     * Sets forSale-flag in article and saves in database.
     *
     * @param articleId article that is modified
     * @param status value to set
     */
    void setSellStatusFromArticle(final Long articleId, final boolean status) {
        final Article article = findArticleById(articleId);
        article.setForSale(status);
        articleRepository.save(article);
    }
}
