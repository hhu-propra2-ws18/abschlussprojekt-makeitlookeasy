package de.propra2.ausleiherino24.propayhandler;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
class PPAccount {

    String account;
    double amount;
    List<Reservation> reservations;

    PPAccount(String account, Double amount) {
        this.account = account;
        this.amount = amount;
        this.reservations = new ArrayList<>();
    }

    double reservationAmount() {
        double reserved = 0;
        for (Reservation r : reservations) {
            reserved += r.getNumber();
        }
        return reserved;
    }

    void addReservation(Double amount) {
        this.reservations.add(new Reservation(this.reservations.size() + 1L, amount));
    }
}
