package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CaseServiceTest {
	private CaseRepository caseRepository;
	private CaseService caseService;

	@Before
	public void setUp(){
		caseRepository = new FakeCaseRepository();
		caseService = new CaseService(caseRepository);
	}

	@Test
	public void OwnerWithThreeCases(){
		Article article = null;

		caseService.addCaseForNewArticle( article, "",0,0);
		caseService.addCaseForNewArticle( article, "",0,0);
		caseService.addCaseForNewArticle( article, "",0,0);

		ArrayList<Case> cases = caseService.getAllCasesFromPersonOwner(0L);

		int i = 0;
		for (Case c :
				cases) {
			assertEquals(java.util.Optional.of(0L).get(), c.getOwner());
			i++;
		}
		assertEquals(3, i);
	}

	@Test
	public void OwnerWithThreeCases2(){
		Article article = null;

		caseService.addCaseForNewArticle(article, "",0,0);
		caseService.addCaseForNewArticle( article, "",0,0);
		caseService.addCaseForNewArticle( article, "",0,0);
		caseService.addCaseForNewArticle( article, "",0,0);
		caseService.addCaseForNewArticle( article, "",0,0);

		ArrayList<Case> cases = caseService.getAllCasesFromPersonOwner(0L);

		int i = 0;
		for (Case c :
				cases) {
			assertEquals(java.util.Optional.of(0L).get(), c.getOwner());
			i++;
		}
		assertEquals(3, i);
	}

	@Test
	public void OwnerWithZeroCases(){
		Article article = null;

		caseService.addCaseForNewArticle( article, "",0,0);
		caseService.addCaseForNewArticle( article, "",0,0);
		caseService.addCaseForNewArticle( article, "",0,0);

		ArrayList<Case> cases = caseService.getAllCasesFromPersonOwner(1L);

		assertTrue(cases.isEmpty());
	}
}
