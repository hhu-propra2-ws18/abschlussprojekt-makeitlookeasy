package de.propra2.ausleiherino24.proPayHandler;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class AccountHandler {
	private RestTemplate restTemplate = new RestTemplate();
	ResponseEntity<Account> accountResponseEntity;
	private static final String ACCOUNT_URL = "localhost:8888/account";
	public boolean hasValidFunds(String accountName, double requestedFunds){
		double reserved = 0;
		Account account = restTemplate.getForObject(ACCOUNT_URL +"/{account}",Account.class, accountName);
		for(Reservation r : account.getReservations()){
			reserved+=r.number;
		}
		return account.getAmount() - reserved >= requestedFunds;
	}
	public boolean addFunds(String username,double amount){
		ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory();
		restTemplate = new RestTemplate(requestFactory);
		HttpEntity<Account> request = new HttpEntity<>(new Account(username, amount));
		accountResponseEntity = restTemplate.exchange(ACCOUNT_URL +"/{account}", HttpMethod.POST, request, Account.class);
		return accountResponseEntity.getStatusCode().equals(HttpStatus.OK);
	}



	private ClientHttpRequestFactory getClientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();

		return clientHttpRequestFactory;
	}


}
