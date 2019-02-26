package de.propra2.ausleiherino24.propayhandler;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.PPTransaction;
import de.propra2.ausleiherino24.model.User;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
public class ReservationHandlerTest {

    private static final String RESERVATION_URL = "http://localhost:8888/reservation";
    private Reservation reservation1;
    private Reservation reservation2;
    private PPTransaction ppTransaction;
    private User user;
    private Case aCase;
    private RestTemplate restTemplate;
    private List<Reservation> resList;
    private ReservationHandler reservationHandler;
    @MockBean
    private CaseRepository caseRepository;
    @MockBean
    private AccountHandler accountHandler;

    @Before
    public void init() {
        user = Mockito.mock(User.class);
        ppTransaction = Mockito.mock(PPTransaction.class);
        aCase = Mockito.mock(Case.class);
        restTemplate = Mockito.mock(RestTemplate.class);
        reservationHandler = new ReservationHandler(caseRepository, restTemplate);

        Mockito.when(aCase.getPpTransaction()).thenReturn(ppTransaction);
        Mockito.when(ppTransaction.getReservationId()).thenReturn(1L);
        Mockito.when(aCase.getReceiver()).thenReturn(user);
        Mockito.when(user.getUsername()).thenReturn("user");
    }

    @Test
    public void releaseReservationByCaseCallsNestedMethodAndSendsCorrectRequest() {

        reservationHandler.releaseReservationByCase(aCase);
        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(RESERVATION_URL + "/release/{account}?reservationId={reservationId}",
                        HttpMethod.POST, null,
                        PPAccount.class, "user", "1");
    }

}
