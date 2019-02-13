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

	String description;

	public Boolean active;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	User owner;
}
