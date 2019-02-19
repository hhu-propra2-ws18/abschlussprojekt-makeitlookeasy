package de.propra2.ausleiherino24.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
public class Conflict {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotNull
	Case conflictedCase;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotNull
	User conflictReporter;

	@NotNull
	@Size(min = 15, max = 2048)
	String conflictDescription;
}
