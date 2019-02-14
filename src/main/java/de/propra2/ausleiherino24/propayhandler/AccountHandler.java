package de.propra2.ausleiherino24.propayhandler;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AccountHandler {
	private RestTemplate restTemplate;
	private static final String ACCOUNT_URL = "http://localhost:8888/account";

	public AccountHandler(RestTemplate restTemplate){
		this.restTemplate = restTemplate;
	}

	public double checkFunds(String accountName){
		PPAccount account = restTemplate.getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, accountName);
		if(account != null ){
			return account.getAmount();
		}
		return 0;
	}

	public boolean hasValidFunds(String accountName, double requestedFunds){
		double reserved = 0;
		PPAccount account = restTemplate.getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, accountName);
		for(Reservation r : account.getReservations()){
				reserved+=r.number;
		}
		return account.getAmount() - reserved >= requestedFunds;
	}

	public double addFunds(String username, double amount){

		HttpEntity<Double> request = new HttpEntity<>(amount);
		ResponseEntity<Double> responseEntity = restTemplate.exchange(ACCOUNT_URL +"/{account}", HttpMethod.POST, request, Double.class, username);
		if(responseEntity.getStatusCode().equals(HttpStatus.ACCEPTED)){
			return responseEntity.getBody();
		}
		return 0.0;
	}

	public double transferFunds(String sourceUser, String targetUser, double amount){

		if(hasValidFunds(sourceUser,amount)) {
			restTemplate = new RestTemplate();
			HttpEntity<Double> request = new HttpEntity<>(amount);

			ResponseEntity<Double> responseEntity = restTemplate.exchange(ACCOUNT_URL + "/{sourceAccount}/transfer/{targetAccount}", HttpMethod.POST, request, Double.class, sourceUser, targetUser);

			if(responseEntity.getStatusCode().equals(HttpStatus.ACCEPTED)){
				return responseEntity.getBody();
			}
		}
		return 0.0;
	}

}
