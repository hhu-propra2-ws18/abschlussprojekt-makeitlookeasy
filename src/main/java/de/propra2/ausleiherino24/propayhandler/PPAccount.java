package de.propra2.ausleiherino24.propayhandler;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
@Data
class 	PPAccount {

	String account;
	Double number;
	List<Reservation> reservations;

	public PPAccount(String account, double number){
		this.account = account;
		this.number = number;
		this.reservations = new ArrayList<>();
	}


	double	getAmount(){
		if(number == null){
			return 0;
		}
		return number;
	}
	
	List<Reservation> getReservations(){
		if(this.reservations == null){
			return new ArrayList<>();
		}
		return this.reservations;
	}
}
