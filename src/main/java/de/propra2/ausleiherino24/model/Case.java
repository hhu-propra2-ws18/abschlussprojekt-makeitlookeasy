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
	User owner;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	User receiver;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Article article;

}
