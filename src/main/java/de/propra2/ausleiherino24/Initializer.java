package de.propra2.ausleiherino24;

import com.github.javafaker.Faker;
import de.propra2.ausleiherino24.data.ArticleRepository;
import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.data.UserRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Category;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements ServletContextInitializer {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final PersonRepository personRepository;
    private final CaseRepository caseRepository;

    private final Faker faker = new Faker(Locale.GERMAN);

    /**
     * TODO Javadoc.
     *  @param userRepository Descriptions
     * @param articleRepository Descriptions
     * @param personRepository Descriptions
     * @param caseRepository
     */
    @Autowired
    public Initializer(UserRepository userRepository, ArticleRepository articleRepository,
            PersonRepository personRepository, CaseRepository caseRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.personRepository = personRepository;
        this.caseRepository = caseRepository;
    }

    @Override
    public void onStartup(final ServletContext servletContext) {
        deleteAll();
        List<Person> persons = initTestArticleWithinUsers();
        persons.addAll(initTestAccounts(persons));
        addtoDatabases(persons);
    }

    private void addtoDatabases(List<Person> persons) {
        personRepository.saveAll(persons);
        persons.forEach(person -> {
            User user = person.getUser();
            userRepository.save(user);
            if(user.getArticleList() != null) {
                user.getArticleList().forEach(article -> {
                    articleRepository.save(article);
                    if (article.getCases() != null)
                        article.getCases().forEach(caseRepository::save);
                });
            }
        });
    }

    private void deleteAll() {
        articleRepository.deleteAll();
        personRepository.deleteAll();
        caseRepository.deleteAll();
        userRepository.deleteAll();
    }

    private List<Person> initTestArticleWithinUsers() {
        return IntStream.range(0, 15).mapToObj(value -> {
            Person person = createPerson(
                    faker.address().fullAddress(),
                    faker.name().firstName(),
                    faker.name().lastName());

            User user = createUser(
                    person.getFirstName() + person.getLastName() + "@mail.de",
                    faker.name().fullName(),
                    "password",
                    person);

            IntStream.range(0, faker.random().nextInt(1, 7))
                    .forEach(value1 -> {
                        int id = faker.random().nextInt(1, 807);
                        Article article = createArticle(
                                readPokemonName(id),
                                faker.chuckNorris().fact(),
                                Category.getAllCategories().get(faker.random()
                                        .nextInt(0, Category.getAllCategories().size() - 1)),
                                user,
                                faker.random().nextInt(5, 500),
                                faker.random().nextInt(100, 2000),
                                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
                                        + id + ".png");
                        user.addArticle(article);
                    });

            return person;
        }).collect(Collectors.toCollection(ArrayList::new));


        //Fügt Cases hinzu
        /*
        persons.forEach(person -> {
            person.getUser().getArticleList().forEach(article -> {
                IntStream.range(0, faker.random().nextInt(0, 2)).forEach(a -> {
                    Case c = createCase(
                            article,
                            persons.get(randNumbExcept(0, persons.size()-1, persons.indexOf(person))).getUser(),
                            convertDateAsLong(
                                    faker.random().nextInt(1, 31),
                                    faker.random().nextInt(1, 12),
                                    2018),
                            convertDateAsLong(
                                    faker.random().nextInt(1, 31),
                                    faker.random().nextInt(1, 12),
                                    2019),
                            1
                    );
                    article.addCase(c);
                });
            });
        });
        */
    }

    private List<Person> initTestAccounts(List<Person> persons) {
        List<Person> testPersons = new ArrayList<>();
        testPersons.add(createUser(
                "user@mail.com",
                "user",
                "password",
                createPerson(
                        "HHU",
                        "Max",
                        "Mustermann"))
                .getPerson());

        testPersons.add(createUser(
                "useradmin@mail.com",
                "admine",
                "password",
                createPerson(
                        "HHU",
                        "Maxi",
                        "Mustermann"))
                .getPerson());

        User hans = createUser(
                "hans@mail.de",
                "Hans",
                "password",
                createPerson(
                        "HHU",
                        "Hans",
                        "Peter"));

        IntStream.range(0, 7)
                .forEach(value1 -> {
                    int id = faker.random().nextInt(1, 807);
                    Article article = createArticle(
                            readPokemonName(id),
                            faker.chuckNorris().fact(),
                            Category.getAllCategories().get(faker.random()
                                    .nextInt(0, Category.getAllCategories().size() - 1)),
                            hans,
                            faker.random().nextInt(5, 500),
                            faker.random().nextInt(100, 2000),
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
                                    + id + ".png");
                    hans.addArticle(article);
                });

        hans.getArticleList().forEach(article ->
            IntStream.range(0, 2).forEach(a -> {
                Case c = createCase(
                        article,
                        persons.get(faker.random().nextInt(0, persons.size() - 1)).getUser(),
                        convertDateAsLong(
                                faker.random().nextInt(1, 31),
                                faker.random().nextInt(1, 12),
                                2018),
                        convertDateAsLong(
                                faker.random().nextInt(1, 31),
                                faker.random().nextInt(1, 12),
                                2019),
                        Case.REQUESTED
                );
                article.addCase(c);
            })
        );

        testPersons.add(hans.getPerson());
        return testPersons;
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

    private Case createCase(Article article, User receiver, Long starttime, Long endtime,
            int requestStatus){
        Case c = new Case();
        c.setReceiver(receiver);
        c.setPrice(article.getCostPerDay());
        c.setDeposit(article.getDeposit());
        c.setStartTime(starttime);
        c.setEndTime(endtime);
        c.setArticle(article);
        c.setRequestStatus(requestStatus);
        return c;
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

    private int randNumbExcept(int a, int b, int z){
        int x = new Faker().random().nextInt(a, b-1);
        if(x >= z) return x+1;
        else return x;
    }

    private Long convertDateAsLong(int day, int month, int year) {
        return new GregorianCalendar(year, month, day).getTimeInMillis();
    }
}
