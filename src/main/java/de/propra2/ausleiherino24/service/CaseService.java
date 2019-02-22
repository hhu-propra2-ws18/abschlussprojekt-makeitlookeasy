package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.propra2.ausleiherino24.propayhandler.AccountHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaseService {

    //TODO some methods need tests
    private final CaseRepository caseRepository;
    private final PersonRepository personRepository;
    private final ArticleService articleService;
    private final UserService userService;
    private final AccountHandler accountHandler;

    /**
     * TODO JavaDoc.
     *  @param caseRepository Description
     * @param personRepository Description
     * @param articleService Description
     * @param userService Description
     * @param accountHandler Description
     */
    @Autowired
    public CaseService(CaseRepository caseRepository, PersonRepository personRepository,
                       ArticleService articleService,
                       UserService userService, AccountHandler accountHandler) {
        this.caseRepository = caseRepository;
        this.personRepository = personRepository;
        this.articleService = articleService;
        this.userService = userService;
        this.accountHandler = accountHandler;
    }

    /**
     * Fügt einen Artikel, welcher frei zum Verleih ist, von einer Person hinzu.
     */
    public void addCaseForNewArticle(Article article, int price, int deposit) {
        Case c = new Case();
        c.setArticle(article);
        c.setDeposit(deposit);
        c.setPrice(price);

        caseRepository.save(c);
    }

    /**
     * Gibt alle Cases zurück, wo die Person der Verleihende ist.
     */
    public List<Case> getAllCasesFromPersonOwner(Long personId) {
        return caseRepository
                .findAllByArticleOwner(personRepository.findById(personId).get().getUser());
    }

    /**
     * Gibt alle Cases zurück, wo die Person der Verleihende ist.
     */
    public List<Case> findAllCasesbyUserId(Long userId) {
        return caseRepository
                .findAllByArticleOwnerId(userId);
    }

    /**
     * Gibt alle Cases zurück, wo die Person der Verleihende ist und der Artikel momentan verliehen
     * ist.
     */
    public List<Case> getLendCasesFromPersonOwner(Long personId) {
        List<Case> cases = getAllCasesFromPersonOwner(personId);
        return cases.stream()
                .filter(c -> c.getReceiver() != null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gibt alle Cases zurück, wo die Person der Verleihende ist und der Artikel momentan nicht
     * verliehen ist.
     */
    public List<Case> getFreeCasesFromPersonOwner(Long personId) {
        List<Case> cases = getAllCasesFromPersonOwner(personId);
        return cases.stream()
                .filter(c -> c.getReceiver() == null)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gibt alle Cases zurück, wo die Person sich von jemanden etwas geliehen hat.
     */
    public List<Case> getLendCasesFromPersonReceiver(Long personId) {
        return caseRepository
                .findAllByReceiver(personRepository.findById(personId).get().getUser());
    }

    /**
     * Gibt alle Cases zurück, die zu einem Artikel vom User gehören und dessen requestStatus auf
     * REQUESTED steht.
     */
    public List<Case> getAllRequestedCasesbyUser(Long userId) {
        return caseRepository
                .findAllByArticleOwner(userService.findUserById(userId))
                .stream()
                .filter(c -> c.getRequestStatus() == Case.REQUESTED)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Erwartet Case mit wo Artikel verliehen werden kann. Case wird modifiziert, dass es nun
     * verliehen ist.
     */
    public void requestArticle(Long articleId, Long starttime, Long endtime, String username)
            throws Exception {
        Article article = articleService.findArticleById(articleId);
        int costs = (int) (article.getDeposit() + article.getCostPerDay() * (endtime - starttime));
        if(accountHandler.checkFunds(username) >= costs) return;

        Case c = new Case();
        c.setArticle(article);
        c.setStartTime(starttime);
        c.setEndTime(endtime);
        c.setDeposit(c.getArticle().getDeposit());
        c.setPrice(c.getArticle().getCostPerDay());
        c.setReceiver(userService.findUserByUsername(username));
        c.setRequestStatus(Case.REQUESTED);

        caseRepository.save(c);
    }

    /**
     * return true, falls Erfolg return false, falls Misserfolg
     */
    public boolean acceptArticleRequest(Long id) {
        Optional<Case> optCase = caseRepository.findById(id);
        if (!optCase.isPresent()) {
            return false;
        }
        Case c = optCase.get();

        //Check whether the article is not reserved in this period of time
        if (requestIsOk(id)) {
            c.setRequestStatus(Case.REQUEST_ACCEPTED);
            caseRepository.save(c);
            return true;
        } else {
            c.setRequestStatus(Case.RENTAL_NOT_POSSIBLE);
            caseRepository.save(c);
            return false;
        }
    }

    public void declineArticleRequest(Long id) {
        Optional<Case> optCase = caseRepository.findById(id);
        if (!optCase.isPresent()) {
            return;
        }
        Case c = optCase.get();
        c.setRequestStatus(Case.REQUEST_DECLINED);
        caseRepository.save(c);
    }

    /**
     * Überprüft, ob der Artikel zu gegebener CaseId in gegebenen Zeitraum noch verliehen werden
     * kann oder nicht True: kann verliehen werden False: kann nicht verliehen werden Die Methode
     * nimmt an, dass die Id korrekt ist
     */
    private boolean requestIsOk(Long id) {
        Case c = caseRepository.findById(id).get();
        Article article = c.getArticle();
        List<Case> cases = article.getCases().stream()
                .filter(ca -> ca.getRequestStatus() == Case.REQUEST_ACCEPTED)
                .collect(Collectors.toList());
        cases.remove(c); //Makes sure, that c is not an element in cases

        for (Case ca : cases) {
            if (!(ca.getStartTime() > c.getEndTime() || ca.getEndTime() < c.getStartTime())) {
                return false;
            }
        }
        return true;
    }

    public List<Case> findAllExpiredCasesbyUserId(Long id) {
        return findAllCasesbyUserId(id)
                .stream()
                .filter(c -> c.getEndTime() < new Date().getTime())
                .filter(c -> c.getRequestStatus() == Case.RUNNING ||
                        c.getRequestStatus() == Case.FINISHED ||
                        c.getRequestStatus() == Case.OPEN_CONFLICT)
                .collect(Collectors.toList());
    }

    /**
     * Stellt den Status von Case mit id id auf Case.OPEN_CONFLICT
     *
     * @param id CaseId
     */
    public void conflictOpened(Long id) {
        Optional<Case> opt = caseRepository.findById(id);
        if (opt.isPresent()) {
            Case c = opt.get();
            c.setRequestStatus(Case.OPEN_CONFLICT);
            caseRepository.save(c);
        }
    }

    /**
     * Stellt den Status von Case mit id id auf Case.FINISHED
     *
     * @param id CaseId
     */
    public void acceptCaseReturn(Long id) {
        Optional<Case> opt = caseRepository.findById(id);
        if (opt.isPresent()) {
            Case c = opt.get();
            c.setRequestStatus(Case.FINISHED);
            caseRepository.save(c);
        }
    }

    /**
     * Findet alle Cases mit Status in {REQUESTED, REQUEST_ACCEPTED, REQUEST_DECLINED,
     * RENTAL_NOT_POSSIBLE}
     * @param id
     * @return
     */
    public List<Case> findAllRequestedCasesbyUserId(Long id) {
        return findAllCasesbyUserId(id)
                .stream()
                .filter(c -> c.getRequestStatus() == Case.REQUESTED
                        || c.getRequestStatus() == Case.REQUEST_ACCEPTED
                        || c.getRequestStatus() == Case.REQUEST_DECLINED
                        || c.getRequestStatus() == Case.RENTAL_NOT_POSSIBLE)
                .collect(Collectors.toList());
    }
}
