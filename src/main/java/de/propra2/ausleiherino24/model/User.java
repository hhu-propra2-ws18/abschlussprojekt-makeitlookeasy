package de.propra2.ausleiherino24.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="userDB")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String username;

	private String password;

	@Column(unique = true)
	private String email;

	private String role;

	@OneToMany(cascade=CascadeType.ALL)
	private List<Article> articleList;

	void addArticle(Article article){
		addArticle(article, false);
	}

	void addArticle(Article article, boolean repetition){
		if(article == null) return;
		if(articleList == null) articleList = new ArrayList<>();
		if(articleList.contains(article))
			articleList.set(articleList.indexOf(article), article);
		else
			articleList.add(article);
		if(!repetition)
			article.setOwner(this, true);
	}

	void removeArticle(Article article){
		articleList.remove(article);
		article.setOwner(null);
	}

	public User(User user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.email = user.getEmail();
		this.role = user.getRole();
	}

	//TODO: override setPassword to hash password
}
