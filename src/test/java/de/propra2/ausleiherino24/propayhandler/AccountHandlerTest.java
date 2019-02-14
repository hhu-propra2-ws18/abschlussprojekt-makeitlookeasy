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
		testAcc1 = new PPAccount("Acc1",100);
		testAcc2 = new PPAccount("Acc2",1000);
	}


	@Test
	public void hasValidFundsWorksWithoutReservations(){
		String accountName = "Acc1";
		Mockito.when(restTemplate.getForObject(ACCOUNT_URL +"/{account}", PPAccount.class, accountName)).thenReturn(testAcc1);
		Assert.assertTrue(accountHandler.hasValidFunds("Acc1",99));
	}



	@Ignore
	@Test
	public void transferFundsWorksWithValidFunds(){
		boolean result = accountHandler.transferFunds("Acc1","Acc2",10);

		Assert.assertTrue(result);
	}

	@Ignore
	@Test
	public void addFundsWorks(){
		//TODO: fixTest
		String username = "Acc1";
		HttpEntity<Double> request = new HttpEntity<>(100.0);
		ResponseEntity<Double> responseEntity = new ResponseEntity<>( 200.0 ,HttpStatus.ACCEPTED);
		Mockito.when(restTemplate.exchange(ACCOUNT_URL +"/{account}", HttpMethod.POST, request, Double.class, username)).thenReturn(responseEntity);


		Assert.assertEquals(200.0,accountHandler.addFunds("Acc1",10.0),0.05);
	}


}
