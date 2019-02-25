package de.propra2.ausleiherino24;

import com.github.javafaker.Faker;
import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.PPTransaction;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.service.ImageService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements ServletContextInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Initializer.class);

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final PersonRepository personRepository;
    private final CaseRepository caseRepository;
    private final ImageService imageService;

    private final Faker faker = new Faker(Locale.GERMAN);
    private static final String SECRET_STRING = "password";

    @Autowired
    public Initializer(final UserRepository userRepository,
            final ArticleRepository articleRepository,
            final PersonRepository personRepository, final CaseRepository caseRepository,
            final ImageService imageService) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.personRepository = personRepository;
        this.caseRepository = caseRepository;
        this.imageService = imageService;
    }

    @Override
    public void onStartup(final ServletContext servletContext) {
        deleteAll();
        final List<Person> persons = initTestArticleWithinUsers();
        persons.addAll(initTestAccounts(persons));
        addToDatabases(persons);
    }

    /**
     * Adds a list of persons and their corresponding users and all their articles and all their
     * cases to the fitting databases
     */
    private void addToDatabases(final List<Person> persons) {
        personRepository.saveAll(persons);
        persons.forEach(person -> {
            final User user = person.getUser();
            userRepository.save(user);
            if (user.getArticleList() != null) {
                user.getArticleList().forEach(article -> {
                    articleRepository.save(article);
                    if (article.getCases() != null) {
                        article.getCases().forEach(caseRepository::save);
                    }
                });
            }
        });
    }

    /**
     * Deletes all data from databases
     */
    private void deleteAll() {
        articleRepository.deleteAll();
        personRepository.deleteAll();
        caseRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * Creates 15 random users. Each with 1 to 7 articles.
     */
    private List<Person> initTestArticleWithinUsers() {
        return IntStream.range(0, 15).mapToObj(value -> {
            final Person person = createPerson(
                    faker.address().fullAddress(),
                    faker.name().firstName(),
                    faker.name().lastName());

            final User user = createUser(
                    person.getFirstName() + person.getLastName() + "@mail.de",
                    faker.name().fullName(),
                    SECRET_STRING,
                    person);

            IntStream.range(0, faker.random().nextInt(1, 7))
                    .forEach(value1 -> {
                        final int pokedexId = faker.random().nextInt(1, 807);
                        final Article article = createArticle(
                                readPokemonName(pokedexId),
                                faker.chuckNorris().fact(),
                                Category.getAllCategories().get(faker.random()
                                        .nextInt(0, Category.getAllCategories().size() - 1)),
                                user,
                                (double) faker.random().nextInt(5, 500),
                                (double) faker.random().nextInt(100, 2000),
                                storePokemonPic(pokedexId),
                                faker.address().fullAddress()
                        );
                        user.addArticle(article);
                    });

            return person;
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Creates 3 accounts for testing (hans, user, admin) and adds couple of articles and cases to
     * hans
     */
    private List<Person> initTestAccounts(final List<Person> persons) {
        final List<Person> testPersons = new ArrayList<>();
        testPersons.add(createUser(
                "user@mail.com",
                "user",
                SECRET_STRING,
                createPerson(
                        "HHU",
                        "Max",
                        "Mustermann"))
                .getPerson());

        testPersons.add(createUser(
                "useradmin@mail.com",
                "admine",
                SECRET_STRING,
                createPerson(
                        "HHU",
                        "Maxi",
                        "Mustermann"))
                .getPerson());

        final User hans = createUser(
                "hans@mail.de",
                "Hans",
                SECRET_STRING,
                createPerson(
                        "HHU",
                        "Hans",
                        "Peter"));

        IntStream.range(0, 7)
                .forEach(value1 -> {
                    final int pokedexId = faker.random().nextInt(1, 807);
                    final Article article = createArticle(
                            readPokemonName(pokedexId),
                            faker.chuckNorris().fact(),
                            Category.getAllCategories().get(faker.random()
                                    .nextInt(0, Category.getAllCategories().size() - 1)),
                            hans,
                            (double) faker.random().nextInt(5, 500),
                            (double) faker.random().nextInt(100, 2000),
                            storePokemonPic(pokedexId),
                            faker.address().fullAddress()
                    );
                    hans.addArticle(article);
                });

        hans.getArticleList().forEach(article ->
                IntStream.range(0, 2).forEach(a -> {
                    final Case aCase = createCase(
                            article,
                            persons.get(faker.random().nextInt(0, persons.size() - 1)).getUser(),
                            convertDateAsLong(
                                    faker.random().nextInt(0, 31),
                                    faker.random().nextInt(0, 11),
                                    2018),
                            convertDateAsLong(
                                    faker.random().nextInt(0, 31),
                                    faker.random().nextInt(0, 11),
                                    2019),
                            Case.REQUESTED
                    );
                    article.addCase(aCase);
                })
        );
        hans.getArticleList().forEach(article ->
                IntStream.range(0, 1).forEach(a -> {
                    final int startDay = faker.random().nextInt(0, 31);
                    final int startMonth = faker.random().nextInt(0, 11);
                    final Case aCase = createCase(
                            article,
                            persons.get(faker.random().nextInt(0, persons.size() - 1)).getUser(),
                            convertDateAsLong(
                                    startDay,
                                    startMonth,
                                    2018),
                            convertDateAsLong(
                                    startDay + faker.random().nextInt(2, 100),
                                    startMonth,
                                    2018),
                            Case.RUNNING
                    );
                    article.addCase(aCase);
                })
        );

        testPersons.add(hans.getPerson());
        return testPersons;
    }

    /**
     * Creates a person from parameters
     */
    private Person createPerson(final String address, final String firstname,
            final String lastname) {
        final Person person = new Person();
        person.setAddress(address);
        person.setFirstName(firstname);
        person.setLastName(lastname);
        return person;
    }

    /**
     * Creates an user from parameters
     */
    private User createUser(final String email, final String username, final String password,
            final Person person) {
        final User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("user");
        user.setPerson(person);
        return user;
    }

    /**
     * Creates an article from parameters
     */
    private Article createArticle(final String name, final String description,
            final Category category, final User owner,
            final Double costPerDay, final Double deposit, final String image,
            final String location) {
        final Article article = new Article();
        article.setActive(true);
        article.setName(name);
        article.setDescription(description);
        article.setCategory(category);
        article.setOwner(owner);
        article.setCostPerDay(costPerDay);
        article.setDeposit(deposit);
        article.setImage(image);
        article.setForRental(true);
        article.setLocation(location);
        return article;
    }

    /**
     * Creates a case from parameters
     */
    private Case createCase(final Article article, final User receiver, final Long starttime,
            final Long endtime,
            final int requestStatus) {
        final Case aCase = new Case();
        aCase.setReceiver(receiver);
        aCase.setPrice(article.getCostPerDay());
        aCase.setDeposit(article.getDeposit());
        aCase.setStartTime(starttime);
        aCase.setEndTime(endtime);
        aCase.setArticle(article);
        aCase.setRequestStatus(requestStatus);
        final PPTransaction ppTransaction = new PPTransaction();
        aCase.setPpTransaction(ppTransaction);
        ppTransaction.setReservationId(-1L);
        ppTransaction.setLendingCost(1D);
        return aCase;
    }

    /**
     * Reads a pokemon corresponding to given id from stored files
     */
    private String readPokemonName(final int pokedexId) {
        try {
            final File resource = new ClassPathResource(
                    "static/Pokemon/names/" + pokedexId + ".txt").getFile();
            final String name = new String(Files
                    .readAllBytes(resource.toPath()))
                    .trim();
            return name.substring(0, 1).toUpperCase(Locale.ENGLISH)
                    + name.substring(1).toLowerCase(Locale.ENGLISH);
        } catch (IOException e) {
            LOGGER.warn("Couldn't parse name of Pokémon {}.", pokedexId, e);
            LOGGER.info("Returning empty String as 'name'.");
            return "";
        }
    }

    /**
     * Stores a pokemon pic corresponding to given id using ImageService
     */
    private String storePokemonPic(final int pokedexId) {
        File file = null;
        try {
            final String fileName = "static/Pokemon/images/" + pokedexId + ".jpg";
            final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            file = new File(classLoader.getResource(fileName).getFile());
        } catch (Exception e) {
            LOGGER.warn("Couldn't parse picture of Pokémon {}.", pokedexId, e);
        }
        return imageService.storeFile(file, null);
    }

    /**
     * Converts a Date to Long
     */
    private Long convertDateAsLong(final int day, final int month, final int year) {
        return new GregorianCalendar(year, month, day).getTimeInMillis();
    }
}
