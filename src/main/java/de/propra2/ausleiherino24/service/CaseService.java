package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.PpTransaction;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import de.propra2.ausleiherino24.propayhandler.ReservationHandler;
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

    public void saveCase(final Case acase) {
        caseRepository.save(acase);
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

    public boolean isValidCase(final Long id) {
        return caseRepository.existsById(id);
    }

    List<Case> getAllCasesFromPersonOwner(final Long personId) {
        return caseRepository
                .findAllByArticleOwner(personService.findPersonById(personId).getUser());
    }

    private List<Case> findAllCasesByUserId(final Long userId) {
        return caseRepository.findAllByArticleOwnerId(userId);
    }

    // TODO: Only implemented in tests. Necessary?
    List<Case> getLendCasesFromPersonOwner(final Long personId) {
        final List<Case> cases = getAllCasesFromPersonOwner(personId);
        return cases.stream()
                .filter(c -> c.getReceiver() != null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Finds all Transactions from receiver by its personId.
     */
    public List<PpTransaction> findAllTransactionsForPerson(final Long personId) {
        return findAllCasesByUserId(personId).stream()
                .filter(c -> c.getRequestStatus() != Case.REQUEST_DECLINED
                        && c.getRequestStatus() != Case.RENTAL_NOT_POSSIBLE)
                .map(Case::getPpTransaction)
                .collect(Collectors.toList());
    }

    // TODO: Only implemented in tests. Necessary?
    List<Case> getFreeCasesFromPersonOwner(final Long personId) {
        final List<Case> cases = getAllCasesFromPersonOwner(personId);
        return cases.stream()
                .filter(c -> c.getReceiver() == null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets all cases for articles a person has borrowed.
     * @param personId of person to obtain lend cases.
     * @return all cases borrowed by a person
     */
    public List<Case> getLendCasesFromPersonReceiver(final Long personId) {
        return caseRepository
                .findAllByReceiver(personService.findPersonById(personId).getUser())
                .stream()
                .filter(c -> !c.getArticle().isForSale())
                .collect(Collectors.toList());
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
                endTime)) {

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

    private Double getCostForAllDays(final Long articleId, final Long startTime,
            final Long endTime) {

        final Double dailyCost = articleService.findArticleById(articleId).getCostPerDay();
        final Date startDate = new Date(startTime);
        final Date endDate = new Date(endTime);

        final long diffInMilliseconds = Math.abs(endDate.getTime() - startDate.getTime());

        return TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS) * dailyCost;
    }


    /**
     * // TODO: JavaDoc ... Checks, if article request is ok.
     *
     * @return 0: case could not be found 1: everything alright 2: the article is already rented in
     *      the given time 3: receiver does not have enough money on ProPay.
     */
    public int acceptArticleRequest(final Long id) {
        final Optional<Case> optCase = caseRepository.findById(id);
        if (!optCase.isPresent()) {
            return 0;
        }
        final Case currentCase = optCase.get();

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

    boolean articleNotRented(final Long id) {
        final Optional<Case> currentCase = caseRepository.findById(id);
        if (!currentCase.isPresent()) {
            return false;
        }

        final Article article = currentCase.get().getArticle();
        final List<Case> cases = article.getCases().stream()
                .filter(ca -> ca.getRequestStatus() == Case.REQUEST_ACCEPTED)
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
                .filter(ca -> ca.getRequestStatus() == Case.REQUEST_ACCEPTED)
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
     */
    public void declineArticleRequest(final Long id) {
        final Optional<Case> optCase = caseRepository.findById(id);
        if (!optCase.isPresent()) {
            return;
        }
        final Case currentCase = optCase.get();
        currentCase.setRequestStatus(Case.REQUEST_DECLINED);
        reservationHandler.releaseReservationByCase(currentCase);
        currentCase.setPpTransaction(new PpTransaction());
        caseRepository.save(currentCase);
    }

    /**
     * Finds all expired cases, where requestStatus in {RUNNING, FINISHED, OPEN_CONFLICT}.
     */
    public List<Case> findAllExpiredCasesByUserId(final Long id) {
        return caseRepository.findAllExpiredCasesByUserId(id, new Date().getTime());
    }

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
    public void acceptCaseReturn(final Long id) {
        final Optional<Case> opt = caseRepository.findById(id);
        if (opt.isPresent()) {
            final Case currentCase = opt.get();
            currentCase.setRequestStatus(Case.FINISHED);
            caseRepository.save(currentCase);
        }
    }

    /**
     * Finds all requested cases from one user by its id.
     */
    public List<Case> findAllRequestedCasesByUserId(final Long id) {
        return findAllCasesByUserId(id)
                .stream()
                .filter(c -> c.getRequestStatus() == Case.REQUESTED
                        || c.getRequestStatus() == Case.REQUEST_ACCEPTED
                        || c.getRequestStatus() == Case.REQUEST_DECLINED
                        || c.getRequestStatus() == Case.RENTAL_NOT_POSSIBLE)
                .filter(c -> !c.getArticle().isForSale())
                .collect(Collectors.toList());
    }

    /**
     * Finds all days where an article is reserved.
     */
    public List<LocalDate> findAllReservedDaysByArticle(final Long id) {
        return caseRepository
                .findAllByArticleAndRequestStatus(articleService.findArticleById(id), 2)
                .stream()
                .map(c -> {
                    final LocalDate start = Instant.ofEpochMilli(c.getStartTime())
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    final LocalDate end = Instant.ofEpochMilli(c.getEndTime())
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
                .filter(c -> c.getRequestStatus() == Case.OPEN_CONFLICT)
                .sorted(Comparator.comparing(Case::getEndTime))
                .collect(Collectors.toList());
    }

    /**
     * Sells article, transfers money and creates case.
     *
     * @param articleId article that is sold
     * @param principal costumer who buys article
     */
    public boolean sellArticle(final Long articleId, final Principal principal) {
        final Article article = articleService.findArticleById(articleId);
        final User costumer = userService.findUserByPrincipal(principal);

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
            articleService.setSellStatusFromArticle(articleId, false);
            articleService.deactivateArticle(articleId);
            return true;
        }
        return false;
    }
}
