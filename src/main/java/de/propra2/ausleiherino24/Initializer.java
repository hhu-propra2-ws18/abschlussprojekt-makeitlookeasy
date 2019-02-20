package de.propra2.ausleiherino24;

import com.github.javafaker.Faker;
import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements ServletContextInitializer {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final PersonRepository personRepository;

    /**
     * TODO Javadoc.
     *
     * @param userRepository Descriptions
     * @param articleRepository Descriptions
     * @param caseRepository Descriptions
     * @param personRepository Descriptions
     * @param caseService Descriptions
     */
    @Autowired
    public Initializer(UserRepository userRepository, ArticleRepository articleRepository,
            PersonRepository personRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.personRepository = personRepository;
    }

    @Override
    public void onStartup(final ServletContext servletContext) {
        initTestAccounts();
        initTestArticleWithinUsers();
    }

    private void initTestArticleWithinUsers() {
        articleRepository.deleteAll();
        Faker faker = new Faker(Locale.GERMAN);
        IntStream.range(0, 15).forEach(value -> {
            Person person = createPerson(
                    faker.address().fullAddress(),
                    faker.name().firstName(),
                    faker.name().lastName());

            User user = createUser(
                    person.getFirstName() + person.getLastName() + "@mail.de",
                    faker.name().fullName(),
                    "password",
                    person);

            ArrayList<Article> articles = IntStream.range(0, faker.random().nextInt(1, 7))
                    .mapToObj(value1 -> {
                        int id = faker.random().nextInt(1, 807);
                        return createArticle(
                                readPokemonName(id),
                                faker.chuckNorris().fact(),
                                Category.getAllCategories().get(faker.random()
                                        .nextInt(0, Category.getAllCategories().size() - 1)),
                                user,
                                faker.random().nextInt(5, 500),
                                faker.random().nextInt(100, 2000),
                                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
                                        + id + ".png");
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

            personRepository.save(person);
            userRepository.save(user);
            articles.forEach(articleRepository::save);
        });
    }

    private void initTestAccounts() {
        userRepository.deleteAll();

        User user = createUser(
                "user@mail.com",
                "user",
                "password",
                createPerson(
                        "HHU",
                        "Max",
                        "Mustermann"));

        User admin = createUser(
                "useradmin@mail.com",
                "admine",
                "password",
                createPerson(
                        "HHU",
                        "Maxi",
                        "Mustermann"));

        User hans = createUser(
                "hans@mail.de",
                "Hans",
                "password",
                createPerson(
                        "HHU",
                        "Hans",
                        "Peter"));

        userRepository.save(hans);
        userRepository.save(admin);
        userRepository.save(user);
    }

    private Person createPerson(String address, String firstname, String lastname) {
        Person person = new Person();
        person.setAddress(address);
        person.setFirstName(firstname);
        person.setLastName(lastname);
        return person;
    }

    private User createUser(String email, String username, String password, Person person) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("user");
        user.setPerson(person);
        return user;
    }

    private Article createArticle(String name, String description, Category category, User owner,
            int costPerDay, int deposit, String image) {
        Article article = new Article();
        article.setActive(true);
        article.setName(name);
        article.setDescription(description);
        article.setCategory(category);
        article.setOwner(owner);
        article.setCostPerDay(costPerDay);
        article.setDeposit(deposit);
        article.setImage(image);
        return article;
    }

    private String readPokemonName(int id){
        try {
            File resource = new ClassPathResource(
                    "static/Pokemon/names/"+id+".txt").getFile();
            String name = new String(Files
                    .readAllBytes(resource.toPath()))
                    .trim();
            return name.substring(0, 1).toUpperCase() + name.substring(1, name.length()-1);
        } catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }
}
