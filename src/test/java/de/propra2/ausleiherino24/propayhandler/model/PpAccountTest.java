package de.propra2.ausleiherino24.propayhandler.model;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class PpAccountTest {

    private PpAccount testAcc1;
    private List<Reservation> reservations;


    @BeforeEach
    public void initialize() {
        testAcc1 = new PpAccount("Acc1", 100.0, new ArrayList<>());
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
