package de.propra2.ausleiherino24.proPayHandler;

import java.util.List;

class Account {
	public Account(String account,double number){
		this.account = account;
		this.number = number;
	}
	String account;
	double number;
	List<Reservation> reservations;
	double	getAmount(){
		return number;
	}
	List<Reservation> getReservations(){
		return this.reservations;
	}
}
