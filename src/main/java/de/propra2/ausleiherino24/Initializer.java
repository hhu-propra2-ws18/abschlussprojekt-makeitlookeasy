package de.propra2.ausleiherino24;

import com.github.javafaker.Faker;
import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.Locale;
import java.util.stream.IntStream;

@Component
public class Initializer implements ServletContextInitializer{

	private final UserRepository userRepository;
	private final ArticleRepository articleRepository;
	private final CaseRepository caseRepository;

	@Autowired
	public Initializer(UserRepository userRepository, ArticleRepository articleRepository, CaseRepository caseRepository) {
		this.userRepository = userRepository;
		this.articleRepository = articleRepository;
		this.caseRepository = caseRepository;
	}

	@Override
	public void onStartup(final ServletContext servletContext) {
		initTestAccounts();
		initTestArticleWithinUsers();
	}

	private void initTestArticleWithinUsers() {
		articleRepository.deleteAll();
		Faker faker = new Faker(Locale.GERMAN);
		IntStream.range(0, 15).mapToObj(value -> {
			User user = createUser(
					faker.funnyName().name()+"@mail.de",
					faker.funnyName().name(),
					faker.crypto().sha256());

			Article article = createArticle(
					faker.beer().name(),
					String.join("\n", faker.lorem().paragraph(5)),
					Category.AUTO,
					user);

			return createCase(
					faker.random().nextBoolean(),
					article,
					faker.random().nextInt(5, 500),
					faker.random().nextInt(100, 2000));
		}).forEach(aCase -> {
			userRepository.save(aCase.getArticle().getOwner());
			articleRepository.save(aCase.getArticle());
			caseRepository.save(aCase);
		});
	}

	private void initTestAccounts() {
		userRepository.deleteAll();

		User user = new User();
		user.setUsername("user");
		user.setPassword("password");
		user.setEmail("user@mail.com");
		user.setRole("user");

		User user2 = new User();
		user2.setUsername("admin");
		user2.setPassword("password");
		user2.setEmail("useradmin@mail.com");
		user2.setRole("admin");

		userRepository.save(user2);
		userRepository.save(user);
	}

	private User createUser(String email, String username, String password) {
		User user = new User();
		user.setEmail(email);
		user.setUsername(username);
		user.setPassword(password);
		user.setRole("user");
		return user;
	}

	private Article createArticle(String name, String description, Category category, User owner){
		Article article = new Article();
		article.setActive(true);
		article.setReserved(false);

		article.setName(name);
		article.setDescription(description);
		article.setCategory(category);
		article.setOwner(owner);
		return article;
	}

	private Case createCase(boolean activ, Article article, int price, int deposit){
		Case aCase = new Case();
		aCase.setActive(activ);
		aCase.setArticle(article);
		aCase.setPrice(price);
		aCase.setDeposit(deposit);
		return aCase;
	}
}
