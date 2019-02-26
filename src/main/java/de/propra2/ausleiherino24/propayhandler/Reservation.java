package de.propra2.ausleiherino24.propayhandler;

import lombok.Data;

/**
 * Reservierungen die in den Accounts von Propay enthalten sind.
 */
@Data
class Reservation {

    Long id;
    Double amount;

    /**
     * base constructor.
     */
    Reservation() {
        this.id = 0L;
        this.amount = 0D;
    }

    /**
     * @param reservationId id of reservation in Propay.
     * @param amount amount of reservation.
     */
    Reservation(final Long reservationId, final Double amount) {
        this.id = reservationId;
        this.amount = amount;
    }

}
