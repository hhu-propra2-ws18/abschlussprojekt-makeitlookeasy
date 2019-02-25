package de.propra2.ausleiherino24.propayhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class PPAccountTest {

    private PPAccount acc;

    @Before
    public void init() {
        acc = new PPAccount("Acc1", 20.0);
    }

    @Test
    public void getAmountShouldReturnCorrectAmountIfAmountIsNotNull() {
        Assertions.assertThat(acc.getAmount()).isEqualByComparingTo(20.0);
    }

    @Test
    public void getAmountShouldReturnZeroIfAmountIsNull() {
        acc.setAmount(0D);

        Assertions.assertThat(acc.getAmount()).isEqualByComparingTo(0.0);
    }

    @Test
    public void getReservationsShouldReturnCorrectListOfReservationsIfReservationsIsNotNull() {
        Reservation res1 = new Reservation();
        res1.setId(1L);
        Reservation res2 = new Reservation();
        res2.setId(2L);
        List<Reservation> resList = new ArrayList<>();
        resList.add(res1);
        resList.add(res2);
        acc.setReservations(resList);

        Assertions.assertThat(acc.getReservations()).isEqualTo(resList);
    }

    @Test
    public void getReservationsShouldReturnInitializedListOfReservationsIfReservationsIsNull() {
        Assertions.assertThat(acc.getReservations()).isEqualTo(new ArrayList<>());
    }

    @Test
    public void test() {
        acc.addReservation(80.0);
        acc.addReservation(45.0);

        Assertions.assertThat(acc.getReservations())
                .isEqualTo(Arrays.asList(new Reservation(1L, 80.0), new Reservation(2L, 45.0)));
        Assertions.assertThat(acc.reservationAmount()).isGreaterThan(124.0);
    }

}
