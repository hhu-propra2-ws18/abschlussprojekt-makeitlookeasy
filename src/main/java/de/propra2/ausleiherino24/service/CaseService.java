package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class CaseService {
	private final CaseRepository caseRepository;

	@Autowired
	public CaseService(CaseRepository caseRepository) {
		this.caseRepository = caseRepository;
	}

	//Fügt einen Artikel einer Person hinzu, welcher frei zum Verleih ist
	public void addCaseForNewArticle(Article article, String title, int price, int deposit){
		Case c = new Case();
		c.setArticle(article);
		c.setDeposit(deposit);
		c.setPrice(price);
		c.setTitle(title);

		caseRepository.save(c);
	}

	//Gibt alle Cases zurück, wo der User der Verleihende ist
	public ArrayList<Case> getAllCasesFromPersonOwner(Long userId){
		return caseRepository.findByOwner(userId);
	}

	//Gibt alle Cases zurück, wo der User der Verleihende ist und der Artikel momentan verliehen ist
	public ArrayList<Case> getLendCasesFromPersonOwner(Long userId){
		ArrayList<Case> cases = getAllCasesFromPersonOwner(userId);
		return cases.stream()
				.filter(c -> c.getReceiver() != null)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	//Gibt alle Cases zurück, wo der User der Verleihende ist und der Artikel momentan nicht verliehen ist
	public ArrayList<Case> getFreeCasesFromPersonOwner(Long userId){
		ArrayList<Case> cases = getAllCasesFromPersonOwner(userId);
		return cases.stream()
				.filter(c -> c.getReceiver() == null)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	//Gibt alle Cases zurück, wo der User sich von jemanden etwas geliehen hat
	public ArrayList<Case> getLendCasesFromPersonReceiver(Long userId){
		return caseRepository.findByReceiver(userId);
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

}
