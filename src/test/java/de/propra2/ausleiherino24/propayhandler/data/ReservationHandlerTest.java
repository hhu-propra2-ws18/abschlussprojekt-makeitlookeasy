package de.propra2.ausleiherino24.propayhandler.data;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.model.PpAccount;
import de.propra2.ausleiherino24.propayhandler.model.PpTransaction;
import de.propra2.ausleiherino24.propayhandler.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
class ReservationHandlerTest {

    @Value("${PP_RESERVATION_URL}")
    private String RESERVATION_URL;
    @Value("${PP_ACCOUNT_URL}")
    private String ACCOUNT_URL;
    private ResponseEntity<Reservation> reservationResp1;
    private Case aCase;
    private RestTemplate restTemplate;
    private ReservationHandler reservationHandler;
    @MockBean
    private CaseRepository caseRepository;

    @BeforeEach
    void init() {
        Reservation reservation1 = Mockito.mock(Reservation.class);
        reservationResp1 = Mockito.mock(ResponseEntity.class);
        User user = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        PpTransaction ppTransaction = Mockito.mock(PpTransaction.class);
        aCase = Mockito.mock(Case.class);
        restTemplate = Mockito.mock(RestTemplate.class);
        reservationHandler = new ReservationHandler(caseRepository, restTemplate, RESERVATION_URL,
                ACCOUNT_URL);

        Mockito.when(reservationResp1.getBody()).thenReturn(reservation1);
        Mockito.when(reservation1.getId()).thenReturn(1L);

        Mockito.when(ppTransaction.getReservationId()).thenReturn(1L);
        Mockito.when(ppTransaction.getLendingCost()).thenReturn(100D);

        Mockito.when(aCase.getPpTransaction()).thenReturn(ppTransaction);
        Mockito.when(aCase.getReceiver()).thenReturn(user);
        Mockito.when(aCase.getOwner()).thenReturn(user2);
        Mockito.when(aCase.getDeposit()).thenReturn(200D);
        Mockito.when(user.getUsername()).thenReturn("user");
        Mockito.when(user2.getUsername()).thenReturn("user2");

    }

    @Test
    void releaseReservationByCaseSendsCorrectRequest() {

        reservationHandler.releaseReservationByCase(aCase);
        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(RESERVATION_URL + "/release/{account}?reservationId={reservationId}",
                        HttpMethod.POST, null,
                        PpAccount.class, "user", "1");
    }

    @Test
    void punishReservationByCaseSendsCorrectRequest() {

        reservationHandler.punishReservationByCase(aCase);
        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(RESERVATION_URL + "/punish/{account}?reservationId={reservationId}",
                        HttpMethod.POST, null,
                        PpAccount.class, "user", "1");
    }

    @Test
    void handleReservedMoneyShouldCallCreateReservationWhenCaseIsRequested() {

        Mockito.when(aCase.getRequestStatus()).thenReturn(Case.REQUESTED);
        Mockito.when(restTemplate
                .exchange(RESERVATION_URL + "/reserve/{account}/{targetAccount}?amount={amount}",
                        HttpMethod.POST, null,
                        Reservation.class, "user", "user2", Double.toString(300)))
                .thenReturn(reservationResp1);

        reservationHandler.handleReservedMoney(aCase);

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(RESERVATION_URL + "/reserve/{account}/{targetAccount}?amount={amount}",
                        HttpMethod.POST, null,
                        Reservation.class, "user", "user2", Double.toString(300));
    }

    @Test
    void handleReservedMoneyShouldCallCreateReservationWhenCaseIsAccepted() {
        Mockito.when(aCase.getRequestStatus()).thenReturn(Case.REQUEST_ACCEPTED);
        Mockito.when(restTemplate
                .exchange(RESERVATION_URL + "/reserve/{account}/{targetAccount}?amount={amount}",
                        HttpMethod.POST, null,
                        Reservation.class, "user", "user2", Double.toString(200)))
                .thenReturn(reservationResp1);

        reservationHandler.handleReservedMoney(aCase);

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(RESERVATION_URL + "/reserve/{account}/{targetAccount}?amount={amount}",
                        HttpMethod.POST, null,
                        Reservation.class, "user", "user2", Double.toString(200));
    }

    @Test
    void handleReservedMoneyShouldCallReleaseReservationWhenCaseIsAccepted() {

        Mockito.when(aCase.getRequestStatus()).thenReturn(Case.REQUEST_ACCEPTED);
        Mockito.when(restTemplate
                .exchange(RESERVATION_URL + "/reserve/{account}/{targetAccount}?amount={amount}",
                        HttpMethod.POST, null,
                        Reservation.class, "user", "user2", Double.toString(200)))
                .thenReturn(reservationResp1);

        reservationHandler.handleReservedMoney(aCase);

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(RESERVATION_URL + "/release/{account}?reservationId={reservationId}",
                        HttpMethod.POST, null,
                        PpAccount.class, "user", "1");
    }

}
