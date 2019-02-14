package de.propra2.ausleiherino24.propayhandler;

import java.util.ArrayList;
import java.util.List;

/**
 * ProPay Account
 */
class 	PPAccount {
	private String account;
	private double number;
	private List<Reservation> reservations;

	public PPAccount(String account, double number){
		this.account = account;
		this.number = number;
		this.reservations = new ArrayList<>();
	}

	double	getAmount(){
		return number;
	}

	// TODO Reservierungen sind?
	List<Reservation> getReservations(){
		if(this.reservations == null){
			return new ArrayList<>();
		}
		return this.reservations;
	}
}
