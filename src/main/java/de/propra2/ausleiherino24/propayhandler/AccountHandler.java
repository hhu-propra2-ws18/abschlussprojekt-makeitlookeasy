package de.propra2.ausleiherino24.propayhandler;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class AccountHandler {
	private RestTemplate restTemplate = new RestTemplate();
	private static final String ACCOUNT_URL = "localhost:8888/account";

	public boolean hasValidFunds(String accountName, double requestedFunds){
		double reserved = 0;
		PPAccount account = restTemplate.getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, accountName);

		for(Reservation r : account.getReservations()){
			reserved+=r.number;
		}

		return account.getAmount() - reserved >= requestedFunds;
	}

	public boolean addFunds(String username, double amount){

		ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		restTemplate = new RestTemplate(requestFactory);
		HttpEntity<Double> request = new HttpEntity<>(amount);

		ResponseEntity<Double> responseEntity = restTemplate.exchange(ACCOUNT_URL +"/{account}", HttpMethod.POST, request, Double.class, username);

		return responseEntity.getStatusCode().equals(HttpStatus.OK);
	}

	public boolean transferFunds(String sourceUser, String targetUser, double amount){

		if(hasValidFunds(sourceUser,amount)) {
			ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			restTemplate = new RestTemplate(requestFactory);
			HttpEntity<Double> request = new HttpEntity<>(amount);

			ResponseEntity<Double> responseEntity = restTemplate.exchange(ACCOUNT_URL + "/{sourceAccount}/transfer/{targetAccount}", HttpMethod.POST, request, Double.class, sourceUser, targetUser);

			return responseEntity.getStatusCode().equals(HttpStatus.OK) || responseEntity.getStatusCode().equals(HttpStatus.CREATED);
		}
		return false;
	}

}
