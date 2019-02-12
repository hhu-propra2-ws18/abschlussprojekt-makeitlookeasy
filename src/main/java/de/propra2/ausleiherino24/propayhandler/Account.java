package de.propra2.ausleiherino24.propayhandler;

import java.util.ArrayList;
import java.util.List;

class Account {
	public Account(String account,double number){
		this.account = account;
		this.number = number;
		this.reservations = new ArrayList<>();
	}
	String account;
	double number;
	List<Reservation> reservations;
	double	getAmount(){
		return number;
	}
	List<Reservation> getReservations(){
		if(this.reservations == null){
			return new ArrayList<>();
		}
		return this.reservations;
	}
}
