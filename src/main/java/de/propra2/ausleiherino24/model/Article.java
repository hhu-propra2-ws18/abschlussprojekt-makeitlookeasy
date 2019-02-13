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

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	User owner;
}
