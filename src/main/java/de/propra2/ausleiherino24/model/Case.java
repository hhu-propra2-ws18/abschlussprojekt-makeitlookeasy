package de.propra2.ausleiherino24.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Case {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	String title;

	Long startTime;

	Long endTime;

	int price;

	int deposit;

	public Boolean active;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	User receiver;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Article article;

	public Case(){}

	public Case(Long id, String title, Long startTime, Long endTime, int price, int deposit, Boolean active, User receiver, Article article) {
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.price = price;
		this.deposit = deposit;
		this.active = active;
		this.receiver = receiver;
		this.article = article;
	}

	public User getOwner(){
		return this.article.getOwner();
	};

}
