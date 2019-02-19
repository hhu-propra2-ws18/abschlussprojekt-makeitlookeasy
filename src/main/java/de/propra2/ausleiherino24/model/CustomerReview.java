package de.propra2.ausleiherino24.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReview {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotNull
	Case aCase;

	String description;

	Long timestamp;
}
