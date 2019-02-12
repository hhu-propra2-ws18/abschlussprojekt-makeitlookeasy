package de.propra2.ausleiherino24.service;

import de.propra2.ausleiherino24.data.CaseRepository;
import de.propra2.ausleiherino24.model.Article;
import de.propra2.ausleiherino24.model.Case;
import de.propra2.ausleiherino24.model.User;
import org.springframework.beans.factory.annotation.Autowired;

public class CaseService {
	private CaseRepository caseRepository;

	@Autowired
	public CaseService(CaseRepository caseRepository){
		this.caseRepository = caseRepository;
	}

	public void addArticleToLend(User owner, Article article, String title, Long starttime, Long endtime, int price, int deposit){
		Case c = new Case();
		c.setArticle(article);
		c.setDeposit(deposit);
		c.setEndTime(endtime);
		c.setOwner(owner);
		c.setPrice(price);
		c.setStartTime(starttime);
		c.setTitle(title);

		caseRepository.save(c);
	}
}
