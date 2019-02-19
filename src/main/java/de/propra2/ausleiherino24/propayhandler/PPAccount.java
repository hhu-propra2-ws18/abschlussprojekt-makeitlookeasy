package de.propra2.ausleiherino24.propayhandler;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
class PPAccount {

	String account;
	Double number;
	List<Reservation> reservations;

	public PPAccount() {
		this.account = "";
		this.number = 0D;
		this.reservations = new ArrayList<>();
	}

	PPAccount(String account, Double number) {
		this.account = account;
		this.number = number;
		this.reservations = new ArrayList<>();
	}


	double getAmount() {
		if (number == null) {
			return 0;
		}
		return number;
	}

	List<Reservation> getReservations() {
		if (this.reservations == null) {
			return new ArrayList<>();
		}
		return this.reservations;
	}

	void addReservation(Double amount) {
		this.reservations.add(new Reservation(this.reservations.size() + 1, amount));
	}
}
