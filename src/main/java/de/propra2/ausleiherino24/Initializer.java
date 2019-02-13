package de.propra2.ausleiherino24;

import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;
import com.github.javafaker.Faker;

import javax.servlet.ServletContext;
import java.util.Locale;
import java.util.stream.IntStream;

@Component
public class Initializer implements ServletContextInitializer{

	private final UserRepository userRepository;
	private final ArticleRepository articleRepository;
	
	@Autowired
	public Initializer(UserRepository userRepository, ArticleRepository articleRepository) {
		this.userRepository = userRepository;
		this.articleRepository = articleRepository;
	}
	
	@Override
	public void onStartup(final ServletContext servletContext) {
		initTestUser();
		initTestArticle();
	}

	private void initTestArticle() {
		articleRepository.deleteAll();
		Faker faker = new Faker(Locale.GERMAN);
		IntStream.range(0, 15).mapToObj(value -> {
			Article article = new Article();
			article.setActive(true);
			article.setName(faker.beer().name());
			article.setDescription(String.join("\n", faker.lorem().paragraph(5)));
			return article;
		}).forEach(this.articleRepository::save);
	}

	private void initTestUser() {
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
}
