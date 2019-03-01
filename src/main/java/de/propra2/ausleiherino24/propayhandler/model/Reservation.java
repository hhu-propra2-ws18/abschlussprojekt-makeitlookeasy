package de.propra2.ausleiherino24.propayhandler.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reservierungen die in den Accounts von Propay enthalten sind.
 */
@NoArgsConstructor
@Data
public class Reservation {

    Long id;
    Double amount;


    /**
     * Full constructor.
     *
     * @param id id of reservation in Propay.
     * @param amount amount of reservation.
     */
    public Reservation(final Long id, final Double amount) {
        this.id = id;
        this.amount = amount;
    }

}
