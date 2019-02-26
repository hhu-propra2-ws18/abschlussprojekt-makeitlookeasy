package de.propra2.ausleiherino24.propayhandler;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class PPAccountTest {

    private PPAccount testAcc1;
    private List<Reservation> reservations;


    @Before
    public void initialize() {
        testAcc1 = new PPAccount("Acc1", 100.0, new ArrayList<>());
        reservations = new ArrayList<>();
        testAcc1.setReservations(reservations);
    }
    @Test
    public void reservationAmountShouldBeZeroWithNoReservations() {

        Assertions.assertThat(testAcc1.reservationAmount()).isEqualTo(0.0);
    }

    @Test
    public void reservationAmountShouldReturnTotalReservationAmount() {
        reservations.add(new Reservation(1L, 95.0));
        reservations.add(new Reservation(2L, 3.0));
        Assertions.assertThat(testAcc1.reservationAmount()).isEqualTo(98.0);
    }
}
