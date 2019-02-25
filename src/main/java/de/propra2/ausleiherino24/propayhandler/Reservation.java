package de.propra2.ausleiherino24.propayhandler;

import lombok.Data;

/**
 * Reservierungen die in den Accounts von Propay enthalten sind.
 */
@Data
class Reservation {

    Long id;
    Double number;

    public Reservation() {
        this.id = 0L;
        this.number = 0D;
    }

    public Reservation(Long id, Double number) {
        this.id = id;
        this.number = number;
    }

}
