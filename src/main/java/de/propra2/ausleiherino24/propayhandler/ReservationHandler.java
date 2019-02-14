package de.propra2.ausleiherino24.propayhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ReservationHandler {
	
	@Autowired
	public ReservationHandler(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		accountHandler = new AccountHandler(restTemplate);
	}
	
	private RestTemplate restTemplate;
	private static final String RESERVATION_URL = "localhost:8888/reservation";
	private AccountHandler accountHandler;

	public boolean createReservation(String sourceUser, String targetUser, double amount){

		if(accountHandler.hasValidFunds(sourceUser,amount)) {

			HttpEntity<Double> request = new HttpEntity<>(amount);

			ResponseEntity<Double> responseEntity = restTemplate.exchange(RESERVATION_URL + "/reserve/{account}/{targetAccount}", HttpMethod.POST, request, Double.class, sourceUser, targetUser);

			return responseEntity.getStatusCode().equals(HttpStatus.OK) || responseEntity.getStatusCode().equals(HttpStatus.CREATED);
		}
		return false;
	}
	
	public boolean releaseReservation(String account, int reservationId){
		HttpEntity<Integer> request = new HttpEntity<>(reservationId);

		ResponseEntity<Integer> responseEntity = restTemplate.exchange(RESERVATION_URL + "/release/{account}", HttpMethod.POST, request, Integer.class, account);

		return responseEntity.getStatusCode().equals(HttpStatus.OK) || responseEntity.getStatusCode().equals(HttpStatus.CREATED);
	}
	
	public boolean punishReservation(String account, int reservationId){
		HttpEntity<Integer> request = new HttpEntity<>(reservationId);

		ResponseEntity<Integer> responseEntity = restTemplate.exchange(RESERVATION_URL + "/punish/{account}", HttpMethod.POST, request, Integer.class, account);

		return responseEntity.getStatusCode().equals(HttpStatus.OK) || responseEntity.getStatusCode().equals(HttpStatus.CREATED);
	}

}
