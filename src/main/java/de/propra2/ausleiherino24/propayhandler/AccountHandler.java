package de.propra2.ausleiherino24.propayhandler;

import de.propra2.ausleiherino24.model.Case;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * gets Propay account Data, adds and transfers Funds except for reservation punishments.
 */
@Service
public class AccountHandler {

    private static final String ACCOUNT_URL = "http://localhost:8888/account";
    private static final String ACCOUNT_DEFAULT = "/{account}";
    private final RestTemplate restTemplate;

    /**
     * Autowired constructor.
     * @param restTemplate used to send requests to propay
     */
    @Autowired
    public AccountHandler(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    /**
     * Gets Propay Account.
     *
     * @param accountName name of account to get
     * @return returns all data of PropayAccount
     */
    PPAccount getAccountData(final String accountName) {
        return restTemplate
                .getForObject(ACCOUNT_URL + ACCOUNT_DEFAULT, PPAccount.class, accountName);
    }

    /**
     * Checks the Funds of an account by the accountName.
     * @param accountName account for which funds are to be checked
     * @return returns amount of unreserved Funds
     */
    public double checkFunds(final String accountName) {
        final PPAccount account = getAccountData(accountName);
        return account.getAmount() - account.reservationAmount();
    }


    /**
     * Checks if lender has enough money to proceed with case.
     *
     * @param acase contains necessary Data
     * @return has enough funds to proceed with Case?
     */
    public boolean hasValidFundsByCase(final Case acase) {
        return hasValidFunds(acase.getReceiver().getUsername(),
                acase.getPpTransaction().getTotalPayment());
    }

    /**
     * Checks whether the account given by its accountName has more or equal money than
     * requestFunds.
     * @param accountName name of user to be checked
     * @param requestedFunds amount of requested Funds to be compared with actual free Funds
     * @return has requested Funds?
     */
    public boolean hasValidFunds(final String accountName, final double requestedFunds) {
        return checkFunds(accountName) >= requestedFunds;
    }

    /**
     * Adds funds to user account.
     *
     * @param username user to get funds
     * @param amount amount to be added
     */
    public void addFunds(final String username, final Double amount) {

        restTemplate.postForLocation(ACCOUNT_URL + ACCOUNT_DEFAULT + "?amount=" + amount.toString(),
                null, username);
    }

    /**
     * Calls transfer for lending cost of case with necessary Data.
     *
     * @param currentCase case containing all necessary Data
     */
    public void transferFundsByCase(final Case currentCase) {
        transferFunds(currentCase.getReceiver().getUsername(), currentCase.getOwner().getUsername(),
                currentCase.getPpTransaction().getLendingCost());
    }


    /**
     * Transfers funds from source to target.
     *
     * @param sourceUser user to give Funds
     * @param targetUser user to receive Funds
     * @param amount amount to be transfered
     */
    private void transferFunds(final String sourceUser, final String targetUser,
            final Double amount) {

        restTemplate.postForLocation(
                ACCOUNT_URL + "/{sourceAccount}/transfer/{targetAccount}" + "?amount=" + amount
                        .toString(), null, sourceUser, targetUser);
    }

}
