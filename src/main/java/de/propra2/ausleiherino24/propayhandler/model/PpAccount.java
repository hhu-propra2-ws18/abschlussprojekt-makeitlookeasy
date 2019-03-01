package de.propra2.ausleiherino24.propayhandler.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PpAccount {

    String account;
    double amount;
    List<Reservation> reservations;

    /**
     * calculates the total amount of all reservations.
     */
    public double reservationAmount() {
        double reserved = 0;
        for (final Reservation reservation : reservations) {
            reserved += reservation.getAmount();
        }
        return reserved;
    }
}
