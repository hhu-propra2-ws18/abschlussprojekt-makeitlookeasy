package de.propra2.ausleiherino24.propayhandler;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PPTransactionRepository;
import de.propra2.ausleiherino24.model.Case;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountHandler {

    private static final String ACCOUNT_URL = "http://localhost:8888/account";
    private static final String ACCOUNT_DEFAULT = "/{account}";
    private RestTemplate restTemplate;
    private PPTransactionRepository ppTransactionRepository;
    private CaseRepository caseRepository;

    /**
     * TODO: Javadoc.
     *
     * @param caseRepository Description
     * @param ppTransactionRepository Description
     * @param restTemplate Description
     */
    @Autowired
    public AccountHandler(CaseRepository caseRepository,
            PPTransactionRepository ppTransactionRepository, RestTemplate restTemplate) {
        this.caseRepository = caseRepository;
        this.ppTransactionRepository = ppTransactionRepository;
        this.restTemplate = restTemplate;
    }


    public PPAccount getAccountData(String accountName) {
        return restTemplate
                .getForObject(ACCOUNT_URL + ACCOUNT_DEFAULT, PPAccount.class, accountName);
    }

    /**
     * TODO: JavaDoc.
     * @param accountName Description
     * @return Description
     */
    public double checkFunds(String accountName) {
        PPAccount account = restTemplate
                .getForObject(ACCOUNT_URL + ACCOUNT_DEFAULT, PPAccount.class, accountName);
        if (account != null) {
            return account.getAmount();
        }
        return 0;
    }


    public boolean hasValidFunds(Case aCase) {
        return hasValidFunds(aCase.getReceiver().getUsername(), aCase.getPrice());
    }

    boolean hasValidFunds(String accountName, double requestedFunds) {
        double reserved = 0;
        PPAccount account = restTemplate
                .getForObject(ACCOUNT_URL + ACCOUNT_DEFAULT, PPAccount.class, accountName);
        for (Reservation r : account.getReservations()) {
            reserved += r.number;
        }
        return account.getAmount() - reserved >= requestedFunds;
    }

    /**
     * TODO JavaDoc.
     * @param username Description
     * @param amount Description
     * @return
     */
    public double addFunds(String username, double amount) {

        HttpEntity<Double> request = new HttpEntity<>(amount);
        ResponseEntity<Double> responseEntity = restTemplate
                .exchange(ACCOUNT_URL + ACCOUNT_DEFAULT, HttpMethod.POST, request, Double.class,
                        username);
        if (responseEntity.getStatusCode().equals(HttpStatus.ACCEPTED)) {
            return responseEntity.getBody();
        }
        return 0.0;
    }

    public double transferFunds(Case aCase) {
        return transferFunds(aCase.getReceiver().getUsername(), aCase.getOwner().getUsername(),
                aCase.getPrice());
    }

    double transferFunds(String sourceUser, String targetUser, double amount) {

        if (hasValidFunds(sourceUser, amount)) {
            HttpEntity<Double> request = new HttpEntity<>(amount);

            ResponseEntity<Double> responseEntity = restTemplate
                    .exchange(ACCOUNT_URL + "/{sourceAccount}/transfer/{targetAccount}",
                            HttpMethod.POST,
                            request, Double.class, sourceUser, targetUser);

            if (responseEntity.getStatusCode().equals(HttpStatus.ACCEPTED)) {
                return responseEntity.getBody();
            }
        }
        return 0.0;
    }

}
