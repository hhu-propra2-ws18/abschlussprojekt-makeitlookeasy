package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.PPTransaction;
import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import de.propra2.ausleiherino24.propayhandler.ReservationHandler;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    // TODO: Only implemented in tests. Necessary?
    void addCaseForNewArticle(final Article article, final Double price, final Double deposit) {
        final Case aCase = new Case();
        aCase.setArticle(article);
        aCase.setDeposit(deposit);
        aCase.setPrice(price);

        caseRepository.save(aCase);
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

    public List<PPTransaction> getAllTransactionsFromPersonReceiver(final Long personId) {
        final List<PPTransaction> ppTransactions = new ArrayList<>();
        final List<Case> cases = getLendCasesFromPersonReceiver(personId);
        for (final Case c : cases) {
            ppTransactions.add(c.getPpTransaction());
        }
        return ppTransactions;
    }

    // TODO: Only implemented in tests. Necessary?
    List<Case> getFreeCasesFromPersonOwner(final Long personId) {
        final List<Case> cases = getAllCasesFromPersonOwner(personId);
        return cases.stream()
                .filter(c -> c.getReceiver() == null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Case> getLendCasesFromPersonReceiver(final Long personId) {
        return caseRepository.findAllByReceiver(personService.findPersonById(personId).getUser());
    }

    // TODO: Method is never used. Delete?
    public List<Case> getAllRequestedCasesbyUser(final Long userId) {
        return caseRepository
                .findAllByArticleOwner(userService.findUserById(userId))
                .stream()
                .filter(c -> c.getRequestStatus() == Case.REQUESTED)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // TODO: Return value is never used. Update implementing methods or void?
    public boolean requestArticle(final Long articleId, final Long startTime, final Long endTime,
            final String username) {
        final Double totalCost = getCostForAllDays(articleId, startTime, endTime);

        if (accountHandler.hasValidFunds(username,
                totalCost + articleService.findArticleById(articleId).getDeposit())) {

            final PPTransaction ppTransaction = new PPTransaction();
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
     * // TODO: JavaDoc ... return 0: case could not be found return 1: everything alright return 2:
     * the article is already rented in the given time return 3: receiver does not have enough money
     * on ProPay
     */
    // TODO: Why is this called acceptArticleRequest, when it handles cases only??
    public int acceptArticleRequest(final Long id) {
        final Optional<Case> optCase = caseRepository.findById(id);
        if (!optCase.isPresent()) {
            return 0;
        }
        final Case c = optCase.get();

        //Check whether the article is not reserved in this period of time
        final boolean articleRented = articleNotRented(id);

        if (articleRented && accountHandler.hasValidFunds(c)) {
            c.setRequestStatus(Case.REQUEST_ACCEPTED);
            reservationHandler.handleReservedMoney(c);
            caseRepository.save(c);
            return 1;
        } else {
            c.setRequestStatus(Case.RENTAL_NOT_POSSIBLE);
            caseRepository.save(c);
            if (articleRented) {
                return 3;
            } else {
                return 2;
            }
        }
    }

    boolean articleNotRented(final Long id) {
        final Optional<Case> c = caseRepository.findById(id);
        if (!c.isPresent()) {
            return false;
        }

        final Article article = c.get().getArticle();
        final List<Case> cases = article.getCases().stream()
                .filter(ca -> ca.getRequestStatus() == Case.REQUEST_ACCEPTED)
                .collect(Collectors.toList());
        cases.remove(c.get());

        for (final Case ca : cases) {
            if (!(ca.getStartTime() > c.get().getEndTime() || ca.getEndTime() < c.get()
                    .getStartTime())) {
                return false;
            }
        }
        return true;
    }

    boolean articleNotRented(final Article article, final Long startTime, final Long endTime,
            final Case c) {
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

    public Case findCaseById(final Long id) {
        final Optional<Case> optionalCase = caseRepository.findById(id);

        if (!optionalCase.isPresent()) {
            LOGGER.warn("Couldn't find case {} in database.", id);
            throw new NullPointerException();
        }

        return optionalCase.get();
    }

    public void declineArticleRequest(final Long id) {
        final Optional<Case> optCase = caseRepository.findById(id);
        if (!optCase.isPresent()) {
            return;
        }
        final Case c = optCase.get();
        c.setRequestStatus(Case.REQUEST_DECLINED);
        reservationHandler.releaseReservation(c);
        c.setPpTransaction(new PPTransaction());
        caseRepository.save(c);
    }


    public List<Case> findAllExpiredCasesByUserId(final Long id) {
        return findAllCasesByUserId(id)
                .stream()
                .filter(c -> c.getEndTime() < new Date().getTime())
                .filter(c -> c.getRequestStatus() == Case.RUNNING ||
                        c.getRequestStatus() == Case.FINISHED ||
                        c.getRequestStatus() == Case.OPEN_CONFLICT)
                .collect(Collectors.toList());
    }

    void conflictOpened(final Long id) {
        final Optional<Case> opt = caseRepository.findById(id);
        if (opt.isPresent()) {
            final Case c = opt.get();
            c.setRequestStatus(Case.OPEN_CONFLICT);
            caseRepository.save(c);
        }
    }

    public void acceptCaseReturn(final Long id) {
        final Optional<Case> opt = caseRepository.findById(id);
        if (opt.isPresent()) {
            final Case c = opt.get();
            c.setRequestStatus(Case.FINISHED);
            caseRepository.save(c);
        }
    }

    public List<Case> findAllRequestedCasesByUserId(final Long id) {
        return findAllCasesByUserId(id)
                .stream()
                .filter(c -> c.getRequestStatus() == Case.REQUESTED
                        || c.getRequestStatus() == Case.REQUEST_ACCEPTED
                        || c.getRequestStatus() == Case.REQUEST_DECLINED
                        || c.getRequestStatus() == Case.RENTAL_NOT_POSSIBLE)
                .collect(Collectors.toList());
    }

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
}
