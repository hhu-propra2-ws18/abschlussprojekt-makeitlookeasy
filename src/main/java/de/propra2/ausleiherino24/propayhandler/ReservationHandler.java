package de.propra2.ausleiherino24.propayhandler;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PPTransactionRepository;
import de.propra2.ausleiherino24.model.Case;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ReservationHandler {

	private static final String RESERVATION_URL = "http://localhost:8888/reservation";
	private RestTemplate restTemplate;
	private AccountHandler accountHandler;
	private PPTransactionRepository ppTransactionRepository;
	private CaseRepository caseRepository;


	public ReservationHandler(PPTransactionRepository ppTransactionRepository, CaseRepository caseRepository,RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.caseRepository = caseRepository;
		this.ppTransactionRepository = ppTransactionRepository;
		accountHandler = new AccountHandler(caseRepository,ppTransactionRepository,restTemplate);
	}

	public boolean createReservation(Case aCase){
		return createReservation(aCase.getReceiver().getUsername(),aCase.getOwner().getUsername(),new Double(aCase.getDeposit()));
	}

	boolean createReservation(String sourceUser, String targetUser, Double amount) {

		if (accountHandler.hasValidFunds(sourceUser, amount)) {

			HttpEntity<Double> request = new HttpEntity<>(amount);

			ResponseEntity<Double> responseEntity = restTemplate
					.exchange(RESERVATION_URL + "/reserve/{account}/{targetAccount}",
							HttpMethod.POST,
							request, Double.class, sourceUser, targetUser);

			return responseEntity.getStatusCode().equals(HttpStatus.OK) || responseEntity
					.getStatusCode()
					.equals(HttpStatus.CREATED);
		}
		return false;
	}

	boolean releaseReservation(String account, Integer reservationId) {
		HttpEntity<Integer> request = new HttpEntity<>(reservationId);

		ResponseEntity<Integer> responseEntity = restTemplate
				.exchange(RESERVATION_URL + "/release/{account}", HttpMethod.POST, request,
						Integer.class,
						account);

		return responseEntity.getStatusCode().equals(HttpStatus.OK) || responseEntity
				.getStatusCode()
				.equals(HttpStatus.CREATED);
	}

	public boolean punishReservation(String account, Integer reservationId) {
		HttpEntity<Integer> request = new HttpEntity<>(reservationId);

		ResponseEntity<Integer> responseEntity = restTemplate
				.exchange(RESERVATION_URL + "/punish/{account}", HttpMethod.POST, request,
						Integer.class,
						account);

		return responseEntity.getStatusCode().equals(HttpStatus.OK) || responseEntity
				.getStatusCode()
				.equals(HttpStatus.CREATED);
	}

}
