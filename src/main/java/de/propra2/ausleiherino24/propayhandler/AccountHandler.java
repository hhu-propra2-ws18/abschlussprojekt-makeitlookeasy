package de.propra2.ausleiherino24.propayhandler;

import de.propra2.ausleiherino24.model.Case;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountHandler {

    private static final String ACCOUNT_URL = "http://localhost:8888/account";
    private static final String ACCOUNT_DEFAULT = "/{account}";
    private RestTemplate restTemplate;

    @Autowired
    public AccountHandler(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    PPAccount getAccountData(final String accountName) {
        return restTemplate
                .getForObject(ACCOUNT_URL + ACCOUNT_DEFAULT, PPAccount.class, accountName);
    }

    public double checkFunds(final String accountName) {
        final PPAccount account = getAccountData(accountName);
        return account.getAmount() - account.reservationAmount();
    }


    public boolean hasValidFunds(final Case acase) {
        return hasValidFunds(acase.getReceiver().getUsername(),
                acase.getPpTransaction().getTotalPayment());
    }

    public boolean hasValidFunds(final String accountName, final double requestedFunds) {
        return checkFunds(accountName) >= requestedFunds;
    }

    //TODO: Used? Fix!
    public void addFunds(final String username, final Double amount) {

        restTemplate.postForLocation(ACCOUNT_URL + ACCOUNT_DEFAULT + "?amount=" + amount.toString(),
                null, username);
    }

    //TODO: Method extraction necessary? Discuss!
    void transferFunds(final Case c) {
        transferFunds(c.getReceiver().getUsername(), c.getOwner().getUsername(),
                c.getPpTransaction().getLendingCost());
    }

    private void transferFunds(final String sourceUser, final String targetUser,
            final Double amount) {

        restTemplate.postForLocation(
                ACCOUNT_URL + "/{sourceAccount}/transfer/{targetAccount}" + "?amount=" + amount
                        .toString(), null, sourceUser, targetUser);
    }

}
