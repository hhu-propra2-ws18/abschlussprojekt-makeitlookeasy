package de.propra2.ausleiherino24.propayhandler;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class ReservationHandler {
	private RestTemplate restTemplate = new RestTemplate();
	private static final String RESERVATION_URL= "localhost:8888/reservation";
	private AccountHandler accountHandler = new AccountHandler();

	public boolean createReservation(String sourceUser, String targetUser, double amount){
		if(accountHandler.hasValidFunds(sourceUser,amount)) {
			ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			restTemplate = new RestTemplate(requestFactory);
			HttpEntity<Double> request = new HttpEntity<>(amount);
			ResponseEntity<Double> responseEntity = restTemplate.exchange(RESERVATION_URL + "/reserve/{account}/{targetAccount}", HttpMethod.POST, request, Double.class, sourceUser, targetUser);
			return responseEntity.getStatusCode().equals(HttpStatus.OK) || responseEntity.getStatusCode().equals(HttpStatus.CREATED);
		}
		return false;
	}

}
