package de.propra2.ausleiherino24.propayhandler;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
    PPAccounts received from Propay
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class PPAccount {

    String account;
    double amount;
    List<Reservation> reservations;

    /*
        returns amount of all reservations added up
     */
    double reservationAmount() {
        double reserved = 0;
        for (final Reservation reservation : reservations) {
            reserved += reservation.getAmount();
        }
        return reserved;
    }
}
