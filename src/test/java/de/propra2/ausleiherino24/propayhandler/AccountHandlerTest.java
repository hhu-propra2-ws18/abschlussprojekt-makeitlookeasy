package de.propra2.ausleiherino24.propayhandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
	}


	@Test
	public void hasValidFundsWorksWithoutReservations(){
		Mockito.when(restTemplate.getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, "Acc1")).thenReturn(testAcc1);
		Assert.assertTrue(accountHandler.hasValidFunds("Acc1",99));
	}


	@Ignore
	@Test
	public void transferFundsWorksWithValidFunds(){
		HttpEntity<Double> request = new HttpEntity<>(10.0);
		ResponseEntity<Double> responseEntity = new ResponseEntity<>( 200.0 ,HttpStatus.ACCEPTED);

		Mockito.when(restTemplate.getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, "Acc1")).thenReturn(testAcc1);
		Mockito.when(restTemplate.exchange(ACCOUNT_URL +"/{sourceAccount}/transfer/{targetAccount}", HttpMethod.POST, request, Double.class, "Acc1","Acc2")).thenReturn(responseEntity);

		Assert.assertEquals(200.0,accountHandler.transferFunds("Acc1","Acc2",10.0),0.05);
	}

	@Test
	public void addFundsWorks(){
		HttpEntity<Double> request = new HttpEntity<>(10.0);
		ResponseEntity<Double> responseEntity = new ResponseEntity<>( 200.0 ,HttpStatus.ACCEPTED);

		Mockito.when(restTemplate.exchange(ACCOUNT_URL +"/{account}", HttpMethod.POST, request, Double.class, "Acc1")).thenReturn(responseEntity);


		Assert.assertEquals(200.0,accountHandler.addFunds("Acc1",10.0),0.05);
	}


}
