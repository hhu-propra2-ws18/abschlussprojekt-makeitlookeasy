package de.propra2.ausleiherino24.propayhandler;

import de.propra2.ausleiherino24.data.CaseRepository;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
public class ReservationHandlerTest {

    private static final String RESERVATION_URL = "http://localhost:8888/reservation";
    private Reservation reservation1;
    private Reservation reservation2;
    private RestTemplate restTemplate;
    private List<Reservation> resList;
    private ReservationHandler reservationHandler;
    @MockBean
    private CaseRepository caseRepository;
    @MockBean
    private AccountHandler accountHandler;

    @Before
    public void init() {
        restTemplate = Mockito.mock(RestTemplate.class);
        reservationHandler = new ReservationHandler(caseRepository,
                restTemplate);
    }

    @Test
    public void dummy() {
        Assert.assertTrue(true);
    }

}
