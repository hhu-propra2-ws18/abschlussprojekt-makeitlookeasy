package de.propra2.ausleiherino24.propayhandler.model;

import lombok.Data;

/**
 * Reservierungen die in den Accounts von Propay enthalten sind.
 */
@Data
public class Reservation {

    Long id;
    Double amount;

    /**
     * Full constructor.
     *
     * @param reservationId id of reservation in Propay.
     * @param amount amount of reservation.
     */
    public Reservation(final Long reservationId, final Double amount) {
        this.id = reservationId;
        this.amount = amount;
    }

}
