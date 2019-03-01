package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.data.AccountHandler;
import de.propra2.ausleiherino24.propayhandler.data.ReservationHandler;
import de.propra2.ausleiherino24.propayhandler.model.PpTransaction;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseService.class);

    private final CaseRepository caseRepository;
    private final ArticleService articleService;
    private final PersonService personService;
    private final UserService userService;
    private final AccountHandler accountHandler;
    private final ReservationHandler reservationHandler;

    /**
     * Autowired constructor.
     */
    @Autowired
    public CaseService(final CaseRepository caseRepository, final ArticleService articleService,
            final PersonService personService, final UserService userService,
            final AccountHandler accountHandler, final ReservationHandler reservationHandler) {
        this.caseRepository = caseRepository;
        this.articleService = articleService;
        this.personService = personService;
        this.userService = userService;
        this.accountHandler = accountHandler;
        this.reservationHandler = reservationHandler;
    }

    /**
     * Saves given case in database.
     */
    public void saveCase(final Case thisCase) {
        caseRepository.save(thisCase);
    }

    /**
     * Finds Case by its id.
     */
    public Case findCaseById(final Long id) {
        final Optional<Case> optionalCase = caseRepository.findById(id);

        if (!optionalCase.isPresent()) {
            LOGGER.warn("Couldn't find case {} in database.", id);
            throw new NoSuchElementException();
        }

        return optionalCase.get();
    }

    /**
     * Checks if case exists.
     *
     * @return true, if a case with the given id exists. Otherwise returns false.
     */
    public boolean isValidCase(final Long id) {
        return caseRepository.existsById(id);
    }

    /**
     * gets all cases owned by person.
     *
     * @param personId personId of the owner of the lend article.
     * @return all cases, where the given id is the personId of the person who owns the article.
     */
    List<Case> getAllCasesFromPersonOwner(final Long personId) {
        return caseRepository
                .findAllByArticleOwner(personService.findPersonById(personId).getUser());
    }

    /**
     * Finds all Transactions from receiver by its personId.
     */
    public List<PpTransaction> findAllTransactionsForPerson(final Long personId) {

        try {
            final List<PpTransaction> ppTransactions = new ArrayList<>(
                    getAllCasesFromPersonOwner(personId).stream()
                            .filter(cases -> cases.getRequestStatus() != Case.REQUEST_DECLINED
                                    && cases.getRequestStatus() != Case.RENTAL_NOT_POSSIBLE)
                            .map(Case::getPpTransaction)
                            .collect(Collectors.toList()));
            ppTransactions.addAll(getLendCasesFromPersonReceiver(personId).stream()
                    .filter(crequest -> crequest.getRequestStatus() != Case.REQUEST_DECLINED
                            && crequest.getRequestStatus() != Case.RENTAL_NOT_POSSIBLE)
                    .map(Case::getPpTransaction)
                    .collect(Collectors.toList()));
            return ppTransactions;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    /**
     * Gets all cases for articles a person has borrowed.
     *
     * @param personId of person to obtain lend cases.
     * @return all cases borrowed by a person
     */
    public List<Case> getLendCasesFromPersonReceiver(final Long personId) {
        return caseRepository.getLendCasesFromPersonReceiver(personId);
    }

    /**
     * Creates ppTransaction and Case for request.
     *
     * @return true, if param username has valid funds. else, otherwise.
     */
    public boolean requestArticle(final Long articleId, final Long startTime, final Long endTime,
            final String username) {
        final Double totalCost = getCostForAllDays(articleId, startTime, endTime);

        if (accountHandler.hasValidFunds(username,
                totalCost + articleService.findArticleById(articleId).getDeposit())
                && articleNotRented(articleService.findArticleById(articleId), startTime,
                endTime) && new Date().getTime()-86000000 < startTime && startTime < endTime
                && !articleService.findArticleById(articleId).getOwner().getUsername()
                .equals(username) && accountHandler.checkAvailability()) {

            final PpTransaction ppTransaction = new PpTransaction();
            ppTransaction.setLendingCost(totalCost);
            ppTransaction.setCautionPaid(false);
            ppTransaction.setDate(new Date().getTime());

            final Case aCase = new Case();
            aCase.setArticle(articleService.findArticleById(articleId));
            aCase.setStartTime(startTime);
            aCase.setEndTime(endTime);
            aCase.setDeposit(aCase.getArticle().getDeposit());
            aCase.setPrice(aCase.getArticle().getCostPerDay());
            aCase.setReceiver(userService.findUserByUsername(username));
            aCase.setRequestStatus(Case.REQUESTED);
            aCase.setPpTransaction(ppTransaction);

            caseRepository.save(aCase);

            reservationHandler.handleReservedMoney(aCase);

            return true;
        }
        return false;
    }

    /**
     * gets cost for all days for time and articleid.
     *
     * @return the total cost for lending the article in the given time.
     */
    private Double getCostForAllDays(final Long articleId, final Long startTime,
            final Long endTime) {

        final Double dailyCost = articleService.findArticleById(articleId).getCostPerDay();
        final Date startDate = new Date(startTime);
        final Date endDate = new Date(endTime);

        final long diffInMilliseconds = Math.abs(endDate.getTime() - startDate.getTime());

        return TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS) * dailyCost;
    }


    /**
     * Checks, if article request is ok.
     *
     * @return 0: not found 1: ok 2: already rented 3: not enough Funds 4: Propay unavailable
     */
    public int acceptArticleRequest(final Long id) {

        final Optional<Case> optCase = caseRepository.findById(id);
        if (!optCase.isPresent()) {
            return 0;
        }
        final Case currentCase = optCase.get();

        if (!accountHandler.checkAvailability()) {
            return 4;
        }

        //Check whether the article is not reserved in this period of time
        final boolean articleRented = articleNotRented(id);

        if (articleRented && accountHandler.hasValidFundsByCase(currentCase)) {
            currentCase.setRequestStatus(Case.REQUEST_ACCEPTED);
            reservationHandler.handleReservedMoney(currentCase);
            caseRepository.save(currentCase);
            return 1;
        } else {
            currentCase.setRequestStatus(Case.RENTAL_NOT_POSSIBLE);
            reservationHandler.releaseReservationByCase(currentCase);
            caseRepository.save(currentCase);
            if (articleRented) {
                return 3;
            } else {
                return 2;
            }
        }
    }

    /**
     * Checks, if the article of the case isn't lend in the wanted time.
     *
     * @param id id of the case to check
     * @return true: not lend in the given time. false: lend in the given time or does not exist
     */
    boolean articleNotRented(final Long id) {
        final Optional<Case> currentCase = caseRepository.findById(id);
        if (!currentCase.isPresent()) {
            return false;
        }

        final Article article = currentCase.get().getArticle();
        final List<Case> cases = article.getCases().stream()
                .filter(caseList -> caseList.getRequestStatus() == Case.REQUEST_ACCEPTED)
                .collect(Collectors.toList());
        cases.remove(currentCase.get());

        for (final Case ca : cases) {
            if (!(ca.getStartTime() > currentCase.get().getEndTime()
                    || ca.getEndTime() < currentCase.get()
                    .getStartTime())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Overloaded method for views.
     */
    boolean articleNotRented(final Article article, final Long startTime, final Long endTime) {
        final List<Case> cases = article.getCases().stream()
                .filter(reqcase -> reqcase.getRequestStatus() == Case.REQUEST_ACCEPTED)
                .collect(Collectors.toList());

        for (final Case ca : cases) {
            if (!(ca.getStartTime() > endTime || ca.getEndTime() < startTime)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Declines an article request.
     *
     * @param id id of the case where the request should be declined.
     */
    public boolean declineArticleRequest(final Long id) {
        if (!accountHandler.checkAvailability()) {
            return false;
        }
        final Optional<Case> optCase = caseRepository.findById(id);
        if (!optCase.isPresent()) {
            return false;
        }
        final Case currentCase = optCase.get();
        currentCase.setRequestStatus(Case.REQUEST_DECLINED);
        reservationHandler.releaseReservationByCase(currentCase);
        currentCase.setPpTransaction(new PpTransaction());
        caseRepository.save(currentCase);
        return true;
    }

    /**
     * Finds all expired cases, where requestStatus in {RUNNING, FINISHED, OPEN_CONFLICT}.
     */
    public List<Case> findAllExpiredCasesByUserId(final Long id) {
        return caseRepository.findAllExpiredCasesByUserId(id, new Date().getTime());
    }

    /**
     * If the given case exists, the status is changed to OPEN_CONFLICT.
     *
     * @param id id of the case, where the status should be changed.
     */
    void conflictOpened(final Long id) {
        final Optional<Case> opt = caseRepository.findById(id);
        if (opt.isPresent()) {
            final Case currentCase = opt.get();
            currentCase.setRequestStatus(Case.OPEN_CONFLICT);
            caseRepository.save(currentCase);
        }
    }

    /**
     * Accepts the return of an Article.
     *
     * @param id CaseId
     */
    public boolean acceptCaseReturn(final Long id) {
        if (!accountHandler.checkAvailability()) {
            return false;
        }
        final Optional<Case> opt = caseRepository.findById(id);
        if (opt.isPresent()) {
            final Case currentCase = opt.get();
            currentCase.setRequestStatus(Case.FINISHED);
            caseRepository.save(currentCase);
        }
        return true;
    }

    /**
     * Finds all requested cases from one user by its id.
     *
     * @param id userId
     */
    public List<Case> findAllRequestedCasesByUserId(final Long id) {
        return caseRepository.findAllRequestedCasesByUserId(id);
    }

    /**
     * Finds all days where an article is reserved.
     *
     * @param id articleId
     */
    public List<LocalDate> findAllReservedDaysByArticle(final Long id) {
        return caseRepository
                .findAllByArticleAndRequestStatus(articleService.findArticleById(id), 2)
                .stream()
                .map(caseStream -> {
                    final LocalDate start = Instant.ofEpochMilli(caseStream.getStartTime())
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    final LocalDate end = Instant.ofEpochMilli(caseStream.getEndTime())
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    final int daysInBetween = Period.between(start, end).getDays();
                    return IntStream
                            .range(0, daysInBetween + 1)
                            .mapToObj(start::plusDays);
                })
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }

    /**
     * Finds all cases with open conflicts.
     */
    public List<Case> findAllCasesWithOpenConflicts() {
        return caseRepository.findAll().stream()
                .filter(cases -> cases.getRequestStatus() == Case.OPEN_CONFLICT)
                .sorted(Comparator.comparing(Case::getEndTime))
                .collect(Collectors.toList());
    }

    /**
     * Sells article, transfers money and creates case.
     *
     * @param articleId article that is sold
     * @param principal costumer who buys article
     * @return true: sale successful. false: costumer hasn't enought money on PPAcount.
     */
    public boolean sellArticle(final Long articleId, final Principal principal) {
        final Article article = articleService.findArticleById(articleId);
        final User costumer = userService.findUserByPrincipal(principal);

        if (!accountHandler.checkAvailability()) {
            return false;
        }

        if (accountHandler.hasValidFunds(costumer.getUsername(),
                articleService.findArticleById(articleId).getCostPerDay())) {
            final Case currentCase = new Case();
            currentCase.setRequestStatus(Case.FINISHED);
            currentCase.setDeposit(0d);
            currentCase.setPrice(article.getCostPerDay());
            currentCase.setArticle(article);
            currentCase.setReceiver(costumer);

            final PpTransaction transaction = new PpTransaction();
            transaction.setLendingCost(article.getCostPerDay());
            transaction.setDate(new Date().getTime());
            transaction.setCautionPaid(false);
            currentCase.setPpTransaction(transaction);
            caseRepository.save(currentCase);
            accountHandler.transferFundsByCase(currentCase);
            articleService.deactivateArticle(articleId);
            return true;
        }
        return false;
    }

    public List<Case> findAllSoldItemsByUserId(final Long id) {
        return caseRepository.findAllSoldItemsByUserId(id);
    }

    /**
     * find cases that will have to be returned soon.
     */
    public List<Case> findAllOutrunningCasesByUserId(final Long id) {
        return caseRepository.findAllOutrunningCasesByUserId(id,
                new Date().getTime(),
                new Date().getTime() + 86400000L);
    }
}
