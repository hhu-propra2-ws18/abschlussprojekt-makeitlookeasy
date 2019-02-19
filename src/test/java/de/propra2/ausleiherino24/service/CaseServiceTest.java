package de.propra2.ausleiherino24.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.Person;
import de.propra2.ausleiherino24.model.User;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class CaseServiceTest {

	private CaseRepository caseRepositoryMock;
	private PersonRepository personRepositoryMock;
	private CaseService caseService;
	private ArrayList<Case> cases;

	@Before
	public void setUp() {
		caseRepositoryMock = mock(CaseRepository.class);
		personRepositoryMock = mock(PersonRepository.class);
		caseService = new CaseService(caseRepositoryMock, personRepositoryMock, null);
		cases = new ArrayList<>();
	}

	@Test
	public void ownerWithThreeCases() {
		cases.add(new Case(0L, null, null, 0, 0,null, null ,null,null, false));
		cases.add(new Case(0L, null, null, 0, 0, null, null,null, null, false));
		cases.add(new Case(0L, null, null, 0, 0, null, null, null,null, false));

		when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
		Optional<Person> o = Optional.of(new Person());
		when(personRepositoryMock.findById(0L)).thenReturn(o);

		assertEquals(cases, caseService.getAllCasesFromPersonOwner(0L));
	}

	@Test
	public void ownerWithThreeCases2() {
		cases.add(new Case(0L, null, null, 0, 0, null,null, null,null, false));
		cases.add(new Case(0L, null, null, 0, 0, null,null, null,null, false));
		cases.add(new Case(1L, null, null, 0, 0, null, null,null,null, false));
		cases.add(new Case(0L, null, null, 0, 0, null, null,null,null, false));

		when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
		Optional<Person> o = Optional.of(new Person());
		when(personRepositoryMock.findById(0L)).thenReturn(o);
		cases.remove(2);

		assertEquals(cases, caseService.getAllCasesFromPersonOwner(0L));
	}

	@Test
	public void ownerWithTwoLendCases() {
		cases.add(new Case(0L, null, null, 0, 0,null, null,null, null, false));
		cases.add(new Case(0L, null, null, 0, 0, null,new User(),null, null, false));
		cases.add(new Case(0L, null, null, 0, 0, null,null,null, null, false));
		cases.add(new Case(0L, null, null, 0, 0, null,new User(),null, null, false));

		when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
		Optional<Person> o = Optional.of(new Person());
		when(personRepositoryMock.findById(0L)).thenReturn(o);
		cases.remove(2);
		cases.remove(0);

		assertEquals(cases, caseService.getLendCasesFromPersonOwner(0L));
	}

	@Test
	public void ownerWithNoLendCases() {
		cases.add(new Case(0L, null, null, 0, 0, null,null, null,null, false));
		cases.add(new Case(0L, null, null, 0, 0, null,null, null,null, false));
		cases.add(new Case(0L, null, null, 0, 0, null,null,null, null, false));

		when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
		Optional<Person> o = Optional.of(new Person());
		when(personRepositoryMock.findById(0L)).thenReturn(o);

		assertTrue(caseService.getLendCasesFromPersonOwner(0L).isEmpty());
	}

	@Test
	public void ownerWithTwoFreeCases() {
		cases.add(new Case(0L, null, null, 0, 0, null,null,null, null, false));
		cases.add(new Case(0L, null, null, 0, 0, null,new User(),null, null, false));
		cases.add(new Case(0L, null, null, 0, 0,null, null,null, null, false));
		cases.add(new Case(0L, null, null, 0, 0, null,new User(),null, null, false));

		when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
		Optional<Person> o = Optional.of(new Person());
		when(personRepositoryMock.findById(0L)).thenReturn(o);
		cases.remove(3);
		cases.remove(1);

		assertEquals(cases, caseService.getFreeCasesFromPersonOwner(0L));
	}

	@Test
	public void ownerWithNoFreeCases() {
		cases.add(new Case(0L, null, null, 0, 0, null,new User(), null,null, false));
		cases.add(new Case(0L, null, null, 0, 0, null,new User(), null,null, false));
		cases.add(new Case(0L, null, null, 0, 0, null,new User(), null,null, false));

		when(caseRepositoryMock.findAllByArticleOwner(null)).thenReturn(cases);
		Optional<Person> o = Optional.of(new Person());
		when(personRepositoryMock.findById(0L)).thenReturn(o);

		assertTrue(caseService.getFreeCasesFromPersonOwner(0L).isEmpty());
	}

	@Test
	public void saveNewArticleCase(){
		Article article = new Article();
		Case c = new Case();
		c.setArticle(article);
		c.setPrice(0);
		c.setDeposit(10);

		caseService.addCaseForNewArticle(article, 0, 10);

		verify(caseRepositoryMock).save(c);
	}

	@Test
	public void lendOneArticle(){
		Case c = new Case();
		when(caseRepositoryMock.findById(0L)).thenReturn(Optional.of(c));
		User user = new User();
		c.setReceiver(user);
		c.setStartTime(0L);
		c.setEndTime(10L);
		ArgumentCaptor<Case> argument = ArgumentCaptor.forClass(Case.class);

		caseService.lendArticleToPerson(0L,user, 0L, 10L);

		verify(caseRepositoryMock).save(argument.capture());
		assertEquals(argument.getValue().getReceiver(), user);
		assertTrue(argument.getValue().getStartTime().equals(0L));
		assertTrue(argument.getValue().getEndTime().equals(10L));
	}

	@Test
	public void lendNotExistingArticle(){
		when(caseRepositoryMock.findById(0L)).thenReturn(Optional.empty());

		caseService.lendArticleToPerson(0L, new User(), 0L, 10L);

		verify(caseRepositoryMock, times(0)).save(any());
		verify(caseRepositoryMock, times(1)).findById(0L);
	}
}
