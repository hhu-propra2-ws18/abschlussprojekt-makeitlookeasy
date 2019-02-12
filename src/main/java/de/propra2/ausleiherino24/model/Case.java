package de.propra2.ausleiherino24.model;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToOne;

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

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	User owner;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	User receiver;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Article article;

}
