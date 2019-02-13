package de.propra2.ausleiherino24.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="caseDB")
public class Case {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	Long startTime;

	Long endTime;

	int price;

	int deposit;

	public Boolean active;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	User receiver;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Article article;

	//Die Konstruktion ist nötig, damit der Article stets mit geupdatet wird. Analoges ist im Article
	//Siehe https://notesonjava.wordpress.com/2008/11/03/managing-the-bidirectional-relationship/
	public void setArticle(Article article){
		setArticle(article, false);
	}
	void setArticle(Article article, boolean repetition){
		this.article = article;
		if(article != null && !repetition)
			article.setACase(this, true);
	}

	public User getOwner(){
		return this.article.getOwner();
	};

}
