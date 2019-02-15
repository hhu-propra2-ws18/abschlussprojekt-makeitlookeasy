package de.propra2.ausleiherino24.propayhandler;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
@RunWith(SpringRunner.class)

public class AccountHandlerTest {

	private static final String ACCOUNT_URL = "http://localhost:8888/account";

	@MockBean
	private RestTemplate restTemplate;

	private PPAccount testAcc1;
	private PPAccount testAcc2;

	@InjectMocks
	AccountHandler accountHandler;

	@Before
	public void initialize(){
		restTemplate = Mockito.mock(RestTemplate.class);
		accountHandler = new AccountHandler(restTemplate);
		testAcc1 = new PPAccount("Acc1",100.0);
		testAcc2 = new PPAccount("Acc2",1000.0);
		
		Mockito.when(restTemplate.getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, "Acc1")).thenReturn(testAcc1);
	}

	@Test
	public void checkFundsTest(){
		Mockito.when(restTemplate.getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, "Acc3")).thenReturn(null);

		Assertions.assertThat(accountHandler.checkFunds("Acc3")).isZero();
		Mockito.verify(restTemplate, Mockito.times(1)).getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, "Acc3");
	}
	
	@Test
	public void checkFundsTest2(){
		Mockito.when(restTemplate.getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, "Acc2")).thenReturn(testAcc2);

		Assertions.assertThat(accountHandler.checkFunds("Acc2")).isEqualByComparingTo(1000.0);
		Mockito.verify(restTemplate, Mockito.times(1)).getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, "Acc2");
	}

	@Test
	public void hasValidFundsWorksWithoutReservationsIfValid(){
		Assertions.assertThat(accountHandler.hasValidFunds("Acc1",99.0)).isTrue();
		Mockito.verify(restTemplate, Mockito.times(1)).getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, "Acc1");
	}

	@Test
	public void hasValidFundsFailsWithoutReservationsIfFundsNotValid(){
		Assertions.assertThat(accountHandler.hasValidFunds("Acc1",101.0)).isFalse();
		Mockito.verify(restTemplate, Mockito.times(1)).getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, "Acc1");
	}

	@Test
	public void hasValidFundsWorksWithReservationsIfValid(){
		testAcc1.addReservation(90.0);

		Assertions.assertThat(accountHandler.hasValidFunds("Acc1",9.0)).isTrue();
		Mockito.verify(restTemplate, Mockito.times(1)).getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, "Acc1");
	}

	@Test
	public void hasValidFundsFailsWithReservationsIfFundsNotValid(){
		testAcc1.addReservation(90.0);
		testAcc1.addReservation(9.0);

		Assertions.assertThat(accountHandler.hasValidFunds("Acc1",2.0)).isFalse();
		Mockito.verify(restTemplate, Mockito.times(1)).getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, "Acc1");
	}


	@Test
	public void transferFundsWorksWithValidFunds(){
		HttpEntity<Double> request = new HttpEntity<>(10.0);
		ResponseEntity<Double> responseEntity = new ResponseEntity<>( 200.0 ,HttpStatus.ACCEPTED);

		Mockito.when(restTemplate.exchange(ACCOUNT_URL +"/{sourceAccount}/transfer/{targetAccount}", HttpMethod.POST, request, Double.class,"Acc1","Acc2")).thenReturn(responseEntity);

		Assertions.assertThat(accountHandler.transferFunds("Acc1","Acc2",10.0)).isEqualByComparingTo(200.0);
		Mockito.verify(restTemplate, Mockito.times(1)).exchange(ACCOUNT_URL +"/{sourceAccount}/transfer/{targetAccount}", HttpMethod.POST, request, Double.class,"Acc1","Acc2");
	}
	
	@Test
	public void transferFundsFailsIfFundsValidButStatusCodeNotAccepted(){
		HttpEntity<Double> request = new HttpEntity<>(10.0);
		ResponseEntity<Double> responseEntity = new ResponseEntity<>( 200.0 ,HttpStatus.CONFLICT);

		Mockito.when(restTemplate.exchange(ACCOUNT_URL +"/{sourceAccount}/transfer/{targetAccount}", HttpMethod.POST, request, Double.class,"Acc1","Acc2")).thenReturn(responseEntity);

		Assertions.assertThat(accountHandler.transferFunds("Acc1","Acc2",10.0)).isEqualByComparingTo(0.0);
		Mockito.verify(restTemplate, Mockito.times(1)).exchange(ACCOUNT_URL +"/{sourceAccount}/transfer/{targetAccount}", HttpMethod.POST, request, Double.class,"Acc1","Acc2");
	}

	@Test
	public void transferFundsFailsIfFundsNotValid(){
		HttpEntity<Double> request = new HttpEntity<>(201.0);
		ResponseEntity<Double> responseEntity = new ResponseEntity<>( 200.0 ,HttpStatus.ACCEPTED);

		Mockito.when(restTemplate.exchange(ACCOUNT_URL +"/{sourceAccount}/transfer/{targetAccount}", HttpMethod.POST, request, Double.class,"Acc1","Acc2")).thenReturn(responseEntity);

		Assertions.assertThat(accountHandler.transferFunds("Acc1","Acc2",201.0)).isEqualByComparingTo(0.0);
		Mockito.verify(restTemplate, Mockito.times(0)).exchange(ACCOUNT_URL +"/{sourceAccount}/transfer/{targetAccount}", HttpMethod.POST, request, Double.class,"Acc1","Acc2");
	}

	@Test
	public void addFundsAddsFoundsIfStatusCodeIsAccepted(){
		HttpEntity<Double> request = new HttpEntity<>(10.0);
		ResponseEntity<Double> responseEntity = new ResponseEntity<>( 200.0 ,HttpStatus.ACCEPTED);

		Mockito.when(restTemplate.exchange(ACCOUNT_URL +"/{account}", HttpMethod.POST, request, Double.class, "Acc1")).thenReturn(responseEntity);

		Assertions.assertThat(accountHandler.addFunds("Acc1",10.0)).isEqualByComparingTo(200.0);
		Mockito.verify(restTemplate, Mockito.times(1)).exchange(ACCOUNT_URL +"/{account}", HttpMethod.POST, request, Double.class, "Acc1");
	}
	
	@Test
	public void addFundsAddsNoFoundsIfStatusCodeIsNotAccepted(){
		HttpEntity<Double> request = new HttpEntity<>(10.0);
		ResponseEntity<Double> responseEntity = new ResponseEntity<>( 200.0 ,HttpStatus.BAD_REQUEST);

		Mockito.when(restTemplate.exchange(ACCOUNT_URL +"/{account}", HttpMethod.POST, request, Double.class, "Acc1")).thenReturn(responseEntity);

		Assertions.assertThat(accountHandler.addFunds("Acc1",10.0)).isEqualByComparingTo(0.0);
		Mockito.verify(restTemplate, Mockito.times(1)).exchange(ACCOUNT_URL +"/{account}", HttpMethod.POST, request, Double.class, "Acc1");
	}
	


}
