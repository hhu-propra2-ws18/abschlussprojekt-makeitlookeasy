package de.propra2.ausleiherino24.proPayHandler;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class AccountHandler {
	private RestTemplate restTemplate = new RestTemplate();
	ResponseEntity<Account> accountResponseEntity;

	public boolean hasValidFunds(String accountName, double requestedFunds){
		double reserved = 0;
		Account account = restTemplate.getForObject("localhost:8888/account/{account}",Account.class, accountName);
		for(Reservation r : account.getReservations()){
			reserved+=r.number;
		}
		return account.getAmount() - reserved >= requestedFunds;
	}
	public void addFunds(String username,double amount){
		ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory();
		restTemplate = new RestTemplate(requestFactory);
		HttpEntity<Account> request = new HttpEntity<>(new Account(username, amount));
		restTemplate.postForObject("localhost:8888/account/{account}", request, Account.class);
	}



	private ClientHttpRequestFactory getClientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();

		return clientHttpRequestFactory;
	}


}
