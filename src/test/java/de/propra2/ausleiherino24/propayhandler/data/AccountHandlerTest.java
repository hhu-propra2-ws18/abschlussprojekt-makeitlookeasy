package de.propra2.ausleiherino24.propayhandler.data;

import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
import de.propra2.ausleiherino24.propayhandler.model.PpAccount;
import de.propra2.ausleiherino24.propayhandler.model.PpTransaction;
import de.propra2.ausleiherino24.propayhandler.model.Reservation;
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
class AccountHandlerTest {

    private static final String ACCOUNT_URL = "http://localhost:8888/account";
    private static final String ACCOUNT_DEFAULT = "/{account}";
    private AccountHandler accountHandler;
    @MockBean
    private RestTemplate restTemplate;

    private PpAccount testAcc1;
    private Case aCase;
    private Double amount;

    @BeforeEach
    void initialize() {
        restTemplate = Mockito.mock(RestTemplate.class);
        accountHandler = new AccountHandler(restTemplate);
        User user = Mockito.mock(User.class);
        User user2 = Mockito.mock(User.class);
        PpTransaction ppTransaction = Mockito.mock(PpTransaction.class);
        aCase = Mockito.mock(Case.class);
        testAcc1 = new PpAccount("Acc1", 100.0, new ArrayList<>());
        PpAccount testAcc2 = new PpAccount("Acc2", 1000.0, new ArrayList<>());
        PpAccount testAcc3 = new PpAccount("Acc3", 0.0, new ArrayList<>());
        amount = 100D;

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
    void checkGetAccountData() {

        Assertions.assertThat(accountHandler.getAccountData("Acc1")).isEqualTo(testAcc1);
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc1");
    }


    @Test
    void checkFundsTestIfZero() {

        Assertions.assertThat(accountHandler.checkFunds("Acc3")).isZero();
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc3");
    }

    @Test
    void checkFundsTestIfAmount() {

        Assertions.assertThat(accountHandler.checkFunds("Acc1")).isEqualByComparingTo(100.0);
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc1");
    }

    @Test
    void hasValidFundsWorksWithoutReservationsIfValid() {
        Assertions.assertThat(accountHandler.hasValidFunds("Acc1", 99.0)).isTrue();
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc1");
    }

    @Test
    void hasValidFundsFailsWithoutReservationsIfFundsNotValid() {
        Assertions.assertThat(accountHandler.hasValidFunds("Acc1", 101.0)).isFalse();
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc1");
    }

    @Test
    void hasValidFundsWorksWithReservationsIfValid() {

        Assertions.assertThat(accountHandler.hasValidFunds("Acc2", 39.0)).isTrue();
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc2");
    }

    @Test
    void hasValidFundsFailsWithReservationsIfFundsNotValid() {

        Assertions.assertThat(accountHandler.hasValidFunds("Acc2", 41.0)).isFalse();
        Mockito.verify(restTemplate, Mockito.times(1))
                .getForObject(ACCOUNT_URL + "/{account}", PpAccount.class, "Acc2");
    }

    @Test
    void addFundsPostsCorrectRequest() {

        accountHandler.addFunds("Acc1", amount);
        Mockito.verify(restTemplate, Mockito.times(1))
                .postForLocation(ACCOUNT_URL + ACCOUNT_DEFAULT + "?amount=" + amount.toString(),
                        null, "Acc1");

    }

    @Test
    void transferFundsByCasePostsCorrectRequest() {
        accountHandler.transferFundsByCase(aCase);
        Mockito.verify(restTemplate, Mockito.times(1))
                .postForLocation(
                        ACCOUNT_URL + "/{sourceAccount}/transfer/{targetAccount}" + "?amount="
                                + amount
                                .toString(), null, "user", "user2");
    }

}
