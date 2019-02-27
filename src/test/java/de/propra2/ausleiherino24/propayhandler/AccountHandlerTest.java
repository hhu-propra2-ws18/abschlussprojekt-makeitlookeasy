package de.propra2.ausleiherino24.propayhandler;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
public class AccountHandlerTest {

    private static final String ACCOUNT_URL = "http://localhost:8888/account";
    private AccountHandler accountHandler;
    @MockBean
    private RestTemplate restTemplate;

    private PpAccount testAcc1;
    private PpAccount testAcc2;
    private PpAccount testAcc3;

    @BeforeEach
    public void initialize() {
        restTemplate = Mockito.mock(RestTemplate.class);
        accountHandler = new AccountHandler(restTemplate);
        testAcc1 = new PpAccount("Acc1", 100.0, new ArrayList<>());
        testAcc2 = new PpAccount("Acc2", 1000.0, new ArrayList<>());
        testAcc3 = new PpAccount("Acc3", 0.0, new ArrayList<>());

        final List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation(1L, 950.0));
        reservations.add(new Reservation(2L, 10.0));
        testAcc2.setReservations(reservations);

        Mockito.when(restTemplate.getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc1"))
                .thenReturn(testAcc1);
        Mockito.when(restTemplate.getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc2"))
                .thenReturn(testAcc2);
        Mockito.when(restTemplate.getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc3"))
                .thenReturn(testAcc3);
    }

    @Test
    public void checkGetAccountData() {

        Assertions.assertThat(accountHandler.getAccountData("Acc1")).isEqualTo(testAcc1);
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc1");
    }


    @Test
    public void checkFundsTestIfZero() {

        Assertions.assertThat(accountHandler.checkFunds("Acc3")).isZero();
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc3");
    }

    @Test
    public void checkFundsTestIfAmount() {

        Assertions.assertThat(accountHandler.checkFunds("Acc1")).isEqualByComparingTo(100.0);
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc1");
    }

    @Test
    public void hasValidFundsWorksWithoutReservationsIfValid() {
        Assertions.assertThat(accountHandler.hasValidFunds("Acc1", 99.0)).isTrue();
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc1");
    }

    @Test
    public void hasValidFundsFailsWithoutReservationsIfFundsNotValid() {
        Assertions.assertThat(accountHandler.hasValidFunds("Acc1", 101.0)).isFalse();
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc1");
    }

    @Test
    public void hasValidFundsWorksWithReservationsIfValid() {

        Assertions.assertThat(accountHandler.hasValidFunds("Acc2", 39.0)).isTrue();
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc2");
    }

    @Test
    public void hasValidFundsFailsWithReservationsIfFundsNotValid() {

        Assertions.assertThat(accountHandler.hasValidFunds("Acc2", 41.0)).isFalse();
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc2");
    }
}
