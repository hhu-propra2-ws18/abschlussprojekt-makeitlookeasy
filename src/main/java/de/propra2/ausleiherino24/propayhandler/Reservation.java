package de.propra2.ausleiherino24.propayhandler;

import lombok.Data;

/**
 * Reservierungen die in den Accounts von Propay enthalten sind.
 */
@Data
class Reservation {

	Integer id;
	Double number;

	public Reservation() {
		this.id = 0;
		this.number = 0D;
	}

	public Reservation(Integer id, Double number) {
		this.id = id;
		this.number = number;
	}

}
