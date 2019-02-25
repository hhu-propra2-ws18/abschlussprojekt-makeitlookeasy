package de.propra2.ausleiherino24.propayhandler;

import de.propra2.ausleiherino24.model.Case;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountHandler {

    private static final String ACCOUNT_URL = "http://localhost:8888/account";
    private static final String ACCOUNT_DEFAULT = "/{account}";
    private RestTemplate restTemplate;

    /**
     * TODO: Javadoc.
     *
     * @param restTemplate Description
     */
    @Autowired
    public AccountHandler(RestTemplate restTemplate) {
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
        PPAccount account;
        account = restTemplate
                .getForObject(ACCOUNT_URL + ACCOUNT_DEFAULT, PPAccount.class, accountName);
        return account.getAmount() - account.reservationAmount();
    }


    public boolean hasValidFunds(Case aCase) {
        return hasValidFunds(aCase.getReceiver().getUsername(),
                aCase.getPpTransaction().getTotalPayment());
    }

    public boolean hasValidFunds(String accountName, double requestedFunds) {
        return checkFunds(accountName) >= requestedFunds;
    }

    /**
     * TODO JavaDoc.
     * @param username Description
     * @param amount Description
     * @return
     */
    public void addFunds(String username, Double amount) {

        HttpEntity<Double> request = new HttpEntity<>(amount); //TODO: weg?
        restTemplate.postForLocation(ACCOUNT_URL + ACCOUNT_DEFAULT + "?amount=" + amount.toString(),
                request, username);
    }

    public void transferFunds(Case aCase) {
        transferFunds(aCase.getReceiver().getUsername(), aCase.getOwner().getUsername(),
                aCase.getPpTransaction().getLendingCost());
    }

    void transferFunds(String sourceUser, String targetUser, Double amount) {

        HttpEntity<Double> request = new HttpEntity<>(amount); //TODO: weg?
        restTemplate.postForLocation(
                ACCOUNT_URL + "/{sourceAccount}/transfer/{targetAccount}" + "?amount=" + amount
                        .toString(), request, sourceUser, targetUser);
    }

}
