package de.propra2.ausleiherino24.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	String username;
	
	String password;

	String email;
}
