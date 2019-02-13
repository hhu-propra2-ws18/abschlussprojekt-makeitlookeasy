package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CaseServiceTest {
	private CaseRepository caseRepositoryMock;
	private PersonRepository personRepositoryMock;
	private CaseService caseService;
	private ArrayList<Case> cases;

	@Before
	public void setUp(){
		caseRepositoryMock = mock(CaseRepository.class);
		personRepositoryMock = mock(PersonRepository.class);
		caseService = new CaseService(caseRepositoryMock, personRepositoryMock);
		cases = new ArrayList<>();
	}

	@Test
	public void OwnerWithThreeCases(){
		Article article = null;
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));

		when(caseRepositoryMock.findByOwner(null)).thenReturn(cases);
		Optional<Person> o = Optional.of(new Person());
		when(personRepositoryMock.findById(0L)).thenReturn(o);

		assertEquals(cases, caseService.getAllCasesFromPersonOwner(0L));
	}

	@Test
	public void OwnerWithThreeCases2(){
		Article article = null;
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(1L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));

		when(caseRepositoryMock.findByOwner(null)).thenReturn(cases);
		Optional<Person> o = Optional.of(new Person());
		when(personRepositoryMock.findById(0L)).thenReturn(o);
		cases.remove(2);

		assertEquals(cases, caseService.getAllCasesFromPersonOwner(0L));
	}

	@Test
	public void OwnerWithTwoLendCases(){
		Article article = null;
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, new User(), article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, new User(), article));

		when(caseRepositoryMock.findByOwner(null)).thenReturn(cases);
		Optional<Person> o = Optional.of(new Person());
		when(personRepositoryMock.findById(0L)).thenReturn(o);
		cases.remove(2);
		cases.remove(0);

		assertEquals(cases, caseService.getLendCasesFromPersonOwner(0L));
	}

	@Test
	public void OwnerWithNoLendCases(){
		Article article = null;
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));

		when(caseRepositoryMock.findByOwner(null)).thenReturn(cases);
		Optional<Person> o = Optional.of(new Person());
		when(personRepositoryMock.findById(0L)).thenReturn(o);

		assertTrue(caseService.getLendCasesFromPersonOwner(0L).isEmpty());
	}

	@Test
	public void OwnerWithTwoFreeCases(){
		Article article = null;
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, new User(), article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, new User(), article));

		when(caseRepositoryMock.findByOwner(null)).thenReturn(cases);
		Optional<Person> o = Optional.of(new Person());
		when(personRepositoryMock.findById(0L)).thenReturn(o);
		cases.remove(3);
		cases.remove(1);

		assertEquals(cases, caseService.getFreeCasesFromPersonOwner(0L));
	}

	@Test
	public void OwnerWithNoFreeCases(){
		Article article = null;
		cases.add(new Case(0L, "", null, null, 0,0, false, new User(), article));
		cases.add(new Case(0L, "", null, null, 0,0, false, new User(), article));
		cases.add(new Case(0L, "", null, null, 0,0, false, new User(), article));

		when(caseRepositoryMock.findByOwner(null)).thenReturn(cases);
		Optional<Person> o = Optional.of(new Person());
		when(personRepositoryMock.findById(0L)).thenReturn(o);

		assertTrue(caseService.getFreeCasesFromPersonOwner(0L).isEmpty());
	}
}
