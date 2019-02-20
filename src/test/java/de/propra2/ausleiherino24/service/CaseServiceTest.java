package de.propra2.ausleiherino24.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class CaseServiceTest {

    private CaseRepository caseRepositoryMock;
    private PersonRepository personRepositoryMock;
    private ArticleService articleServiceMock;
    private UserService userServiceMock;
    private CaseService caseService;
    private ArrayList<Case> cases;

    @Before
    public void setUp() {
        caseRepositoryMock = mock(CaseRepository.class);
        personRepositoryMock = mock(PersonRepository.class);
        articleServiceMock = mock(ArticleService.class);
        userServiceMock = mock(UserService.class);
        caseService = new CaseService(caseRepositoryMock, personRepositoryMock, articleServiceMock,
                userServiceMock);
        cases = new ArrayList<>();
    }

    @Test
    public void OwnerWithThreeCases() {
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));

        when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
        Optional<Person> o = Optional.of(new Person());
        when(personRepositoryMock.findById(0L)).thenReturn(o);

        assertEquals(cases, caseService.getAllCasesFromPersonOwner(0L));
    }

    @Test
    public void OwnerWithThreeCases2() {
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));
        cases.add(new Case(null, 1L, null, null, 0, 0, 0, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));

        when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
        Optional<Person> o = Optional.of(new Person());
        when(personRepositoryMock.findById(0L)).thenReturn(o);
        cases.remove(2);

        assertEquals(cases, caseService.getAllCasesFromPersonOwner(0L));
    }

    @Test
    public void OwnerWithTwoLendCases() {
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, new User(), null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, new User(), null));
        when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
        Optional<Person> o = Optional.of(new Person());
        when(personRepositoryMock.findById(0L)).thenReturn(o);
        cases.remove(2);
        cases.remove(0);

        assertEquals(cases, caseService.getLendCasesFromPersonOwner(0L));
    }

    @Test
    public void OwnerWithNoLendCases() {
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));

        when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
        Optional<Person> o = Optional.of(new Person());
        when(personRepositoryMock.findById(0L)).thenReturn(o);

        assertTrue(caseService.getLendCasesFromPersonOwner(0L).isEmpty());
    }

    @Test
    public void OwnerWithTwoFreeCases() {
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, new User(), null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, new User(), null));
        when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
        Optional<Person> o = Optional.of(new Person());
        when(personRepositoryMock.findById(0L)).thenReturn(o);
        cases.remove(3);
        cases.remove(1);

        assertEquals(cases, caseService.getFreeCasesFromPersonOwner(0L));
    }

    @Test
    public void OwnerWithNoFreeCases() {
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, new User(), null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, new User(), null));
        cases.add(new Case(null, 0L, null, null, 0, 0, 0, null, new User(), null));

        when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
        Optional<Person> o = Optional.of(new Person());
        when(personRepositoryMock.findById(0L)).thenReturn(o);

        assertTrue(caseService.getFreeCasesFromPersonOwner(0L).isEmpty());
    }

    @Test
    public void saveNewArticleCase() {
        Article article = new Article();
        Case c = new Case();
        c.setArticle(article);
        c.setPrice(0);
        c.setDeposit(10);

        caseService.addCaseForNewArticle(article, 0, 10);

        verify(caseRepositoryMock).save(c);
    }

    @Test
    public void requestArticle() throws Exception {
        Long articleId = 0L, st = 5L, et = 10L;
        String username = "";
        Article article = new Article();
        article.setDeposit(100);
        article.setCostPerDay(50);
        when(articleServiceMock.findArticleById(articleId)).thenReturn(article);
        when(userServiceMock.findUserByUsername(username)).thenReturn(new User());
        ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);

        caseService.requestArticle(articleId, st, et, username);

        verify(caseRepositoryMock).save(argument.capture());
        assertEquals(st, argument.getValue().getStartTime());
        assertEquals(et, argument.getValue().getEndTime());
        assertEquals(article, argument.getValue().getArticle());
        assertEquals(new User(), argument.getValue().getReceiver());
        assertEquals(100, argument.getValue().getDeposit());
        assertEquals(50, argument.getValue().getPrice());
    }

    @Test(expected = Exception.class)
    public void requestArticleCatchException() throws Exception {
        Long articleId = 0L, st = 5L, et = 10L;
        String username = "";
        when(articleServiceMock.findArticleById(articleId)).thenReturn(null);
        when(userServiceMock.findUserByUsername(username)).thenReturn(new User());

        caseService.requestArticle(articleId, st, et, username);

        verify(caseRepositoryMock, times(0)).save(any());
    }
}
