package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class CaseServiceTest {
	private CaseRepository caseRepositoryMock;
	private CaseService caseService;
	private ArrayList<Case> cases;

	@Before
	public void setUp(){
		//caseRepository = new FakeCaseRepository();
		caseRepositoryMock = mock(CaseRepository.class);
		caseService = new CaseService(caseRepositoryMock);
		cases = new ArrayList<>();
	}

	@Test
	public void OwnerWithThreeCases(){
		Article article = null;
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));

		when(caseRepositoryMock.findByOwner(0L)).thenReturn(cases);

		assertEquals(cases, caseService.getAllCasesFromPersonOwner(0L));
	}

	@Test
	public void OwnerWithThreeCases2(){
		Article article = null;
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(1L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));

		when(caseRepositoryMock.findByOwner(0L)).thenReturn(cases);
		cases.remove(2);

		assertEquals(cases, caseService.getAllCasesFromPersonOwner(0L));
	}

	@Test
	public void OwnerWithZeroCases(){
		Article article = null;
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));

		when(caseRepositoryMock.findByOwner(1L)).thenReturn(cases);

		assertTrue(caseService.getAllCasesFromPersonOwner(0L).isEmpty());
	}

	@Test
	public void OwnerWithTwoLendCases(){
		Article article = null;
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, new User(), article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, new User(), article));

		when(caseRepositoryMock.findByOwner(0L)).thenReturn(cases);
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

		when(caseRepositoryMock.findByOwner(0L)).thenReturn(cases);

		assertTrue(caseService.getLendCasesFromPersonOwner(0L).isEmpty());
	}

	@Test
	public void OwnerWithTwoFreeCases(){
		Article article = null;
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, new User(), article));
		cases.add(new Case(0L, "", null, null, 0,0, false, null, article));
		cases.add(new Case(0L, "", null, null, 0,0, false, new User(), article));

		when(caseRepositoryMock.findByOwner(0L)).thenReturn(cases);
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

		when(caseRepositoryMock.findByOwner(0L)).thenReturn(cases);

		assertTrue(caseService.getFreeCasesFromPersonOwner(0L).isEmpty());
	}
}
