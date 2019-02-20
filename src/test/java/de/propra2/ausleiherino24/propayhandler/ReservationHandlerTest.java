package de.propra2.ausleiherino24.propayhandler;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PPTransactionRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
public class ReservationHandlerTest {

	private final String ACCOUNT_URL = "http://localhost:8888/account";
	private final String RESERVATION_URL = "http://localhost:8888/reservation";
	private Reservation res1 = new Reservation();
	private Reservation res2 = new Reservation();
	private RestTemplate restTemplate;
	private ResponseEntity<Integer> responseEntityMockInt;
	private ResponseEntity<Double> responseEntityMockDouble;
	private ReservationHandler reservationHandler;
	private PPAccount acc1;
	private List<Reservation> resList;
	@MockBean
	private PPTransactionRepository ppTransactionRepository;
	@MockBean
	private CaseRepository caseRepository;

	@Before
	public void init() {
		restTemplate = Mockito.mock(RestTemplate.class);
		responseEntityMockInt = Mockito.mock(ResponseEntity.class);
		responseEntityMockDouble = Mockito.mock(ResponseEntity.class);
		reservationHandler = new ReservationHandler(ppTransactionRepository,caseRepository,restTemplate);
		acc1 = new PPAccount("acc1", 100.0);

		resList = new ArrayList<>();
		res1 = new Reservation();
		res1.setNumber(10.0);
		res2 = new Reservation();
		res2.setNumber(10.0);
		resList.add(res1);
		resList.add(res2);

		acc1.setReservations(resList);

		Mockito.when(
				restTemplate.getForObject(ACCOUNT_URL + "/{account}", PPAccount.class, "user1"))
				.thenReturn(acc1);

		Mockito.when(restTemplate
				.exchange(RESERVATION_URL + "/reserve/{account}/{targetAccount}", HttpMethod.POST,
						new HttpEntity<>(80.0), Double.class, "user1", "user2"))
				.thenReturn(responseEntityMockDouble);

		Mockito.when(restTemplate
				.exchange(RESERVATION_URL + "/release/{account}", HttpMethod.POST,
						new HttpEntity<>(1),
						Integer.class, "user1")).thenReturn(responseEntityMockInt);

		Mockito.when(restTemplate
				.exchange(RESERVATION_URL + "/punish/{account}", HttpMethod.POST,
						new HttpEntity<>(1),
						Integer.class, "user1")).thenReturn(responseEntityMockInt);
	}

	@Test
	public void createReservationShouldReturnFalseIfSourceUserHasNoValidFounds() {
		res1.setNumber(11.0);

		Assertions.assertThat(reservationHandler.createReservation("user1", "user2", 80.0))
				.isFalse();
		Mockito.verify(restTemplate, Mockito.times(1))
				.getForObject(ACCOUNT_URL + "/{account}", PPAccount.class,
						"user1");
	}

	@Test
	public void createReservationShouldReturnTrueIfSourceUserHasValidFoundsAndHttpStatusOfExchangeWasOk() {
		Mockito.when(responseEntityMockDouble.getStatusCode()).thenReturn(HttpStatus.OK);

		Assertions.assertThat(reservationHandler.createReservation("user1", "user2", 80.0))
				.isTrue();
		Mockito.verify(restTemplate, Mockito.times(1))
				.getForObject(ACCOUNT_URL + "/{account}", PPAccount.class,
						"user1");
	}

	@Test
	public void createReservationShouldReturnTrueIfSourceUserHasValidFoundsAndHttpStatusOfExchangeWasCreated() {
		Mockito.when(responseEntityMockDouble.getStatusCode()).thenReturn(HttpStatus.CREATED);

		Assertions.assertThat(reservationHandler.createReservation("user1", "user2", 80.0))
				.isTrue();
		Mockito.verify(restTemplate, Mockito.times(1))
				.getForObject(ACCOUNT_URL + "/{account}", PPAccount.class,
						"user1");
	}

	@Test
	public void createReservationShouldReturnFalseIfSourceUserHasValidFoundsAndHttpStatusOfExchangeWasNeitherOkNorCreated() {
		Mockito.when(responseEntityMockDouble.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);

		Assertions.assertThat(reservationHandler.createReservation("user1", "user2", 80.0))
				.isFalse();
		Mockito.verify(restTemplate, Mockito.times(1))
				.getForObject(ACCOUNT_URL + "/{account}", PPAccount.class,
						"user1");
	}

	@Test
	public void releaseReservationShouldReturnTrueIfHttpStatusOfExchangeWasOk() {
		Mockito.when(responseEntityMockInt.getStatusCode()).thenReturn(HttpStatus.OK);

		Assertions.assertThat(reservationHandler.releaseReservation("user1", 1)).isTrue();
		Mockito.verify(restTemplate, Mockito.times(1))
				.exchange(RESERVATION_URL + "/release/{account}", HttpMethod.POST,
						new HttpEntity<>(1), Integer.class, "user1");
	}

	@Test
	public void releaseReservationShouldReturnTrueIfHttpStatusOfExchangeWasCreated() {
		Mockito.when(responseEntityMockInt.getStatusCode()).thenReturn(HttpStatus.CREATED);

		Assertions.assertThat(reservationHandler.releaseReservation("user1", 1)).isTrue();
		Mockito.verify(restTemplate, Mockito.times(1))
				.exchange(RESERVATION_URL + "/release/{account}", HttpMethod.POST,
						new HttpEntity<>(1), Integer.class, "user1");
	}

	@Test
	public void releaseReservationShouldReturnFalseIfHttpStatusOfExchangeWasNeitherOkNorCreated() {
		Mockito.when(responseEntityMockInt.getStatusCode()).thenReturn(HttpStatus.CONFLICT);

		Assertions.assertThat(reservationHandler.releaseReservation("user1", 1)).isFalse();
		Mockito.verify(restTemplate, Mockito.times(1))
				.exchange(RESERVATION_URL + "/release/{account}", HttpMethod.POST,
						new HttpEntity<>(1), Integer.class, "user1");
	}

	@Test
	public void punishReservationShouldReturnTrueIfHttpStatusOfExchangeWasOk() {
		Mockito.when(responseEntityMockInt.getStatusCode()).thenReturn(HttpStatus.OK);

		Assertions.assertThat(reservationHandler.punishReservation("user1", 1)).isTrue();
		Mockito.verify(restTemplate, Mockito.times(1))
				.exchange(RESERVATION_URL + "/punish/{account}", HttpMethod.POST,
						new HttpEntity<>(1), Integer.class, "user1");
	}

	@Test
	public void punishReservationShouldReturnTrueIfHttpStatusOfExchangeWasCreated() {
		Mockito.when(responseEntityMockInt.getStatusCode()).thenReturn(HttpStatus.CREATED);

		Assertions.assertThat(reservationHandler.punishReservation("user1", 1)).isTrue();
		Mockito.verify(restTemplate, Mockito.times(1))
				.exchange(RESERVATION_URL + "/punish/{account}", HttpMethod.POST,
						new HttpEntity<>(1), Integer.class, "user1");
	}

	@Test
	public void punishReservationShouldReturnFalseIfHttpStatusOfExchangeWasNeitherOkNorCreated() {
		Mockito.when(responseEntityMockInt.getStatusCode()).thenReturn(HttpStatus.CONFLICT);

		Assertions.assertThat(reservationHandler.punishReservation("user1", 1)).isFalse();
		Mockito.verify(restTemplate, Mockito.times(1))
				.exchange(RESERVATION_URL + "/punish/{account}", HttpMethod.POST,
						new HttpEntity<>(1), Integer.class, "user1");
	}
}
