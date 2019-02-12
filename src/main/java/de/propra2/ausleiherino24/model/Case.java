package de.propra2.ausleiherino24.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToOne;
import java.util.function.LongFunction;

@Data
@Entity
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
	Long owner;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Long receiver;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	Article article;

}
