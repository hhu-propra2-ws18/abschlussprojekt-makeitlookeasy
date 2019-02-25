package de.propra2.ausleiherino24.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.PPTransaction;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import de.propra2.ausleiherino24.propayhandler.ReservationHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class CaseServiceTest {

    private CaseRepository caseRepositoryMock;
    private PersonRepository personRepositoryMock;
    private ArticleService articleServiceMock;
    private UserService userServiceMock;
    private CaseService caseService;
    private AccountHandler accountHandlerMock;
    private ReservationHandler reservationHandlerMock;
    private ArrayList<Case> cases;

    @Before
    public void setUp() {
        accountHandlerMock = mock(AccountHandler.class);
        reservationHandlerMock = mock(ReservationHandler.class);
        caseRepositoryMock = mock(CaseRepository.class);
        personRepositoryMock = mock(PersonRepository.class);
        articleServiceMock = mock(ArticleService.class);
        userServiceMock = mock(UserService.class);
        caseService = new CaseService(caseRepositoryMock, personRepositoryMock, articleServiceMock,
                userServiceMock, accountHandlerMock, reservationHandlerMock);
        cases = new ArrayList<>();
    }

    @Test
    public void ownerWithThreeCases() {
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));

        when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
        Optional<Person> o = Optional.of(new Person());
        when(personRepositoryMock.findById(0L)).thenReturn(o);

        assertEquals(cases, caseService.getAllCasesFromPersonOwner(0L));
    }

    @Test
    public void ownerWithThreeCases2() {
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));
        cases.add(new Case(null, 1L, null, null, 0D, 0D, 0, null, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));

        when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
        Optional<Person> o = Optional.of(new Person());
        when(personRepositoryMock.findById(0L)).thenReturn(o);
        cases.remove(2);

        assertEquals(cases, caseService.getAllCasesFromPersonOwner(0L));
    }

    @Test
    public void ownerWithTwoLendCases() {
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, new User(), null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, new User(), null, null));
        when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
        Optional<Person> o = Optional.of(new Person());
        when(personRepositoryMock.findById(0L)).thenReturn(o);
        cases.remove(2);
        cases.remove(0);

        assertEquals(cases, caseService.getLendCasesFromPersonOwner(0L));
    }

    @Test
    public void ownerWithNoLendCases() {
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));

        when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
        Optional<Person> o = Optional.of(new Person());
        when(personRepositoryMock.findById(0L)).thenReturn(o);

        assertTrue(caseService.getLendCasesFromPersonOwner(0L).isEmpty());
    }

    @Test
    public void ownerWithTwoFreeCases() {
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, new User(), null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, null, null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, new User(), null, null));
        when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
        Optional<Person> o = Optional.of(new Person());
        when(personRepositoryMock.findById(0L)).thenReturn(o);
        cases.remove(3);
        cases.remove(1);

        assertEquals(cases, caseService.getFreeCasesFromPersonOwner(0L));
    }

    @Test
    public void ownerWithNoFreeCases() {
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, new User(), null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, new User(), null, null));
        cases.add(new Case(null, 0L, null, null, 0D, 0D, 0, null, new User(), null, null));

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
        c.setPrice(0D);
        c.setDeposit(10D);

        caseService.addCaseForNewArticle(article, 0D, 10D);

        verify(caseRepositoryMock).save(c);
    }

    @Test
    public void requestArticle() throws Exception {
        Long articleId = 0L, st = 5L, et = 10L;
        String username = "";
        Article article = new Article();
        article.setDeposit(100D);
        article.setCostPerDay(50D);
        when(articleServiceMock.findArticleById(articleId)).thenReturn(article);
        when(userServiceMock.findUserByUsername(username)).thenReturn(new User());
        when(accountHandlerMock.hasValidFunds(eq(""), Mockito.anyDouble())).thenReturn(true);
        ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);

        caseService.requestArticle(articleId, st, et, username);

        verify(caseRepositoryMock).save(argument.capture());
        assertEquals(st, argument.getValue().getStartTime());
        assertEquals(et, argument.getValue().getEndTime());
        assertEquals(article, argument.getValue().getArticle());
        assertEquals(new User(), argument.getValue().getReceiver());
        assertEquals(100D, argument.getValue().getDeposit(), 1);
        assertEquals(50D, argument.getValue().getPrice(), 1);
    }

    @Test
    public void requestArticleWithoutEnoughtMoney() throws Exception {
        Long articleId = 0L, st = 5L, et = 10L;
        String username = "";
        Article article = new Article();
        article.setDeposit(100D);
        article.setCostPerDay(50D);
        when(articleServiceMock.findArticleById(articleId)).thenReturn(article);
        when(userServiceMock.findUserByUsername(username)).thenReturn(new User());
        when(accountHandlerMock.hasValidFunds(any(), anyDouble())).thenReturn(false);

        caseService.requestArticle(articleId, st, et, username);

        verify(caseRepositoryMock, times(0)).save(any());
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

    @Test
    public void articleAlreadyRequestedInTheTime(){
        Article article = new Article();
        Case c1 = new Case();
        c1.setRequestStatus(Case.REQUEST_ACCEPTED);
        c1.setStartTime(0L);
        c1.setEndTime(5L);
        c1.setArticle(article);
        Case c2 = new Case();
        c2.setRequestStatus(Case.REQUEST_ACCEPTED);
        c2.setStartTime(1L);
        c2.setEndTime(4L);
        c2.setArticle(article);
        article.setCases(Arrays.asList(c1, c2));
        when(caseRepositoryMock.findById(0L)).thenReturn(Optional.of(c1));

        assertFalse(caseService.requestIsOk(0L));
    }

    @Test
    public void articleAlreadyRequestedInTheTime2(){
        Article article = new Article();
        Case c1 = new Case();
        c1.setRequestStatus(Case.REQUEST_ACCEPTED);
        c1.setStartTime(0L);
        c1.setEndTime(5L);
        c1.setArticle(article);
        Case c2 = new Case();
        c2.setRequestStatus(Case.REQUEST_ACCEPTED);
        c2.setStartTime(1L);
        c2.setEndTime(4L);
        c2.setArticle(article);
        article.setCases(Arrays.asList(c1, c2));
        when(caseRepositoryMock.findById(0L)).thenReturn(Optional.of(c2));

        assertFalse(caseService.requestIsOk(0L));
    }

    @Test
    public void articleAlreadyRequestedInTheTime3(){
        Article article = new Article();
        Case c1 = new Case();
        c1.setRequestStatus(Case.REQUEST_ACCEPTED);
        c1.setStartTime(1L);
        c1.setEndTime(4L);
        c1.setArticle(article);
        Case c2 = new Case();
        c2.setRequestStatus(Case.REQUEST_ACCEPTED);
        c2.setStartTime(3L);
        c2.setEndTime(5L);
        c2.setArticle(article);
        article.setCases(Arrays.asList(c1, c2));
        when(caseRepositoryMock.findById(0L)).thenReturn(Optional.of(c1));

        assertFalse(caseService.requestIsOk(0L));
    }

    @Test
    public void articleNotRequestedInTheTime(){
        Article article = new Article();
        Case c1 = new Case();
        c1.setRequestStatus(Case.REQUEST_ACCEPTED);
        c1.setStartTime(0L);
        c1.setEndTime(1L);
        c1.setArticle(article);
        Case c2 = new Case();
        c2.setRequestStatus(Case.REQUEST_ACCEPTED);
        c2.setStartTime(2L);
        c2.setEndTime(3L);
        c2.setArticle(article);
        article.setCases(Arrays.asList(c1, c2));
        when(caseRepositoryMock.findById(0L)).thenReturn(Optional.of(c1));

        assertTrue(caseService.requestIsOk(0L));
    }

    @Test
    public void acceptingRequestPossible(){
        Article article = new Article();
        Case c1 = new Case();
        c1.setRequestStatus(Case.REQUEST_ACCEPTED);
        c1.setStartTime(0L);
        c1.setEndTime(1L);
        c1.setArticle(article);
        Case c2 = new Case();
        c2.setRequestStatus(Case.REQUEST_ACCEPTED);
        c2.setStartTime(2L);
        c2.setEndTime(3L);
        c2.setArticle(article);
        article.setCases(Arrays.asList(c1, c2));
        when(caseRepositoryMock.findById(0L)).thenReturn(Optional.of(c1));
        when(accountHandlerMock.hasValidFunds(any())).thenReturn(true);
        ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);

        assertTrue(caseService.acceptArticleRequest(0L));
        verify(caseRepositoryMock).save(argument.capture());
        assertEquals(Case.REQUEST_ACCEPTED, argument.getValue().getRequestStatus());
    }

    @Test
    public void acceptingRequestNotPossible(){
        Article article = new Article();
        Case c1 = new Case();
        c1.setRequestStatus(Case.REQUEST_ACCEPTED);
        c1.setStartTime(0L);
        c1.setEndTime(4L);
        c1.setArticle(article);
        Case c2 = new Case();
        c2.setRequestStatus(Case.REQUEST_ACCEPTED);
        c2.setStartTime(3L);
        c2.setEndTime(5L);
        c2.setArticle(article);
        article.setCases(Arrays.asList(c1, c2));
        when(caseRepositoryMock.findById(0L)).thenReturn(Optional.of(c1));
        when(accountHandlerMock.hasValidFunds(any())).thenReturn(true);
        ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);

        assertFalse(caseService.acceptArticleRequest(0L));
        verify(caseRepositoryMock).save(argument.capture());
        assertEquals(Case.RENTAL_NOT_POSSIBLE, argument.getValue().getRequestStatus());
    }

    @Test
    public void acceptNotExistingRequest(){
        when(caseRepositoryMock.findById(0L)).thenReturn(Optional.empty());

        assertFalse(caseService.acceptArticleRequest(0L));
    }

    @Test
    public void declineRequest(){
        when(caseRepositoryMock.findById(0L)).thenReturn(Optional.of(new Case()));
        ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);

        caseService.declineArticleRequest(0L);

        verify(caseRepositoryMock).save(argument.capture());
        assertEquals(Case.REQUEST_DECLINED, argument.getValue().getRequestStatus());
        verify(reservationHandlerMock).releaseReservation(argument.getValue());
        assertEquals(new PPTransaction(), argument.getValue().getPpTransaction());
    }

    @Test
    public void declineNotExistingRequest(){
        when(caseRepositoryMock.findById(0L)).thenReturn(Optional.empty());

        caseService.declineArticleRequest(0L);

        verify(caseRepositoryMock, times(0)).save(any());
    }

    @Test
    public void openConflict(){
        when(caseRepositoryMock.findById(0L)).thenReturn(Optional.of(new Case()));
        ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);

        caseService.conflictOpened(0L);

        verify(caseRepositoryMock).save(argument.capture());
        assertEquals(Case.OPEN_CONFLICT, argument.getValue().getRequestStatus());
    }

    @Test
    public void finishCase(){
        when(caseRepositoryMock.findById(0L)).thenReturn(Optional.of(new Case()));
        ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);

        caseService.acceptCaseReturn(0L);

        verify(caseRepositoryMock).save(argument.capture());
        assertEquals(Case.FINISHED, argument.getValue().getRequestStatus());
    }

    @Test
    public void onlyRequestCases(){
        Case c1 = new Case();
        c1.setRequestStatus(Case.REQUESTED);
        Case c2 = new Case();
        c2.setRequestStatus(Case.REQUEST_ACCEPTED);
        Case c3 = new Case();
        c3.setRequestStatus(Case.REQUEST_DECLINED);
        Case c4 = new Case();
        c4.setRequestStatus(Case.RENTAL_NOT_POSSIBLE);
        ArrayList<Case> cases = new ArrayList<>(Arrays.asList(c1, c2, c3, c4));
        when(caseRepositoryMock.findAllByArticleOwnerId(0L)).thenReturn(cases);

        assertEquals(cases, caseService.findAllRequestedCasesbyUserId(0L));
    }

    @Test
    public void zeroRequestCases(){
        Case c1 = new Case();
        c1.setRequestStatus(Case.FINISHED);
        Case c2 = new Case();
        c2.setRequestStatus(Case.OPEN_CONFLICT);
        Case c3 = new Case();
        c3.setRequestStatus(Case.RUNNING);
        Case c4 = new Case();
        c4.setRequestStatus(Case.RUNNING);
        ArrayList<Case> cases = new ArrayList<>(Arrays.asList(c1, c2, c3, c4));
        when(caseRepositoryMock.findAllByArticleOwnerId(0L)).thenReturn(cases);

        assertTrue(caseService.findAllRequestedCasesbyUserId(0L).isEmpty());
    }

    @Test
    public void twoRequestCases(){
        Case c1 = new Case();
        c1.setRequestStatus(Case.RUNNING);
        Case c2 = new Case();
        c2.setRequestStatus(Case.REQUEST_ACCEPTED);
        Case c3 = new Case();
        c3.setRequestStatus(Case.FINISHED);
        Case c4 = new Case();
        c4.setRequestStatus(Case.RENTAL_NOT_POSSIBLE);
        ArrayList<Case> cases = new ArrayList<>(Arrays.asList(c1, c2, c3, c4));
        when(caseRepositoryMock.findAllByArticleOwnerId(0L)).thenReturn(cases);
        cases.remove(c1);
        cases.remove(c3);

        assertEquals(cases, caseService.findAllRequestedCasesbyUserId(0L));
    }
}
