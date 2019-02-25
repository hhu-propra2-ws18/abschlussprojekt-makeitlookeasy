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
import java.util.function.Function;
import java.util.concurrent.TimeUnit;
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
    public CaseService(CaseRepository caseRepository, ArticleService articleService,
            PersonService personService, UserService userService,
            AccountHandler accountHandler, ReservationHandler reservationHandler) {
        this.caseRepository = caseRepository;
        this.articleService = articleService;
        this.personService = personService;
        this.userService = userService;
        this.accountHandler = accountHandler;
        this.reservationHandler = reservationHandler;
    }

    // TODO: Only implemented in tests. Necessary?
    void addCaseForNewArticle(Article article, Double price, Double deposit) {
        Case c = new Case();
        c.setArticle(article);
        c.setDeposit(deposit);
        c.setPrice(price);

        caseRepository.save(c);
    }

    List<Case> getAllCasesFromPersonOwner(Long personId) {
        return caseRepository
                .findAllByArticleOwner(personService.findPersonById(personId).getUser());
    }

    private List<Case> findAllCasesByUserId(Long userId) {
        return caseRepository.findAllByArticleOwnerId(userId);
    }

    // TODO: Only implemented in tests. Necessary?
    List<Case> getLendCasesFromPersonOwner(Long personId) {
        List<Case> cases = getAllCasesFromPersonOwner(personId);
        return cases.stream()
                .filter(c -> c.getReceiver() != null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<PPTransaction> getAllTransactionsFromPersonReceiver(Long personId) {
        List<PPTransaction> ppTransactions = new ArrayList<>();
        List<Case> cases = getLendCasesFromPersonReceiver(personId);
        for (Case c : cases) {
            ppTransactions.add(c.getPpTransaction());
        }
        return ppTransactions;
    }

    // TODO: Only implemented in tests. Necessary?
    List<Case> getFreeCasesFromPersonOwner(Long personId) {
        List<Case> cases = getAllCasesFromPersonOwner(personId);
        return cases.stream()
                .filter(c -> c.getReceiver() == null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Case> getLendCasesFromPersonReceiver(Long personId) {
        return caseRepository.findAllByReceiver(personService.findPersonById(personId).getUser());
    }

    // TODO: Method is never used. Delete?
    public List<Case> getAllRequestedCasesbyUser(Long userId) {
        return caseRepository
                .findAllByArticleOwner(userService.findUserById(userId))
                .stream()
                .filter(c -> c.getRequestStatus() == Case.REQUESTED)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // TODO: Return value is never used. Update implementing methods or void?
    public boolean requestArticle(Long articleId, Long startTime, Long endTime, String username) {
        Double totalCost = getCostForAllDays(articleId, startTime, endTime);

        if (accountHandler.hasValidFunds(username,
                totalCost + articleService.findArticleById(articleId).getDeposit())) {

            PPTransaction ppTransaction = new PPTransaction();
            ppTransaction.setLendingCost(totalCost);
            ppTransaction.setCautionPaid(false);

            Case c = new Case();
            c.setArticle(articleService.findArticleById(articleId));
            c.setStartTime(startTime);
            c.setEndTime(endTime);
            c.setDeposit(c.getArticle().getDeposit());
            c.setPrice(c.getArticle().getCostPerDay());
            c.setReceiver(userService.findUserByUsername(username));
            c.setRequestStatus(Case.REQUESTED);
            c.setPpTransaction(ppTransaction);

            caseRepository.save(c);

            reservationHandler.handleReservedMoney(c);

            return true;
        }
        return false;
    }

    private Double getCostForAllDays(Long articleId, Long startTime, Long endTime) {

        Double dailyCost = articleService.findArticleById(articleId).getCostPerDay();
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);

        long diffInMilliseconds = Math.abs(endDate.getTime() - startDate.getTime());

        return TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS) * dailyCost;
    }


    /**
     * return 0: case could not be found
     * return 1: everything alright
     * return 2: the article is already rented in the given time
     * return 3: receiver does not have enough money on Propay
     */
    public int acceptArticleRequest(Long id) {
        Optional<Case> optCase = caseRepository.findById(id);
        if (!optCase.isPresent()) {
            return 0;
        }
        Case c = optCase.get();

        //Check whether the article is not reserved in this period of time
        boolean articleRented = articleNotRented(id);
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

    /**
     * checks whether the article is not rented in the given time
     * @param id CaseId
     * @return
     */
    boolean articleNotRented(Long id) {
        Optional<Case> c = caseRepository.findById(id);
        if (!c.isPresent()) {
            return false;
        }

        Article article = c.get().getArticle();
        List<Case> cases = article.getCases().stream()
                .filter(ca -> ca.getRequestStatus() == Case.REQUEST_ACCEPTED)
                .collect(Collectors.toList());
        cases.remove(c.get());

        for (Case ca : cases) {
            if (!(ca.getStartTime() > c.get().getEndTime() || ca.getEndTime() < c.get().getStartTime())) {
                return false;
            }
        }
        return true;
    }

    boolean articleNotRented(Article article, Long startTime, Long endTime, Case c) {
        List<Case> cases = article.getCases().stream()
                .filter(ca -> ca.getRequestStatus() == Case.REQUEST_ACCEPTED)
                .collect(Collectors.toList());

        for (Case ca : cases) {
            if (!(ca.getStartTime() > endTime || ca.getEndTime() < startTime)) {
                return false;
            }
        }
        return true;
    }

    public Case findCaseById(Long id) {
        Optional<Case> optionalCase = caseRepository.findById(id);

        if (!optionalCase.isPresent()) {
            LOGGER.warn("Couldn't find case {} in database.", id);
            throw new NullPointerException();
        }

        return optionalCase.get();
    }

    public void declineArticleRequest(Long id) {
        Optional<Case> optCase = caseRepository.findById(id);
        if (!optCase.isPresent()) {
            return;
        }
        Case c = optCase.get();
        c.setRequestStatus(Case.REQUEST_DECLINED);
        reservationHandler.releaseReservation(c);
        c.setPpTransaction(new PPTransaction());
        caseRepository.save(c);
    }


    public List<Case> findAllExpiredCasesByUserId(Long id) {
        return findAllCasesByUserId(id)
                .stream()
                .filter(c -> c.getEndTime() < new Date().getTime())
                .filter(c -> c.getRequestStatus() == Case.RUNNING ||
                        c.getRequestStatus() == Case.FINISHED ||
                        c.getRequestStatus() == Case.OPEN_CONFLICT)
                .collect(Collectors.toList());
    }

    void conflictOpened(Long id) {
        Optional<Case> opt = caseRepository.findById(id);
        if (opt.isPresent()) {
            Case c = opt.get();
            c.setRequestStatus(Case.OPEN_CONFLICT);
            caseRepository.save(c);
        }
    }

    public void acceptCaseReturn(Long id) {
        Optional<Case> opt = caseRepository.findById(id);
        if (opt.isPresent()) {
            Case c = opt.get();
            c.setRequestStatus(Case.FINISHED);
            caseRepository.save(c);
        }
    }

    public List<Case> findAllRequestedCasesByUserId(Long id) {
        return findAllCasesByUserId(id)
                .stream()
                .filter(c -> c.getRequestStatus() == Case.REQUESTED
                        || c.getRequestStatus() == Case.REQUEST_ACCEPTED
                        || c.getRequestStatus() == Case.REQUEST_DECLINED
                        || c.getRequestStatus() == Case.RENTAL_NOT_POSSIBLE)
                .collect(Collectors.toList());
    }

    public List<LocalDate> findAllReservedDaysByArticle(Long id) {
        return caseRepository
                .findAllByArticleAndRequestStatus(articleService.findArticleById(id), 2)
                .stream()
                .map(c -> {
                    LocalDate start = Instant.ofEpochMilli(c.getStartTime())
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate end = Instant.ofEpochMilli(c.getEndTime())
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    int daysInBetween = Period.between(start, end).getDays();
                    return IntStream
                            .range(0, daysInBetween + 1)
                            .mapToObj(start::plusDays);
                })
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }
}
