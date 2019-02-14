package de.propra2.ausleiherino24.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * User hat neben Person eine eigene ID, um diesen als Plattformbenutzer explizit separat ansteuern zu können.
 */
@Data
@Entity
@Table(name="userDB")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String username;

	private String password;

	@Column(unique = true)
	private String email;

	private String role;

	public User(User user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.email = user.getEmail();
		this.role = user.getRole();
	}

	//TODO: override setPassword to hash password
}
