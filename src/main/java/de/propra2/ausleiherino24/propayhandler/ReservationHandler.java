package de.propra2.ausleiherino24.propayhandler;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Case;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ReservationHandler {

    private static final String RESERVATION_URL = "http://localhost:8888/reservation";
    private RestTemplate restTemplate;
    private AccountHandler accountHandler;

    private CaseRepository caseRepository;

    public ReservationHandler(final CaseRepository caseRepository,
            final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.caseRepository = caseRepository;
        accountHandler = new AccountHandler(restTemplate);
    }

    public void handleReservedMoney(final Case aCase) {
        Long reservationId = -1L;

        if (aCase.getRequestStatus() == Case.REQUESTED) {
            reservationId = createReservation(aCase.getReceiver().getUsername(),
                    aCase.getOwner().getUsername(),
                    aCase.getDeposit() + aCase.getPpTransaction().getLendingCost());
        }

        if (aCase.getRequestStatus() == Case.REQUEST_ACCEPTED) {
            if (aCase.getPpTransaction().getReservationId() != -1L) {
                releaseReservation(aCase);
            }
            accountHandler.transferFunds(aCase);
            reservationId = createReservation(aCase.getReceiver().getUsername(),
                    aCase.getOwner().getUsername(),
                    aCase.getDeposit());
        }

        aCase.getPpTransaction().setReservationId(reservationId);
        caseRepository.save(aCase);
    }

    private Long createReservation(final String sourceUser, final String targetUser,
            final Double amount) {

        final ResponseEntity<Reservation> responseEntity = restTemplate
                .exchange(RESERVATION_URL + "/reserve/{account}/{targetAccount}?amount={amount}",
                        HttpMethod.POST,
                        null, Reservation.class, sourceUser, targetUser, amount.toString());

        return responseEntity.getBody().getId();
    }

    public void releaseReservation(final Case aCase) {
        if (aCase.getPpTransaction().getReservationId() != -1) {
            releaseReservation(aCase.getReceiver().getUsername(),
                    aCase.getPpTransaction().getReservationId());
            aCase.getPpTransaction().setReservationId(-1L);
        }
    }

    private void releaseReservation(final String account, final Long reservationId) {
        restTemplate.exchange(RESERVATION_URL + "/release/{account}?reservationId={reservationId}",
                HttpMethod.POST, null,
                PPAccount.class, account, reservationId.toString());
    }

    public void punishReservation(final Case aCase) {
        punishReservation(aCase.getReceiver().getUsername(),
                aCase.getPpTransaction().getReservationId());
    }

    boolean punishReservation(final String account,
            final Long reservationId) {
        final ResponseEntity<PPAccount> responseEntity = restTemplate
                .exchange(RESERVATION_URL + "/punish/{account}?reservationId={reservationId}\"",
                        HttpMethod.POST, null,
                        PPAccount.class,
                        account, reservationId.toString());

        return responseEntity.getStatusCode().equals(HttpStatus.OK) || responseEntity
                .getStatusCode()
                .equals(HttpStatus.CREATED);
    }

}
