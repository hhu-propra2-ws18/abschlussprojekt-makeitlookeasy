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

    PPAccount(String account, Double amount) {
        this.account = account;
        this.amount = amount;
    }

    double reservationAmount() {
        double reserved = 0;
        for (int i = 0; i < reservations.size(); i++) {
            reserved += reservations.get(i).getAmount();
        }
        return reserved;
    }

    public void addReservation(Double amount) {
        this.reservations.add(new Reservation(this.reservations.size() + 1L, amount));
    }
}
