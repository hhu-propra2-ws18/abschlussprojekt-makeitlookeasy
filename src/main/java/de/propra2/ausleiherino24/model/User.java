package de.propra2.ausleiherino24.model;

import lombok.Data;

import javax.persistence.Id;

@Data
public class User {
	
	@Id
	Long id;
	
	String username;
	
	String first;
	String last;
	
}
