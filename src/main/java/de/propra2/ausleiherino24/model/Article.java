package de.propra2.ausleiherino24.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Article {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	String name;

	@Lob
	String description;

	// If this is true the article is not available for rental ("deleted")
	Boolean active;

	Boolean reserved;

	Category category;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	User owner;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Case aCase;


	//Die Konstruktion ist nötig, damit der Case stets mit geupdatet wird. Analoges ist im Case
	//Siehe https://notesonjava.wordpress.com/2008/11/03/managing-the-bidirectional-relationship/
	public void setACase(Case aCase){
		setACase(aCase, false);
	}
	void setACase(Case aCase, boolean repetition){
		this.aCase = aCase;
		if(aCase != null && !repetition)
			aCase.setArticle(this, true);
	}

	public void setOwner(User user){
		setOwner(user, false);
	}
	void setOwner(User user, boolean repetition){
		this.owner = user;
		if(user != null && !repetition) {
			user.addArticle(this, true);
		}
	}

	public Article(){}

	public Article(Long id, String name, String description, Boolean active, Boolean reserved, User owner) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.active = active;
		this.reserved = reserved;
		this.owner = owner;
	}
}
