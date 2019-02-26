package de.propra2.ausleiherino24.propayhandler;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class PPAccount {

    String account;
    double amount;
    List<Reservation> reservations;

    PPAccount(final String account, final Double amount) {
        this.account = account;
        this.amount = amount;
    }

    double reservationAmount() {
        double reserved = 0;
        for (final Reservation reservation : reservations) {
            reserved += reservation.getAmount();
        }
        return reserved;
    }

    void addReservation(final Double amount) {
        this.reservations.add(new Reservation(this.reservations.size() + 1L, amount));
    }
}
