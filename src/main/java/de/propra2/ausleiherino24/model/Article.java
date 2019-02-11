package de.propra2.ausleiherino24.model;

import lombok.Data;

import javax.persistence.Id;

@Data
public class Article {
	
	@Id
	Long id;
	
	String name;
	String description;
}
