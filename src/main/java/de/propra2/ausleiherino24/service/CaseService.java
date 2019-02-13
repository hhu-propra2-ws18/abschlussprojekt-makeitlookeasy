package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.data.PersonRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CaseService {
	private final CaseRepository caseRepository;
	private final PersonRepository personRepository;
	private final ArticleService articleService;

	@Autowired
	public CaseService(CaseRepository caseRepository, PersonRepository personRepository, ArticleService articleService) {
		this.caseRepository = caseRepository;
		this.personRepository = personRepository;
		this.articleService = articleService;
	}

	//Fügt einen Artikel, welcher frei zum Verleih ist, von einer Person hinzu
	public void addCaseForNewArticle(Article article, int price, int deposit){
		Case c = new Case();
		c.setArticle(article);
		c.setDeposit(deposit);
		c.setPrice(price);

		caseRepository.save(c);
	}

	//Gibt alle Cases zurück, wo die Person der Verleihende ist
	public ArrayList<Case> getAllCasesFromPersonOwner(Long personId){
		return caseRepository.findByOwner(personRepository.findById(personId).get().getUser());
	}

	//Gibt alle Cases zurück, wo die Person der Verleihende ist und der Artikel momentan verliehen ist
	public ArrayList<Case> getLendCasesFromPersonOwner(Long personId){
		ArrayList<Case> cases = getAllCasesFromPersonOwner(personId);
		return cases.stream()
				.filter(c -> c.getReceiver() != null)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	//Gibt alle Cases zurück, wo die Person der Verleihende ist und der Artikel momentan nicht verliehen ist
	public ArrayList<Case> getFreeCasesFromPersonOwner(Long personId){
		ArrayList<Case> cases = getAllCasesFromPersonOwner(personId);
		return cases.stream()
				.filter(c -> c.getReceiver() == null)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	//Gibt alle Cases zurück, wo die Person sich von jemanden etwas geliehen hat
	public ArrayList<Case> getLendCasesFromPersonReceiver(Long personId){
		return caseRepository.findByReceiver(personRepository.findById(personId).get().getUser());
	}

	//Erwartet Case mit wo Artikel verliehen werden kann. Case wird modifiziert, dass es nun verliehen ist.
	public void lendArticleToPerson(Long caseId, User receiver, Long starttime, Long endtime) {
		if(!(caseRepository.findById(caseId).isPresent())) return;

		Case c = caseRepository.findById(caseId).get();
		c.setReceiver(receiver);
		c.setStartTime(starttime);
		c.setEndTime(endtime);

		caseRepository.save(c);
	}

	public List<Case> findAllCasesWithNonActiveArticles(){
		return articleService.getAllNonActiveArticles().stream()
				.map(caseRepository::findByArticle)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toCollection(ArrayList::new));
	}

}
