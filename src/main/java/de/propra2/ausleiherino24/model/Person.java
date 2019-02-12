package de.propra2.ausleiherino24.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	User user;

	String firstName;

	String lastName;

	String contact;

}
