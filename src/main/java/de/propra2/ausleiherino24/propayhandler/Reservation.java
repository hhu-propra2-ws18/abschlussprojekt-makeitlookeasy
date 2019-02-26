package de.propra2.ausleiherino24.propayhandler;

import lombok.Data;

/**
 * Reservierungen die in den Accounts von Propay enthalten sind.
 */
@Data
class Reservation {

    Long id;
    Double amount;

    Reservation() {
        this.id = 0L;
        this.amount = 0D;
    }

    Reservation(final Long reservationId, final Double number) {
        this.id = reservationId;
        this.amount = number;
    }

}
