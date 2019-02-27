package de.propra2.ausleiherino24.propayhandler;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Case;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


/*
    handles all Reservation Interaction with Propay
    Also calls transfer of accountHandler to ensure the amount is booked without
    interruptions after releasing the reservation for the initial cost.
 */
@Component
public class ReservationHandler {

    private static final String RESERVATION_URL = "http://localhost:8888/reservation";
    private final RestTemplate restTemplate;
    private final AccountHandler accountHandler;
    private final CaseRepository caseRepository;


    /**
     * Constructor.
     *
     * @param caseRepository needed to update reservationIds of PPTransactions via Cases
     * @param restTemplate needed for propay requests accountHandler needed to transfer Funds in
     *      between Reservations.
     */
    public ReservationHandler(final CaseRepository caseRepository,
            final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.caseRepository = caseRepository;
        accountHandler = new AccountHandler(restTemplate);
    }


    /**
     * createReservation when case is requested releases old reservation, calls transfer and creates
     * new reservation for deposit if case was Accepted. Handling in one Method due to Test data
     * compromising the process and for better usage saves ReservationId to remember which
     * reservation belongs to the case/transaction
     *
     * @param currentCase contains all necessary data to process what should be done calls
     */
    public void handleReservedMoney(final Case currentCase) {
        Long reservationId = -1L;

        if (currentCase.getRequestStatus() == Case.REQUESTED) {
            reservationId = createReservation(currentCase.getReceiver().getUsername(),
                    currentCase.getOwner().getUsername(),
                    currentCase.getDeposit() + currentCase.getPpTransaction().getLendingCost());
        }

        if (currentCase.getRequestStatus() == Case.REQUEST_ACCEPTED) {
            if (currentCase.getPpTransaction().getReservationId() != -1L) {
                releaseReservationByCase(currentCase);
            }
            accountHandler.transferFundsByCase(currentCase);
            reservationId = createReservation(currentCase.getReceiver().getUsername(),
                    currentCase.getOwner().getUsername(),
                    currentCase.getDeposit());
        }

        currentCase.getPpTransaction().setReservationId(reservationId);
        caseRepository.save(currentCase);
    }

    /**
     * creates Reservation.
     *
     * @param sourceUser user for which the reservation will be created
     * @param targetUser user the reservation is pointing to
     * @param amount amount that should be reserved from source to target user
     * @return reservationId to remember which reservation belongs to the Case
     */
    private Long createReservation(final String sourceUser, final String targetUser,
            final Double amount) {

        final ResponseEntity<Reservation> responseEntity = restTemplate
                .exchange(RESERVATION_URL + "/reserve/{account}/{targetAccount}?amount={amount}",
                        HttpMethod.POST,
                        null, Reservation.class, sourceUser, targetUser, amount.toString());

        return responseEntity.getBody().getId();
    }

    /**
     * completely releases a reservation for a Case (calls method for case parameters).
     *
     * @param currentCase contains all necessary Data to release according reservation
     */
    public void releaseReservationByCase(final Case currentCase) {
        if (currentCase.getPpTransaction().getReservationId() != -1) {
            releaseReservation(currentCase.getReceiver().getUsername(),
                    currentCase.getPpTransaction().getReservationId());
            currentCase.getPpTransaction().setReservationId(-1L);
        }
    }

    /**
     * releases reservation with reservationId on Propay Account account.
     *
     * @param account account on which the reservation is to be released
     * @param reservationId id of reservation to be released
     */
    private void releaseReservation(final String account, final Long reservationId) {
        restTemplate.exchange(RESERVATION_URL + "/release/{account}?reservationId={reservationId}",
                HttpMethod.POST, null,
                PpAccount.class, account, reservationId.toString());
    }

    /**
     * calls punishReservationByCase with case parameters.
     *
     * @param currentCase contains all necessary data to do request
     */
    public void punishReservationByCase(final Case currentCase) {
        punishReservation(currentCase.getReceiver().getUsername(),
                currentCase.getPpTransaction().getReservationId());
    }


    /**
     * punishes reservation with reservationId from account to previously defined target account
     * this account was saved by propay when creating the reservation.
     *
     * @param account account to be punished
     * @param reservationId reservation that will be punished
     */
    private void punishReservation(final String account,
            final Long reservationId) {
        restTemplate.exchange(RESERVATION_URL + "/punish/{account}?reservationId={reservationId}",
                HttpMethod.POST, null,
                PpAccount.class,
                account, reservationId.toString());
    }

}
