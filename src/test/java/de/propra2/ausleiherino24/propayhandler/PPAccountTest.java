package de.propra2.ausleiherino24.propayhandler;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
public class PPAccountTest {
	private PPAccount acc;
	
	@Before
	public void init() {
		acc = new PPAccount("Acc1", 20.0);
	}
	
	@Test
	public void getAmountShouldReturnCorrectAmountIfAmountIsNotNull() {
		Assertions.assertThat(acc.getAmount()).isEqualByComparingTo(20.0);
	}
	
	@Test
	public void getAmountShouldReturnZeroIfAmountIsNull() {
		acc.setNumber(null);
		
		Assertions.assertThat(acc.getAmount()).isEqualByComparingTo(0.0);
	}
	
	@Test
	public void getReservationsShouldReturnCorrectListOfReservationsIfReservationsIsNotNull() {
		Reservation res1 = new Reservation();
		res1.setId(1);
		Reservation res2 = new Reservation();
		res2.setId(2);
		List<Reservation> resList = new ArrayList<>();
		resList.add(res1);
		resList.add(res2);
		acc.setReservations(resList);
		
		Assertions.assertThat(acc.getReservations()).isEqualTo(resList);
	}
	
	@Test
	public void getReservationsShouldReturnInitializedListOfReservationsIfReservationsIsNull() {
		Assertions.assertThat(acc.getReservations()).isEqualTo(new ArrayList<>());
	}

}
