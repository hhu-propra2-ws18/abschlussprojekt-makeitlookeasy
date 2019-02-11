package de.propra2.ausleiherino24.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Case {

	@Id
	Long id;

	User owner;
	User receiver;

	String title;

	Long starttime;
	Long endtime;

	Article article;
	int price;
	int deposit;
}
